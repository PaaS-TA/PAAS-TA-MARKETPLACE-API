//package org.openpaas.paasta.marketplace.api.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.openpaas.paasta.marketplace.api.service.UserProductService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import javax.transaction.Transactional;
//import java.util.Date;
//
//@Component
//@Transactional
//@Slf4j
//public class ScheduledTasks {
//
//    @Value("${provisioning.pool-size}")
//    private int provisioningPoolSize;
//
//    @Value("${deprovisioning.pool-size}")
//    private int deprovisioningPoolSize;
//
////    @Value("${task.execution.restrict-to-same-host:true}")
////    private boolean restrictToSameHost;
//
//    @Autowired
//    UserProductService userProductService;
//
//    @Scheduled(fixedRateString = "${provisioning.ready-fixed-rate}", initialDelayString = "${provisioning.ready-initial-delay}")
//    public void readyProvision() {
//        log.info("readyProvision: start -------------------------------------");
//
//        int provisioningCount = (int) userProductService.countOfProvisioning();
//
//        if (provisioningPoolSize > provisioningCount) {
//            SoftwareInstance instance = userProductService.getOneToReadyProvision();
//
//            if (instance != null) {
//                instance.setProvisionStatus(ProvisionStatus.Ready);
//            }
//        }
//
//        log.info("readyProvision: end -------------------------------------");
//    }
//
//    @Scheduled(fixedRateString = "${provisioning.progress-fixed-rate}", initialDelayString = "${provisioning.progress-initial-delay}")
//    public void provision() {
//        log.info("provision: start -------------------------------------");
//
//        SoftwareInstance instance = userProductService.getOneToProvision();
//
//        if (instance != null) {
//            instance.setProvisionStatus(ProvisionStatus.InProgress);
//            instance.setProvisionStartDate(new Date());
//
//            try {
//                userProductService.provision(instance.getId());
//            } catch (Exception e) {
//                // ignore.
//                log.warn("exception occured: {}", instance.getId());
//            }
//        }
//
//        log.info("provision: end -------------------------------------");
//    }
//
//    @Scheduled(fixedRateString = "${deprovisioning.ready-fixed-rate}", initialDelayString = "${deprovisioning.ready-initial-delay}")
//    public void readyDeprovision() {
//        log.info("readyDeprovision: start -------------------------------------");
//
//        int deprovisioningCount = (int) userProductService.countOfDeprovisioning();
//
//        if (deprovisioningPoolSize > deprovisioningCount) {
//            SoftwareInstance instance = userProductService.getOneToReadyDeprovision();
//
//            if (instance != null) {
//                instance.setDeprovisionStatus(ProvisionStatus.Ready);
//            }
//        }
//
//        log.info("readyDeprovision: end -------------------------------------");
//    }
//
//    @Scheduled(fixedRateString = "${deprovisioning.progress-fixed-rate}", initialDelayString = "${deprovisioning.progress-initial-delay}")
//    public void deprovision() {
//        log.info("deprovision: start -------------------------------------");
//
//        SoftwareInstance instance = userProductService.getOneToDeprovision();
//
//        if (instance != null) {
//            instance.setDeprovisionStatus(ProvisionStatus.InProgress);
//            instance.setDeprovisionStartDate(new Date());
//
//            try {
//                userProductService.deprovision(instance.getId());
//            } catch (Exception e) {
//                // ignore.
//                log.warn("exception occured: {}", instance.getId());
//            }
//        }
//
//        log.info("deprovision: end -------------------------------------");
//    }
//
//    @Scheduled(fixedRateString = "${provisioning.timeout-fixed-rate}", initialDelayString = "${provisioning.timeout-initial-delay}")
//    public void timeoutProvisioning() {
//        log.info("timeoutProvisioning: start -------------------------------------");
//
//        userProductService.stopProvisioning(true);
//
//        log.info("timeoutProvisioning: end -------------------------------------");
//    }
//
//    @Scheduled(fixedRateString = "${deprovisioning.timeout-fixed-rate}", initialDelayString = "${deprovisioning.timeout-initial-delay}")
//    public void timeoutDeprovisioning() {
//        log.info("timeoutDeprovisioning: start -------------------------------------");
//
//        userProductService.stopDeprovisioning(true);
//
//        log.info("timeoutDeprovisioning: end -------------------------------------");
//    }
//
//    @Scheduled(cron = "0 0 * * * ?")
//    public void approvalFromWaiting() {
//        log.info("approvalFromWaiting: start -------------------------------------");
//
//        userProductService.approvalFromWaiting();
//
//        log.info("approvalFromWaiting: end -------------------------------------");
//    }
//
//    @Scheduled(cron = "0 5 * * * ?")
//    public void deleteFromExpired() {
//        log.info("deleteFromExpired: start -------------------------------------");
//
//        userProductService.deleteFromExpired();
//
//        log.info("deleteFromExpired: end -------------------------------------");
//    }
//
//    @PostConstruct
//    public void init() {
//        //initHostName();
//        forceStop();
//    }
//
////    public void initHostName() {
////        if (!restrictToSameHost) {
////            return;
////        }
////
////        String hostName = HostUtils.getHostName();
////        SoftwareInstanceSpecification.setSystemHost(hostName);
////
////        log.info("hostName: {}", hostName);
////    }
//
//    public void forceStop() {
//        forceStopProvisioning();
//        forceStopDeprovisioning();
//    }
//
//    public void forceStopProvisioning() {
//        log.info("forceStopProvisioning: start -------------------------------------");
//
//        userProductService.stopProvisioning(false);
//
//        log.info("forceStopProvisioning: end -------------------------------------");
//    }
//
//    public void forceStopDeprovisioning() {
//        log.info("forceStopDeprovisioning: start -------------------------------------");
//
//        userProductService.stopDeprovisioning(false);
//
//        log.info("forceStopDeprovisioning: end -------------------------------------");
//    }
//
//}
