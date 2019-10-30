package org.openpaas.paasta.marketplace.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.openpaas.paasta.marketplace.api.domain.Instance;
import org.openpaas.paasta.marketplace.api.domain.InstanceCartSpecification;
import org.openpaas.paasta.marketplace.api.domain.Instance.ProvisionStatus;
import org.openpaas.paasta.marketplace.api.domain.Instance.Status;
import org.openpaas.paasta.marketplace.api.domain.InstanceSpecification;
import org.openpaas.paasta.marketplace.api.repository.InstanceRepository;
import org.openpaas.paasta.marketplace.api.util.HostUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class InstanceService {

    @Value("${provisioning.try-count}")
    private int provisioningTryCount;

    @Value("${provisioning.timeout}")
    private long provisioningTimeout;

    @Value("${deprovisioning.try-count}")
    private int deprovisioningTryCount;

    @Value("${deprovisioning.timeout}")
    private long deprovisioningTimeout;

    private final InstanceRepository instanceRepository;
    //private final StatsRepository statsRepository;

    private final PlatformService platformService;

    public Instance create(Instance instance) {
        instance.setStatus(Instance.Status.Approval);
        instance.setProvisionStatus(Instance.ProvisionStatus.Pending);
        instance.setUsageStartDate(LocalDateTime.now());
        instance.setHost(HostUtils.getHostName());

        return instanceRepository.save(instance);
    }

    public Page<Instance> getPage(InstanceSpecification spec, Pageable pageable) {
        return instanceRepository.findAll(spec, pageable);
    }

//    public Page<Instance> getPage2(String userId, LocalDateTime usageStartDate, LocalDateTime usageEndDate, Pageable pageable) {
//        return statsRepository.countsOfInstsUsingMonth(userId, usageStartDate, usageEndDate, pageable);
//    }

    public Instance get(Long id) {
        return instanceRepository.findById(id).get();
    }

    public Instance updateToDeleted(Long id) {
        Instance saved = instanceRepository.findById(id).get();
        saved.setStatus(Status.Deleted);
        saved.setDeprovisionStatus(ProvisionStatus.Pending);
        saved.setUsageEndDate(LocalDateTime.now());

        return saved;
    }

    @Async("provisionExecutor")
    public Future<Instance> provision(Long id) {
        Instance instance = instanceRepository.findById(id).get();

        try {
            log.info("provision start: {}", instance.getId());

            platformService.deprovision(instance);
            platformService.provision(instance);

            instance.setProvisionStatus(Instance.ProvisionStatus.Successful);
            instance.setProvisionEndDate(LocalDateTime.now());

            log.info("provision success: {}", instance.getId());
        } catch (Exception e) {
            log.info("provision failed: {}", instance.getId());

            instance.setProvisionTryCount(instance.getProvisionTryCount() + 1);
            if (instance.getProvisionTryCount() >= provisioningTryCount) {
                instance.setProvisionStatus(Instance.ProvisionStatus.Failed);
            } else {
                instance.setProvisionStatus(Instance.ProvisionStatus.Pending);
            }
            instance.setProvisionEndDate(LocalDateTime.now());
        }

        return new AsyncResult<>(instance);
    }

    @Async("deprovisionExecutor")
    public Instance deprovision(Long id) {
        Instance instance = instanceRepository.findById(id).get();

        try {
            log.info("deprovision start: {}", instance.getId());

            platformService.deprovision(instance);

            instance.setDeprovisionStatus(Instance.ProvisionStatus.Successful);
            instance.setDeprovisionEndDate(LocalDateTime.now());

            log.info("deprovision success: {}", instance.getId());
        } catch (Exception e) {
            log.info("deprovision failed: {}", instance.getId());

            instance.setDeprovisionTryCount(instance.getDeprovisionTryCount() + 1);
            if (instance.getDeprovisionTryCount() >= deprovisioningTryCount) {
                instance.setDeprovisionStatus(Instance.ProvisionStatus.Failed);
            } else {
                instance.setDeprovisionStatus(Instance.ProvisionStatus.Pending);
            }
            instance.setDeprovisionEndDate(LocalDateTime.now());
        }

        return instance;
    }

    public long countOfProvisioning() {
        log.info("countOfProvisioning: start");

        InstanceSpecification spec = InstanceSpecification.ofSystemHost();
        spec.setStatus(Status.Approval);
        spec.getProvisionStatusIn().add(ProvisionStatus.InProgress);
        spec.getProvisionStatusIn().add(ProvisionStatus.Ready);

        long count = instanceRepository.count(spec);

        log.info("countOfProvisioning: end: {}", count);

        return count;
    }

    public Instance getOneToReadyProvision() {
        log.info("getOneToReadyProvision: start");

        InstanceSpecification spec = InstanceSpecification.ofSystemHost();
        spec.setStatus(Status.Approval);
        spec.setProvisionStatus(ProvisionStatus.Pending);
        spec.setProvisionTryCountMax(provisioningTryCount);

        List<Order> orders = new ArrayList<>();
        orders.add(new Order(Direction.ASC, "provisionTryCount"));
        orders.add(new Order(Direction.ASC, "id"));

        Pageable pageable = PageRequest.of(0, 1, Sort.by(orders));
        Page<Instance> instancePage = instanceRepository.findAll(spec, pageable);

        List<Instance> instanceList = instancePage.getContent();
        if (instanceList.isEmpty()) {
            return null;
        }

        Instance instance = instanceList.get(0);

        log.info("getOneToReadyProvision: end: {}", instance);

        return instance;
    }

    public Instance getOneToProvision() {
        log.info("getOneToProvision: start");

        InstanceSpecification spec = InstanceSpecification.ofSystemHost();
        spec.setStatus(Status.Approval);
        spec.setProvisionStatus(ProvisionStatus.Ready);
        spec.setProvisionTryCountMax(provisioningTryCount);

        List<Order> orders = new ArrayList<>();
        orders.add(new Order(Direction.ASC, "provisionTryCount"));
        orders.add(new Order(Direction.ASC, "id"));

        Pageable pageable = PageRequest.of(0, 1, Sort.by(orders));
        Page<Instance> instancePage = instanceRepository.findAll(spec, pageable);

        List<Instance> instanceList = instancePage.getContent();
        if (instanceList.isEmpty()) {
            return null;
        }

        Instance instance = instanceList.get(0);

        log.info("getOneToProvision: end: {}", instance);

        return instance;
    }

    public long countOfDeprovisioning() {
        log.info("countOfDeprovisioning: start");

        InstanceSpecification spec = InstanceSpecification.ofSystemHost();
        spec.setStatus(Status.Deleted);
        spec.getDeprovisionStatusIn().add(ProvisionStatus.InProgress);
        spec.getDeprovisionStatusIn().add(ProvisionStatus.Ready);

        long count = instanceRepository.count(spec);

        log.info("countOfDeprovisioning: end: {}", count);

        return count;
    }

    public Instance getOneToReadyDeprovision() {
        log.info("getOneToReadyDeprovision: start");

        InstanceSpecification spec = InstanceSpecification.ofSystemHost();
        spec.setStatus(Status.Deleted);
        spec.setDeprovisionStatus(ProvisionStatus.Pending);
        spec.setDeprovisionTryCountMax(deprovisioningTryCount);

        List<Order> orders = new ArrayList<>();
        orders.add(new Order(Direction.ASC, "deprovisionTryCount"));
        orders.add(new Order(Direction.ASC, "id"));

        Pageable pageable = PageRequest.of(0, 1, Sort.by(orders));
        Page<Instance> instancePage = instanceRepository.findAll(spec, pageable);

        List<Instance> instanceList = instancePage.getContent();
        if (instanceList.isEmpty()) {
            return null;
        }

        Instance instance = instanceList.get(0);

        log.info("getOneToReadyDeprovision: end: {}", instance);

        return instance;
    }

    public Instance getOneToDeprovision() {
        log.info("getOneToDeprovision: start");

        InstanceSpecification spec = InstanceSpecification.ofSystemHost();
        spec.setStatus(Status.Deleted);
        spec.setDeprovisionStatus(ProvisionStatus.Ready);
        spec.setDeprovisionTryCountMax(deprovisioningTryCount);

        List<Order> orders = new ArrayList<>();
        orders.add(new Order(Direction.ASC, "deprovisionTryCount"));
        orders.add(new Order(Direction.ASC, "id"));

        Pageable pageable = PageRequest.of(0, 1, Sort.by(orders));
        Page<Instance> instancePage = instanceRepository.findAll(spec, pageable);

        List<Instance> instanceList = instancePage.getContent();
        if (instanceList.isEmpty()) {
            return null;
        }

        Instance instance = instanceList.get(0);

        log.info("getOneToDeprovision: end: {}", instance);

        return instance;
    }

    public void stopProvisioning(boolean timeout) {
        log.info("stopProvisioning: start: {}", timeout);

        InstanceSpecification spec = InstanceSpecification.ofSystemHost();
        spec.setProvisionStatus(ProvisionStatus.InProgress);
        if (timeout) {
            spec.setDeprovisionStartDateBefore(LocalDateTime.now().minusSeconds(deprovisioningTimeout / 1000));
        }

        List<Instance> instanceList = instanceRepository.findAll(spec);
        for (Instance instance : instanceList) {
            instance.setProvisionStatus(ProvisionStatus.Pending);
            if (timeout) {
                instance.setProvisionTryCount(instance.getProvisionTryCount() + 1);
            }
        }

        log.info("stopProvisioning: end: {}", timeout);
    }

    public void stopDeprovisioning(boolean timeout) {
        log.info("stopDeprovisioning: start: {}", timeout);

        InstanceSpecification spec = InstanceSpecification.ofSystemHost();
        spec.setDeprovisionStatus(ProvisionStatus.InProgress);
        if (timeout) {
            spec.setDeprovisionStartDateBefore(LocalDateTime.now().minusSeconds(deprovisioningTimeout / 1000));
        }

        List<Instance> instanceList = instanceRepository.findAll(spec);
        for (Instance instance : instanceList) {
            instance.setDeprovisionStatus(ProvisionStatus.Pending);
            if (timeout) {
                instance.setDeprovisionTryCount(instance.getDeprovisionTryCount() + 1);
            }
        }

        log.info("stopDeprovisioning: end: {}", timeout);
    }

    public Long usagePriceTotal(InstanceCartSpecification spec) {
    	if (StringUtils.isBlank(spec.getCreatedBy())) {
    		return 0L;
    	}
//    	String usagePriceTotal = instanceRepository.usagePriceTotal(spec.getCreatedBy());
//    	return (StringUtils.isNotBlank(usagePriceTotal) ? Long.parseLong(usagePriceTotal) : 0L);
    	return instanceRepository.usagePriceTotal(spec.getCreatedBy());
    }
}
