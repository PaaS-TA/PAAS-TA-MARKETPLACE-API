package org.openpaas.paasta.marketplace.api.controller.cloudfoundry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cloudfoundry.doppler.Envelope;
import org.openpaas.paasta.marketplace.api.service.cloudfoundry.AppService;
import org.openpaas.paasta.marketplace.api.util.CommonUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hrjin
 * @version 1.0
 * @since 2019-08-20
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/admin/apps")
public class AppController extends CommonUtils{
    @Value("${market.org..guid}")
    public String marketOrgGuid;

    @Value("${market.space.guid}")
    public String marketSpaceGuid;

    private final AppService appService;


    /**
     * 앱 최근 로그
     *
     * @param guid
     * @return Space respSpace
     * @throws Exception the exception
     */
    @GetMapping(value = "/{guid}/recentLogs")
    public Map getRecentLog(@PathVariable String guid) throws Exception {
        log.info("getRecentLog Start : " + guid);

        Map mapLog = new HashMap();
        try {
            List<Envelope> respAppEvents = appService.getRecentLog(guid);
            mapLog.put("log", respAppEvents);
        } catch (Exception e) {
            log.info("################ ");
            log.error(e.toString());
            mapLog.put("log", "");
        }

        log.info("getRecentLog End");

        return mapLog;
    }
}
