package org.openpaas.paasta.marketplace.api.service.cloudfoundry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.cloudfoundry.client.v2.applications.*;
import org.cloudfoundry.client.v2.routemappings.CreateRouteMappingRequest;
import org.cloudfoundry.client.v2.routemappings.ListRouteMappingsRequest;
import org.cloudfoundry.client.v2.routemappings.ListRouteMappingsResponse;
import org.cloudfoundry.client.v2.routes.CreateRouteRequest;
import org.cloudfoundry.client.v2.routes.DeleteRouteRequest;
import org.cloudfoundry.doppler.Envelope;
import org.cloudfoundry.doppler.RecentLogsRequest;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.doppler.ReactorDopplerClient;
import org.javaswift.joss.model.Container;
import org.javaswift.joss.model.StoredObject;
import org.openpaas.paasta.marketplace.api.cloudFoundryModel.App;
import org.openpaas.paasta.marketplace.api.cloudFoundryModel.NameType;
import org.openpaas.paasta.marketplace.api.config.common.Common;
import org.openpaas.paasta.marketplace.api.domain.Instance;
import org.openpaas.paasta.marketplace.api.domain.Software;
import org.openpaas.paasta.marketplace.api.exception.PlatformException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author hrjin
 * @version 1.0
 * @since 2019-08-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AppService extends Common {

    @Value("${market.org.guid}")
    public String marketOrgGuid;

    @Value("${market.space.guid}")
    public String marketSpaceGuid;

    @Value("${market.naming-type}")
    public NameType localNamingType;

    @Value("${market.domain_guid}")
    public String marketDomainGuid;

    public static final int ONE_SECOND = 1000;

    @Autowired
    private final Container container;

    /**
     * 앱 목록 조회
     *
     * @param orgid
     * @param spaceid
     * @return
     */
    public ListApplicationsResponse getAppList(String orgid, String spaceid) throws IOException {
        ListApplicationsResponse listApplicationsResponse = cloudFoundryClient(tokenProvider()).applicationsV2().list(ListApplicationsRequest.builder().organizationId(orgid).spaceId(spaceid).build()).block();
        return listApplicationsResponse;
    }

    /**
     * 앱 생성
     *
     * @param param
     * @param name
     * @return
     */
    public Map<String, Object> createApp(Software param, String name, String requestedMemorySize, String requestedDiskSize) {
        ReactorCloudFoundryClient reactorCloudFoundryClient = cloudFoundryClient(tokenProvider());
        String applicationid = "applicationID";
        String routeid = "route ID";
        File file = null;
        App app = new App();

        try {

            Map parsingEnv = createManifestFile(param);
            List applications = (List) parsingEnv.get("applications");

            Object propertyResult = applications.get(0);
            LinkedHashMap<String, ?> resultMap = (LinkedHashMap<String, String>) propertyResult;

            Integer memorySize;
            Integer instance = null;
            Integer diskSize;
            String buildPack = null;

//            if(resultMap.containsKey("memory")){
//                memorySize = Integer.valueOf(resultMap.get("memory").toString().replaceAll("[^0-9]", ""));
//
//                if(resultMap.get("memory").toString().toLowerCase().contains("g")){
//                    memorySize = memorySize * 1024;
//                }
//            }
//
//            if(resultMap.containsKey("disk_quota")){
//                diskSize = Integer.valueOf(resultMap.get("disk_quota").toString().replaceAll("[^0-9]", ""));
//
//                if(resultMap.get("disk_quota").toString().toLowerCase().contains("g")){
//                    diskSize = diskSize * 1024;
//                }
//            }

            String reqExp = "\\s*[a-zA-Z]+";
            String[] aa = requestedMemorySize.split(reqExp);
            System.out.println("aa :: " + aa[0]);
            double bbb = Double.parseDouble(aa[0]);

            String[] cc = requestedDiskSize.split(reqExp);
            System.out.println("cc :: " + cc[0]);
            double ddd = Double.parseDouble(cc[0]);

            if(requestedMemorySize.toLowerCase().contains("g")) {
                //aa = requestedMemorySize.split(reqExp);
                bbb = bbb * 1024;
                System.out.println("bbb :: " + bbb);
            }

            memorySize = (int) Math.round(bbb);
            System.out.println("memorySize :::" + memorySize);


            if(requestedDiskSize.toLowerCase().contains("g")) {
                //String[] cc = requestedDiskSize.split(reqExp);
                ddd = ddd * 1024;
                System.out.println("ddd :: " + ddd);

            }

            diskSize = (int) Math.round(ddd);
            System.out.println("diskSize :::" + diskSize);



            if(resultMap.containsKey("instances")){
                instance = (Integer) resultMap.get("instances");
            }

            if(resultMap.containsKey("buildpacks")){
                List<String> buildpacks = (List) resultMap.get("buildpacks");
                buildPack = buildpacks.get(0);
            }

            app.setInstances(instance);
            app.setMemory(memorySize);
            app.setDiskQuota(diskSize);
            app.setBuildpack(buildPack);
            app.setSpaceGuid(marketSpaceGuid);
            app.setAppName(name);
            app.setDomainId(marketDomainGuid);
            app.setHostName(name);

            log.info("================= 앱 생성 START =================");

            log.info("[{}] ::: 임시파일을 생성합니다.", name);
            file = createTempFile(param); // 임시파일을 생성합니다.

            log.info("[{}] ::: App을 만들고 guid를 return 합니다.", name);

            try {
                applicationid = createApplication(app, reactorCloudFoundryClient); // App을 만들고 guid를 return 합니다.
            }catch(NullPointerException npe) {
                applicationid = createApplication(app, reactorCloudFoundryClient); // App을 만들고 guid를 return 합니다.
            }

            log.info("[{}] ::: route를 생성후 guid를 return 합니다.", name);
            routeid = createRoute(app, reactorCloudFoundryClient); //route를 생성후 guid를 return 합니다.

            log.info("[{}] ::: app와 route를 mapping합니다.", name);
            routeMapping(applicationid, routeid, reactorCloudFoundryClient); // app와 route를 mapping합니다.

            log.info("[{}] ::: app에 파일 업로드 작업을 합니다.", name);
            fileUpload(file, applicationid, reactorCloudFoundryClient); // app에 파일 업로드 작업을 합니다.

            String finalApplicationid = applicationid;

            log.info("================= 앱 생성 END app name ::: {} - APP_GUID ::: {}" ,name, finalApplicationid);

            Map<String, Object> result = new HashMap<>();
            result.put("appId", finalApplicationid);
            result.put("env", resultMap);
            
            return result;
        } catch (Exception e) {

            log.info("Exception Class:::{}", e.getClass().getName());
            //e.printStackTrace();
            log.info(e.getMessage());
            if (!applicationid.equals("applicationID")) {
                if (!routeid.equals("route ID")) {
                    reactorCloudFoundryClient.routes().delete(DeleteRouteRequest.builder().routeId(routeid).build()).block();
                }
                reactorCloudFoundryClient.applicationsV2().delete(DeleteApplicationRequest.builder().applicationId(applicationid).build()).block();
            }

            Map<String, Object> result = new HashMap<>();
            result.put("RESULT", "fail");
            result.put("msg", e.getMessage());
            
            return result;
        } finally {
            if (file != null) {
                boolean deleted = file.delete();
                log.info("file: {}, deleted: {}", file, deleted);
            }
        }
    }


    /**
     * 임시 app 파일을 생성한다.
     *
     * @param param  Catalog(모델클래스)
     * @return Map(자바클래스)
     * @throws Exception Exception(자바클래스)
     */
    protected File createTempFile(Software param) throws Exception {
        try {
            String appName= param.getApp();
            int index = param.getApp().indexOf(".");
            File file = File.createTempFile(appName.substring(0, index), appName.substring(index , appName.length()));
            int pathIndex = param.getAppPath().lastIndexOf("/");
            String FileName = param.getAppPath().substring(pathIndex + 1, param.getAppPath().length());
            final StoredObject object = container.getObject(FileName);
            byte[] bytes = object.downloadObject();
            InputStream is = new ByteArrayInputStream( bytes );
            OutputStream out = new FileOutputStream(file);
            IOUtils.copy(is, out);
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(out);
            log.info("file ::: " + file.getPath());
            return file;
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new PlatformException("createTempFile", e);
        }
    }

    /**
     * 임시 manifest 파일을 생성한다.
     *
     * @param param  Catalog(모델클래스)
     * @return Map(자바클래스)
     * @throws Exception Exception(자바클래스)
     */
    protected Map createManifestFile(Software param) throws Exception {
        File file = null;
        try {
            //response.setContentType("application/octet-stream");
            String manifestName= param.getManifest();
            int index = param.getManifest().indexOf(".");
            file = File.createTempFile(manifestName.substring(0, index), manifestName.substring(index , manifestName.length()));
            int pathIndex = param.getManifestPath().lastIndexOf("/");
            String FileName = param.getManifestPath().substring(pathIndex + 1, param.getManifestPath().length());
            log.info(FileName);
            final StoredObject object = container.getObject(FileName);
            byte[] bytes = object.downloadObject();

            InputStream is = new ByteArrayInputStream( bytes );
            OutputStream out = new FileOutputStream(file);
            IOUtils.copy(is, out);
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(out);
            log.info("file ::: " + file.getPath());
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return convertYamlToJson(file);
    }

    public Map<String, Object> convertYamlToJson(File file) {
        Yaml yaml = new Yaml();

        Reader yamlFile = null;

        try {
            yamlFile = new FileReader(file);
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
        }

        Map<String, Object> yamlMaps = yaml.load(yamlFile);

        return yamlMaps;
    }

    protected String getBrowser(String header) {

        if (header.indexOf("MSIE") > -1) {
            return "MSIE";
        } else if (header.indexOf("Chrome") > -1) {
            return "Chrome";
        } else if (header.indexOf("Opera") > -1) {
            return "Opera";
        } else if (header.indexOf("Trident/7.0") > -1) {
            //IE 11 이상 //IE 버전 별 체크 >> Trident/6.0(IE 10) , Trident/5.0(IE 9) , Trident/4.0(IE 8)
            return "MSIE";
        }

        return "Firefox";
    }

    protected String getDisposition(String filename, String browser) throws Exception {
        String encodedFilename = null;

        if (browser.equals("MSIE")) {
            encodedFilename = URLEncoder.encode(filename, "UTF-8").replaceAll("\\+", "%20");
        } else if (browser.equals("Firefox")) {
            encodedFilename = "\"" + new String(filename.getBytes("UTF-8"), "8859_1") + "\"";
        } else if (browser.equals("Opera")) {
            encodedFilename = "\"" + new String(filename.getBytes("UTF-8"), "8859_1") + "\"";
        } else if (browser.equals("Chrome")) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < filename.length(); i++) {
                char c = filename.charAt(i);
                if (c > '~') {
                    sb.append(URLEncoder.encode("" + c, "UTF-8"));
                } else {
                    sb.append(c);
                }
            }
            encodedFilename = sb.toString();
        } else {
            throw new RuntimeException("Not supported browser");
        }

        return encodedFilename;
    }

    protected String createApplication(App param, ReactorCloudFoundryClient reactorCloudFoundryClient) throws Exception {
        CreateApplicationResponse applicationRes =  reactorCloudFoundryClient.
                applicationsV2().create(CreateApplicationRequest.builder().buildpack(param.getBuildpack()).memory(param.getMemory()).name(param.getAppName()).diskQuota(param.getDiskQuota()).spaceId(param.getSpaceGuid()).build()).block();

        Thread.sleep(5000);

        return  applicationRes.getMetadata().getId();

    }

    /**
     * 라우트를 생성한다..
     *
     * @param param                     Catalog
     * @param reactorCloudFoundryClient ReactorCloudFoundryClient
     * @return Map(자바클래스)
     * @throws Exception Exception(자바클래스)
     */
    protected String createRoute(App param, ReactorCloudFoundryClient reactorCloudFoundryClient) {
        return reactorCloudFoundryClient.
                routes().create(CreateRouteRequest.builder().host(param.getHostName()).domainId(param.getDomainId()).spaceId(param.getSpaceGuid()).build()).block().getMetadata().getId();
    }


    /**
     * 라우트를 앱에 매핑한다.
     *
     * @param applicationid             String
     * @param routeid                   String
     * @param reactorCloudFoundryClient ReactorCloudFoundryClient
     * @return Map(자바클래스)
     * @throws Exception Exception(자바클래스)
     */
    protected void routeMapping(String applicationid, String routeid, ReactorCloudFoundryClient reactorCloudFoundryClient) throws Exception {
        reactorCloudFoundryClient.
                routeMappings().create(CreateRouteMappingRequest.builder().routeId(routeid).applicationId(applicationid).build()).block();
    }

    /**
     * 파일을 업로드한다.
     *
     * @param file                      File
     * @param applicationid             String
     * @param reactorCloudFoundryClient ReactorCloudFoundryClient
     * @return Map(자바클래스)
     * @throws Exception Exception(자바클래스)
     */
    protected void fileUpload(File file, String applicationid, ReactorCloudFoundryClient reactorCloudFoundryClient) throws Exception {
        try {
            reactorCloudFoundryClient.
                    applicationsV2().upload(UploadApplicationRequest.builder().applicationId(applicationid).application(file.toPath()).build()).block();
        } catch (Exception e) {
            log.info(e.toString());
            throw new PlatformException("fileUpload", e);
        }
    }

    public Map updateApp(Map envJson, String appGuid) {
        Map resultMap = new HashMap();
        try {
            ReactorCloudFoundryClient cloudFoundryClient = cloudFoundryClient(tokenProvider());
            if (envJson != null && envJson.size() > 0) {
                cloudFoundryClient.applicationsV2().update(org.cloudfoundry.client.v2.applications.UpdateApplicationRequest.builder().applicationId(appGuid).environmentJsons(envJson).build()).block();
            } else if (envJson != null && envJson.size() == 0) {
                cloudFoundryClient.applicationsV2().update(UpdateApplicationRequest.builder().applicationId(appGuid).environmentJsons(new HashMap<>()).build()).block();
            }
            resultMap.put("result", true);
        } catch (Exception e) {
            // todo ::: to delete
            //e.printStackTrace();
            resultMap.put("result", false);
            resultMap.put("msg", e);
        }

        return resultMap;
    }

    /**
     * 카탈로그 앱을 시작한다.
     *
     * @param applicationId             applicationId
     * @return Map(자바클래스)
     */
    public Map<String, Object> procStartApplication(String applicationId) {
        ReactorCloudFoundryClient reactorCloudFoundryClient = cloudFoundryClient(tokenProvider());

        try {
            Thread.sleep(500);
            reactorCloudFoundryClient.applicationsV2().update(UpdateApplicationRequest.builder().applicationId(applicationId).state("STARTED").build()).block();
        } catch (Exception e) {
            log.info(e.toString());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("RESULT", "success");

        return result;
    }

    public void timer(int waitTime) {
        long startTime = Calendar.getInstance().getTimeInMillis();
        while ((Calendar.getInstance().getTimeInMillis() - startTime) < (ONE_SECOND * waitTime)) {
            try {
                Thread.sleep(1_000L);
            } catch (InterruptedException e) {
                log.warn(e.getMessage(), e);
                Thread.currentThread().interrupt();
            }
        }
    }

    public ApplicationEntity getApplicationNameExists(String name) throws PlatformException {
        int count = 0;
        ListApplicationsResponse listApplicationsResponse;
        ApplicationEntity app = null;
        try{
            listApplicationsResponse = cloudFoundryClient(tokenProvider()).applicationsV2().list(ListApplicationsRequest.builder().organizationId(marketOrgGuid).spaceId(marketSpaceGuid).build()).block();

            for (ApplicationResource applicationResource : listApplicationsResponse.getResources()) {
                if (applicationResource.getEntity().getName().equals(name)) {
                    app = applicationResource.getEntity();
                    count++;
                }
            }

        }catch (Exception e) {
            throw new PlatformException("getApplicationNameExist", e);
        }

        return app;
    }

    public GetApplicationResponse getApp(Instance instance) {
        log.info("get app for deprovision");
        return cloudFoundryClient(tokenProvider()).applicationsV2().get(GetApplicationRequest.builder().applicationId(instance.getAppGuid()).build()).block();
    }

    public ListRouteMappingsResponse getRouteMappingList(String appGuid) {
        return cloudFoundryClient(tokenProvider()).routeMappings().list(ListRouteMappingsRequest.builder().applicationId(appGuid).build()).block();
    }


    /**
     * 앱 라우트를 삭제한다.
     *
     * @param guid
     * @param routeGuid
     * @return
     */
    public Map removeApplicationRoute(String guid, String routeGuid) {
        Map resultMap = new HashMap();

        try {
            cloudFoundryClient(tokenProvider()).applicationsV2().removeRoute(RemoveApplicationRouteRequest.builder().applicationId(guid).routeId(routeGuid).build()).block();
            cloudFoundryClient(tokenProvider()).routes().delete(DeleteRouteRequest.builder().routeId(routeGuid).build()).block();

            resultMap.put("result", true);
        } catch (Exception e) {
            //e.printStackTrace();
            resultMap.put("result", false);
            resultMap.put("msg", e);
        }

        return resultMap;
    }


    /**
     * 앱을 삭제한다.
     *
     * @param appGuid
     * @return
     */
    public Map deleteApp(String appGuid) {
        HashMap result = new HashMap();
        try {
            cloudFoundryClient(tokenProvider()).applicationsV2().delete(DeleteApplicationRequest.builder().applicationId(appGuid).build()).block();
            result.put("result", true);
            result.put("msg", "You have successfully completed the task.");
        } catch (Exception e) {
            //e.printStackTrace();
            result.put("result", false);
            result.put("msg", e.getMessage());
        }
        return result;
    }


    /**
     * 앱 최근 로그
     *
     * @param guid
     * @return
     */
    public List<Envelope> getRecentLog(String guid) {
        //TokenProvider tokenProvider = tokenProvider(token);
        ReactorDopplerClient reactorDopplerClient = dopplerClient(connectionContext(), tokenProvider());

        RecentLogsRequest.Builder requestBuilder = RecentLogsRequest.builder();
        requestBuilder.applicationId(guid);

        List<Envelope> getRecentLog = reactorDopplerClient.recentLogs(requestBuilder.build()).collectList().block();
        return getRecentLog;
    }

}
