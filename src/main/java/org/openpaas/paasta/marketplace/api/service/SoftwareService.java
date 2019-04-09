package org.openpaas.paasta.marketplace.api.service;

import java.util.List;

import javax.transaction.Transactional;

import org.openpaas.paasta.marketplace.api.model.Software;
import org.openpaas.paasta.marketplace.api.model.SoftwareSpecification;
import org.openpaas.paasta.marketplace.api.repository.SoftwareRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class SoftwareService {

	@Autowired
    private SoftwareRepository softwareRepository;

    public List<Software> getSoftwareList(SoftwareSpecification spec) {
        return softwareRepository.findAll(spec);
    }

    public Software createSoftware(Software software) {
        Software saved = softwareRepository.save(software);

        createSnapshot(saved);

        return saved;
    }

    public Software getSoftware(long id) {
        return softwareRepository.findById(id).orElse(null);
    }

    public void createSnapshot(Software software) {
        // TODO:
    }

}
