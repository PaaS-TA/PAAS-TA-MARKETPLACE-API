package org.openpaas.paasta.marketplace.api.controller;

import org.cloudfoundry.client.v2.applications.ListApplicationsResponse;
import org.openpaas.paasta.marketplace.api.model.App;
import org.openpaas.paasta.marketplace.api.service.AppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * App Controller
 *
 * @author hrjin
 * @version 1.0
 * @since 2019-01-15
 */
@RestController
@RequestMapping(value = "/apps")
public class AppController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppController.class);

    @Autowired
    private AppService appService;
//
//    @Autowired
//    private CatalogService catalogService;
//
//    /**
//     * App 생성
//     *
//     * @param catalog the catalog
//     * @return Map
//     */
//    @PostMapping(value = "/apps")
//    public Map createApp(@RequestBody Catalog catalog, HttpServletRequest request) {
//        LOGGER.debug("Create App start");
//        Map result = catalogService.createApp(catalog);
//        LOGGER.info("return create app :: {} ", result);
//        return result;
//    }
//
//
//    /**
//     * App 실행
//     *
//     * @param app the app
//     * @return Map
//     */
//    @PostMapping(value = "/apps/start")
//    public Map startApp(@RequestBody App app){
//        return appService.startApp(app);
//    }

    @GetMapping
    public ListApplicationsResponse getAppsList(){
        return appService.getAppsList();
    }

    @PostMapping(value = "/v3")
    public App createApp(@RequestBody App app){
        return appService.createApp(app);
    }
}
