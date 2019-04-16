package org.openpaas.paasta.marketplace.api.service;

import org.cloudfoundry.client.v2.applications.ListApplicationsRequest;
import org.cloudfoundry.client.v2.applications.ListApplicationsResponse;
import org.cloudfoundry.client.v2.routemappings.CreateRouteMappingRequest;
import org.cloudfoundry.client.v2.routemappings.CreateRouteMappingResponse;
import org.cloudfoundry.client.v2.routes.CreateRouteRequest;
import org.cloudfoundry.client.v2.routes.CreateRouteResponse;
import org.cloudfoundry.client.v3.*;
import org.cloudfoundry.client.v3.applications.*;
import org.cloudfoundry.client.v3.builds.CreateBuildRequest;
import org.cloudfoundry.client.v3.builds.CreateBuildResponse;
import org.cloudfoundry.client.v3.builds.GetBuildRequest;
import org.cloudfoundry.client.v3.builds.GetBuildResponse;
import org.cloudfoundry.client.v3.packages.*;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.openpaas.paasta.marketplace.api.common.Common;
import org.openpaas.paasta.marketplace.api.common.CommonService;
import org.openpaas.paasta.marketplace.api.model.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.FileSystems;
import java.time.Duration;
import java.util.Map;


/**
 * App Service
 *
 * @author hrjin
 * @version 1.0
 * @since 2019-01-15
 */
@Service
public class AppService extends Common{
    private static final Logger LOGGER = LoggerFactory.getLogger(AppService.class);

    @Value("${local.uploadPath}")
    private String localUploadPath;

    @Autowired
    CommonService commonService;


    /**
     * 관리자 계정으로 Application 목록 조회.
     *
     * @return ListApplicationsResponse
     */
    public ListApplicationsResponse getAppsList() {
        ReactorCloudFoundryClient cloudFoundryClient = Common.cloudFoundryClient(connectionContext(), tokenProvider(getToken()));

        // Todo ::: organizationId 와 spaceId 는 추후 변수로 넣어줄 예정.
        ListApplicationsResponse listApplicationsResponse = cloudFoundryClient.applicationsV2().list(ListApplicationsRequest.builder().organizationId("9cffc61f-370b-4bbf-b091-45d937802d88").spaceId("05d7f1f8-686f-43d5-aa1a-01693733e36c").build()).block();
        LOGGER.info("어떻게 나오니? 앱 리스트야? " + listApplicationsResponse.toString());

        return listApplicationsResponse;
    }


    /**
     * Application 생성.
     *
     * @param app the app
     * @return App
     */
    public App createApp(App app){

        // 1. Find the GUID of your space and set an APP_NAME
        // ★ 2. Create an empty app
        // ★ 3. Create an empty package for the app
        // 4. If your package is type buildpack, create a ZIP file of your application (zip -r my-app.zip *)
        // ★ 5. If your package is type buildpack, upload your bits to your new package
        // ★ 6. Stage your package and create a build
        // 7. Wait for the state of the new build to reach STAGED (watch cf curl /v3/builds/$BUILD_GUID)
        // ★ 8. Get the droplet corresponding to the staged build
        // ★ 9. Assign the droplet to the app
        // ★ 10. Create a route
        // ★ 11. Map the route to your app
        // ★ 12. Start your app


        String appGuid = getAppGuid(app);
        LOGGER.info("appGuid = " + appGuid);

        String packageGuid = getPackageGuid(appGuid);
        LOGGER.info("packageGuid = " + packageGuid);

        uploadPackage(packageGuid);

        String buildGuid = null;

        while(buildGuid == null){

            try{
                buildGuid = getBuildGuid(packageGuid).getId();
            }catch (NullPointerException e){
                System.out.println("null 이야 임뫄~~~");
            }

            if(buildGuid != null){
                LOGGER.info("buildGuid = " + buildGuid);
                break;
            }
        }


        // droplet guid
        String dropletGuid = null;

        while(dropletGuid == null){

            try{
                dropletGuid = getDropletGuid(buildGuid).getDroplet().getId();
            }catch (NullPointerException e){
                System.out.println("null 이야 임뫄~~~");
            }

            if(dropletGuid != null){
                assignDroplet(appGuid, dropletGuid);
                break;
            }
        }


        CreateRouteResponse route = createRoute(app);

        routeMapping(appGuid, route);


        // Start your app
        StartApplicationResponse startApplicationResponse = Common.cloudFoundryClient(connectionContext(), tokenProvider(getToken())).applicationsV3()
                .start(StartApplicationRequest.builder()
                        .applicationId(appGuid).build()).block();


        Map resultMap = objectMapper.convertValue(startApplicationResponse, Map.class);

        return commonService.setResultObject(resultMap, App.class);
    }




