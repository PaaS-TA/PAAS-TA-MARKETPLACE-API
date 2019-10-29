package org.openpaas.paasta.marketplace.api.config;

import java.time.LocalDateTime;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.openpaas.paasta.marketplace.api.domain.Instance;
import org.openpaas.paasta.marketplace.api.domain.Instance.ProvisionStatus;
import org.openpaas.paasta.marketplace.api.domain.InstanceSpecification;
import org.openpaas.paasta.marketplace.api.service.InstanceService;
import org.openpaas.paasta.marketplace.api.util.HostUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ScheduledTasks {

    @Value("${provisioning.pool-size}")
    private int provisioningPoolSize;

    @Value("${deprovisioning.pool-size}")
    private int deprovisioningPoolSize;

    @Value("${task.execution.restrict-to-same-host:true}")
    private boolean restrictToSameHost;

    private final InstanceService instanceService;

    private static final String LINE = "---------------------------------";

    private static final String NEXT_LINE = "\n" + LINE;

    @PostConstruct
    public void init() {
        initHostName();
        forceStop();
    }

    public void initHostName() {
        if (!restrictToSameHost) {
            return;
        }

        String hostName = HostUtils.getHostName();
        InstanceSpecification.setSystemHost(hostName);

        log.info("hostName: {}", hostName);
    }

    public void forceStop() {
        forceStopProvisioning();
        forceStopDeprovisioning();
    }

    private void logLine(String message) {
        String out = NEXT_LINE;
        out += "\n" + "  " + message;
        out += NEXT_LINE;

        log.info(out);
    }

    public void forceStopProvisioning() {
        logLine("forceStopProvisioning: start");

        instanceService.stopProvisioning(false);

        logLine("forceStopProvisioning: end");
    }

    public void forceStopDeprovisioning() {
        logLine("forceStopDeprovisioning: start");

        instanceService.stopDeprovisioning(false);

        logLine("forceStopDeprovisioning: end");
    }

    @Scheduled(fixedRateString = "${provisioning.ready-fixed-rate}", initialDelayString = "${provisioning.ready-initial-delay}")
    public void readyProvision() {
        logLine("readyProvision: start");

        int provisioningCount = (int) instanceService.countOfProvisioning();

        if (provisioningPoolSize > provisioningCount) {
            Instance instance = instanceService.getOneToReadyProvision();

            if (instance != null) {
                log.info("TTA [app-{}] ::: 시간 검증 시작 ::: {}", instance.getId() ,instance.getSoftware().getName());
                instance.setProvisionStatus(ProvisionStatus.Ready);
            }
        }

        logLine("readyProvision: end");
    }

    @Scheduled(fixedRateString = "${provisioning.progress-fixed-rate}", initialDelayString = "${provisioning.progress-initial-delay}")
    public void provision() {
        logLine("provision: start");

        Instance instance = instanceService.getOneToProvision();

        if (instance != null) {
            instance.setProvisionStatus(ProvisionStatus.InProgress);
            instance.setProvisionStartDate(LocalDateTime.now());

            try {
                instanceService.provision(instance.getId());
            } catch (Exception e) {
                // ignore.
                log.warn("exception occured: {}", instance.getId());
            }
        }

        logLine("provision: end");
    }

    @Scheduled(fixedRateString = "${deprovisioning.ready-fixed-rate}", initialDelayString = "${deprovisioning.ready-initial-delay}")
    public void readyDeprovision() {
        logLine("readyDeprovision: start");

        int deprovisioningCount = (int) instanceService.countOfDeprovisioning();

        if (deprovisioningPoolSize > deprovisioningCount) {
            Instance instance = instanceService.getOneToReadyDeprovision();

            if (instance != null) {
                instance.setDeprovisionStatus(ProvisionStatus.Ready);
            }
        }

        logLine("readyDeprovision: end");
    }

    @Scheduled(fixedRateString = "${deprovisioning.progress-fixed-rate}", initialDelayString = "${deprovisioning.progress-initial-delay}")
    public void deprovision() {
        logLine("deprovision: start");

        Instance instance = instanceService.getOneToDeprovision();

        if (instance != null) {
            instance.setDeprovisionStatus(ProvisionStatus.InProgress);
            instance.setDeprovisionStartDate(LocalDateTime.now());

            try {
                instanceService.deprovision(instance.getId());
            } catch (Exception e) {
                // ignore.
                log.warn("exception occured: {}", instance.getId());
            }
        }

        logLine("deprovision: end");
    }

    @Scheduled(fixedRateString = "${provisioning.timeout-fixed-rate}", initialDelayString = "${provisioning.timeout-initial-delay}")
    public void timeoutProvisioning() {
        logLine("timeoutProvisioning: start");

        instanceService.stopProvisioning(true);

        logLine("timeoutProvisioning: end");
    }

    @Scheduled(fixedRateString = "${deprovisioning.timeout-fixed-rate}", initialDelayString = "${deprovisioning.timeout-initial-delay}")
    public void timeoutDeprovisioning() {
        logLine("timeoutDeprovisioning: start");

        instanceService.stopDeprovisioning(true);

        logLine("timeoutDeprovisioning: end");
    }

}
