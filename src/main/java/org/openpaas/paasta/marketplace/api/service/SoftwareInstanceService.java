package org.openpaas.paasta.marketplace.api.service;

import java.util.List;

import javax.transaction.Transactional;

import org.openpaas.paasta.marketplace.api.domain.Software;
import org.openpaas.paasta.marketplace.api.domain.SoftwareInstance;
import org.openpaas.paasta.marketplace.api.domain.SoftwareInstance.ProvisionStatus;
import org.openpaas.paasta.marketplace.api.domain.SoftwareInstance.Status;
import org.openpaas.paasta.marketplace.api.domain.SoftwareInstanceSpecification;
import org.openpaas.paasta.marketplace.api.repository.SoftwareInstanceRepository;
import org.openpaas.paasta.marketplace.api.repository.SoftwareRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class SoftwareInstanceService {

	@Autowired
    private SoftwareRepository softwareRepository;

    @Autowired
    private SoftwareInstanceRepository softwareInstanceRepository;

    public SoftwareInstance createSoftwareInstance(SoftwareInstance softwareInstance) {
        Software software = softwareRepository.findById(softwareInstance.getSoftware().getId()).orElse(null);
        softwareInstance.setSoftware(software);
        softwareInstance.setStatus(Status.Pending);

        SoftwareInstance saved = softwareInstanceRepository.save(softwareInstance);

        return saved;
    }

    public SoftwareInstance createSoftwareInstance(SoftwareInstance softwareInstance, Status status) {
        Software software = softwareRepository.findById(softwareInstance.getSoftware().getId()).orElse(null);
        softwareInstance.setSoftware(software);
        softwareInstance.setStatus(status);

        SoftwareInstance saved = softwareInstanceRepository.save(softwareInstance);

        return saved;
    }

    public List<SoftwareInstance> getSoftwareInstanceList(SoftwareInstanceSpecification spec) {
        return softwareInstanceRepository.findAll(spec);
    }

    public SoftwareInstance getSoftwareInstance(Long id) {
        return softwareInstanceRepository.findById(id).orElse(null);
    }

    public SoftwareInstance provision(Long id) {
        SoftwareInstance softwareInstance = softwareInstanceRepository.findById(id).orElse(null);
        softwareInstance.setProvisionStatus(ProvisionStatus.InProgress);

        try {
            // TODO:
            softwareInstance.setProvisionStatus(ProvisionStatus.Successful);
        } catch (Exception e) {
            softwareInstance.setProvisionStatus(ProvisionStatus.Failed);
        }

        return softwareInstance;
    }

}