    private String getAppGuid(App app){
        // Create an empty app (POST /v3/apps)
        CreateApplicationResponse appResponse = Common.cloudFoundryClient(connectionContext(), tokenProvider(getToken())).applicationsV3()
                .create(CreateApplicationRequest.builder()
                        .name(app.getName())
                        .relationships(ApplicationRelationships.builder()
                                .space(ToOneRelationship.builder()
                                        .data(Relationship.builder()
                                                .id(app.getSpaceGuid())
                                                .build())
                                        .build())
                                .build())
                        .build()).block();
        LOGGER.info("빈 app 생성~~");
        return appResponse.getId();
    }

    private String getPackageGuid(String appGuid){
        // Create an empty package for the app (POST /v3/packages)
        CreatePackageResponse packageResponse = Common.cloudFoundryClient(connectionContext(), tokenProvider(getToken())).packages()
                .create(CreatePackageRequest.builder()
                        .type(PackageType.BITS)
                        .relationships(PackageRelationships.builder()
                                .application(ToOneRelationship.builder()
                                        .data(Relationship.builder()
                                                .id(appGuid)
                                                .build())
                                        .build())
                                .build())
                        .build()
                ).block();
        LOGGER.info("package 생성~~");
        return packageResponse.getId();
    }

    private void uploadPackage(String packageGuid){
        // If your package is type buildpack, upload your bits to your new package (POST /v3/packages/:guid/upload)
        UploadPackageResponse uploadPackageResponse = Common.cloudFoundryClient(connectionContext(), tokenProvider(getToken())).packages()
                .upload(UploadPackageRequest.builder()
                        .packageId(packageGuid)
                        .bits(FileSystems.getDefault().getPath(localUploadPath,"php-sample.zip"))
                        //.bits(new ClassPathResource(localUploadPath + "/php-sample.zip").getFile().toPath())
                        .build()).blockOptional(Duration.ofSeconds(60)).get();
        LOGGER.info("package 업로드~~");
    }

    private CreateBuildResponse getBuildGuid(String packageGuid) {
        // Stage your package and create a build (POST /v3/builds)
        CreateBuildResponse createBuildResponse = Common.cloudFoundryClient(connectionContext(), tokenProvider(getToken())).builds()
                .create(CreateBuildRequest.builder()
                        .getPackage(Relationship.builder()
                                .id(packageGuid).build())
                        .lifecycle(Lifecycle.builder()
                                .type(LifecycleType.BUILDPACK)
                                .data(BuildpackData.builder()
                                        .buildpacks("php_buildpack")
                                        .stack("cflinuxfs2")
                                        .build()).build())
                        .build()).blockOptional(Duration.ofSeconds(60)).get();
        LOGGER.info("Build 생성~~");
        return createBuildResponse;
    }

    private GetBuildResponse getDropletGuid(String buildGuid) {
        // Get the droplet corresponding to the staged build
        GetBuildResponse getBuildResponse = Common.cloudFoundryClient(connectionContext(), tokenProvider(getToken())).builds()
                .get(GetBuildRequest.builder()
                        .buildId(buildGuid)
                        .build()).blockOptional(Duration.ofSeconds(60)).get();

        LOGGER.info("droplet 조회~~");
        return getBuildResponse;
    }


    private void assignDroplet(String appGuid, String dropletGuid) {
        LOGGER.info("appGuid :::: " + appGuid + " & dropletGuid :::: " + dropletGuid);
        // Assign the droplet to the app (PATCH /v3/apps/:guid/relationships/current_droplet)
        SetApplicationCurrentDropletResponse currentDropletResponse = Common.cloudFoundryClient(connectionContext(), tokenProvider(getToken())).applicationsV3()
                .setCurrentDroplet(SetApplicationCurrentDropletRequest.builder()
                        .applicationId(appGuid)
                        .data(Relationship.builder()
                                .id(dropletGuid).build())
                        .build()).block();
        LOGGER.info("droplet assign~~");
    }

    private CreateRouteResponse createRoute(App app) {
        // Create a route
        CreateRouteResponse createRouteResponse = Common.cloudFoundryClient(connectionContext(), tokenProvider(getToken())).routes()
                .create(CreateRouteRequest.builder()
                        .host(app.getHostName())
                        .domainId(app.getDomainId())
                        .spaceId(app.getSpaceGuid())
                        .build()).block();
        LOGGER.info("라우트 생성~~");
        return createRouteResponse;
    }

    private void routeMapping(String appGuid, CreateRouteResponse route) {
        // Map the route to your app
        CreateRouteMappingResponse createRouteMappingResponse = Common.cloudFoundryClient(connectionContext(), tokenProvider(getToken())).routeMappings()
                .create(CreateRouteMappingRequest.builder()
                        .applicationId(appGuid)
                        .routeId(route.getMetadata().getId())
                        .build()).block();
    }

    //    @Autowired
//    private PropertyService propertyService;
//
//    @Autowired
//    @Qualifier("portalRest")
//    private RestTemplate portalRest;
//
//
//    /**
//     * Application 실행
//     *
//     * @param app the app
//     * @return Map
//     */
//    public Map startApp(App app) {
//        return portalRest.postForObject(propertyService.getPortalApiUrl() + Constants.V3_URL + "/apps/startApp", app, Map.class);
//    }
}
