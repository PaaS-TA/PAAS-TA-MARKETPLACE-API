package org.openpaas.paasta.marketplace.api.service;

import lombok.RequiredArgsConstructor;
import org.openpaas.paasta.marketplace.api.domain.Software;
import org.openpaas.paasta.marketplace.api.domain.Software.Status;
import org.openpaas.paasta.marketplace.api.domain.SoftwareHistory;
import org.openpaas.paasta.marketplace.api.domain.SoftwareHistorySpecification;
import org.openpaas.paasta.marketplace.api.domain.SoftwareSpecification;
import org.openpaas.paasta.marketplace.api.repository.SoftwareHistoryRepository;
import org.openpaas.paasta.marketplace.api.repository.SoftwareRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SoftwareService {

    //private final SwiftOSService swiftOSService;

    private final SoftwareRepository softwareRepository;

    private final SoftwareHistoryRepository softwareHistoryRepository;

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
        saved.setAppPath(software.getAppPath());
        saved.setManifest(software.getManifest());
        saved.setManifestPath(software.getManifestPath());
        saved.setIcon(software.getIcon());
        saved.setIconPath(software.getIconPath());
        saved.setScreenshotList(software.getScreenshotList());
        saved.setSummary(software.getSummary());
        saved.setDescription(software.getDescription());
        saved.setType(software.getType());
        saved.setPricePerDay(software.getPricePerDay());
        saved.setVersion(software.getVersion());
        saved.setInUse(software.getInUse());

        SoftwareHistory history = new SoftwareHistory();
        history.setSoftware(saved);
        softwareHistoryRepository.save(history);

        return saved;
    }

    public Software updateMetadata(Software software) {
        Software saved = softwareRepository.findById(software.getId()).get();
        saved.setName(software.getName());
        saved.setCategory(software.getCategory());
        saved.setInUse(software.getInUse());
        if (software.getStatus() != saved.getStatus()) {
            saved.setStatusModifiedDate(LocalDateTime.now());
        }
        saved.setStatus(software.getStatus());
        saved.setConfirmComment(software.getConfirmComment());

        SoftwareHistory history = new SoftwareHistory();
        history.setSoftware(saved);
        softwareHistoryRepository.save(history);

        return saved;
    }

    public List<SoftwareHistory> getHistoryList(SoftwareHistorySpecification spec, Sort sort) {
        return softwareHistoryRepository.findAll(spec, sort);
    }

    public List<Software> getSwByCreatedBy(String providerId) {
        return softwareRepository.findByCreatedBy(providerId);
    }

}
