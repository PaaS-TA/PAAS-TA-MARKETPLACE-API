package org.openpaas.paasta.marketplace.api.service;

import java.util.List;

import javax.transaction.Transactional;

import org.openpaas.paasta.marketplace.api.domain.Product;
import org.openpaas.paasta.marketplace.api.domain.SoftwareSpecification;
import org.openpaas.paasta.marketplace.api.repository.SoftwareRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class SoftwareService {

	@Autowired
    private SoftwareRepository softwareRepository;

    public List<Product> getSoftwareList(SoftwareSpecification spec) {
        return softwareRepository.findAll(spec);
    }

    public Product createSoftware(Product software) {
        Product saved = softwareRepository.save(software);

        createSnapshot(saved);

        return saved;
    }

    public Product getSoftware(long id) {
        return softwareRepository.findById(id).orElse(null);
    }

    public void createSnapshot(Product software) {
        // TODO:
    }

}
