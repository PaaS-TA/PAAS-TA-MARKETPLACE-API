//package org.openpaas.paasta.marketplace.api.controller.cloudfoundry;
//
//import lombok.RequiredArgsConstructor;
//import org.cloudfoundry.client.v2.applications.ListApplicationsResponse;
//import org.openpaas.paasta.marketplace.api.service.InstanceService;
//import org.openpaas.paasta.marketplace.api.service.PlatformService;
//import org.openpaas.paasta.marketplace.api.service.SoftwareService;
//import org.openpaas.paasta.marketplace.api.service.cloudfoundry.AppService;
//import org.openpaas.paasta.marketplace.api.util.CommonUtils;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.io.IOException;
//
///**
// * @author hrjin
// * @version 1.0
// * @since 2019-08-20
// */
//@RestController
//@RequiredArgsConstructor
//@RequestMapping(value = "/apps")
//public class AppController extends CommonUtils{
//    @Value("${market.org..guid}")
//    public String marketOrgGuid;
//
//    @Value("${market.space.guid}")
//    public String marketSpaceGuid;
//
//    private final AppService appService;
//
//    private final SoftwareService softwareService;
//
//    private final PlatformService platformService;
//
//    private final InstanceService instanceService;
//    /**
//     * 앱 목록 조회
//     *
//     * @return
//     */
//    @GetMapping
//    public ListApplicationsResponse getAppList() throws IOException {
////        CommonUtils commonUtils = new CommonUtils();
////        Map map = commonUtils.convertYamlToJson();
////        System.out.println("yaml to json ::: " + map.toString());
//        Object obj = softwareService.getObjectDownload("596d5de714184b5da8ffb8688a58b210-1566287270833-bWFuaWZlc3QueW1s");
//        System.out.println("obj ::: " + obj);
//        convertYamlToJson(obj);
//        return appService.getAppList(marketOrgGuid, marketSpaceGuid);
//    }
//
////    @PostMapping
////    public Software createApp(@RequestBody Software param, @RequestHeader("User-Agent") String token, HttpServletResponse httpServletResponse) throws Exception {
////        Instance instance = instanceService.findBySoftwareId(param.getId());
////
////        //platformService.provision(instance);
////        return instance.getSoftware();
////    }
//}
