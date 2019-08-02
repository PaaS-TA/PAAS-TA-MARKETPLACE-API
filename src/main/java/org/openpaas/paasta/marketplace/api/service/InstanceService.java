package org.openpaas.paasta.marketplace.api.service;

import java.time.LocalDateTime;

import javax.transaction.Transactional;

import org.openpaas.paasta.marketplace.api.domain.Instance;
import org.openpaas.paasta.marketplace.api.domain.Instance.Status;
import org.openpaas.paasta.marketplace.api.domain.InstanceSpecification;
import org.openpaas.paasta.marketplace.api.repository.InstanceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class InstanceService {

    private final InstanceRepository instanceRepository;

    public Instance create(Instance instance) {
        instance.setStatus(Instance.Status.Approval);
        instance.setProvisionStatus(Instance.ProvisionStatus.Pending);
        instance.setUsageStartDate(LocalDateTime.now());

        return instanceRepository.save(instance);
    }

    public Page<Instance> getPage(InstanceSpecification spec, Pageable pageable) {
        return instanceRepository.findAll(spec, pageable);
    }

    public Instance get(Long id) {
        return instanceRepository.findById(id).get();
    }

    public Instance updateToDeleted(Long id) {
        Instance saved = instanceRepository.findById(id).get();
        saved.setStatus(Status.Deleted);
        saved.setUsageEndDate(LocalDateTime.now());

        return saved;
    }

}
