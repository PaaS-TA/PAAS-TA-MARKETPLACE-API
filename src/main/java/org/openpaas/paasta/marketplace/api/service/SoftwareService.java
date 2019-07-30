package org.openpaas.paasta.marketplace.api.service;

import javax.transaction.Transactional;

import org.openpaas.paasta.marketplace.api.domain.Software;
import org.openpaas.paasta.marketplace.api.domain.Software.Status;
import org.openpaas.paasta.marketplace.api.domain.SoftwareSpecification;
import org.openpaas.paasta.marketplace.api.repository.SoftwareRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SoftwareService {

    private final SoftwareRepository softwareRepository;

    public Software create(Software software) {
        software.setStatus(Status.Pending);

        return softwareRepository.save(software);
    }

    public Page<Software> getPage(SoftwareSpecification spec, Pageable pageable) {
        return softwareRepository.findAll(spec, pageable);
    }

    public Software get(Long id) {
        return softwareRepository.findById(id).get();
    }

    public Software getByName(String name) {
        return softwareRepository.findByName(name);
    }

    public Software update(Software software) {
        Software saved = softwareRepository.findById(software.getId()).get();
        saved.setName(software.getName());
        saved.setCategory(software.getCategory());
        saved.setApp(software.getApp());
        saved.setManifest(software.getManifest());
        saved.setIcon(software.getIcon());
        saved.setScreenshotList(software.getScreenshotList());
        saved.setSummary(software.getSummary());
        saved.setDescription(software.getDescription());
        saved.setType(software.getType());
        saved.setPricePerDay(software.getPricePerDay());
        saved.setVersion(software.getVersion());
        saved.setInUse(software.getInUse());

        return saved;
    }

    public Software updateMetadata(Software software) {
        Software saved = softwareRepository.findById(software.getId()).get();
        saved.setName(software.getName());
        saved.setCategory(software.getCategory());
        saved.setInUse(software.getInUse());
        saved.setStatus(software.getStatus());
        saved.setConfirmComment(software.getConfirmComment());

        return saved;
    }

}
