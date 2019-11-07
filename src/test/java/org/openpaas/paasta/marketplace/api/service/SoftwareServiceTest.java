package org.openpaas.paasta.marketplace.api.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openpaas.paasta.marketplace.api.domain.Category;
import org.openpaas.paasta.marketplace.api.domain.Software;
import org.openpaas.paasta.marketplace.api.domain.SoftwareHistory;
import org.openpaas.paasta.marketplace.api.domain.SoftwareHistorySpecification;
import org.openpaas.paasta.marketplace.api.domain.SoftwarePlan;
import org.openpaas.paasta.marketplace.api.domain.SoftwareSpecification;
import org.openpaas.paasta.marketplace.api.repository.SoftwareHistoryRepository;
import org.openpaas.paasta.marketplace.api.repository.SoftwarePlanRepository;
import org.openpaas.paasta.marketplace.api.repository.SoftwareRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class SoftwareServiceTest extends AbstractMockTest {

    SoftwareService softwareService;

    @Mock
    SoftwareRepository softwareRepository;

    @Mock
    SoftwarePlanRepository softwarePlanRepository;

    @Mock
    SoftwareHistoryRepository softwareHistoryRepository;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        softwareService = new SoftwareService(softwareRepository, softwarePlanRepository, softwareHistoryRepository);
    }

    @Test
    public void create() {
        Category category1 = category(1L, "category-01");
        Software software1 = software(1L, "software-01", category1);

        given(softwareRepository.save(any(Software.class))).willReturn(software1);

        Software result = softwareService.create(software1);
        assertEquals(software1, result);

        verify(softwareRepository).save(any(Software.class));
    }

    @Test
    public void getPage() {
        SoftwareSpecification spec = new SoftwareSpecification();
        Pageable pageRequest = PageRequest.of(0, 10);

        Category category1 = category(1L, "category-01");
        Software software1 = software(1L, "software-01", category1);
        Software software2 = software(2L, "software-02", category1);

        List<Software> softwareList = new ArrayList<Software>();
        softwareList.add(software1);
        softwareList.add(software2);

        Page<Software> page = new PageImpl<>(softwareList);

        given(softwareRepository.findAll(any(SoftwareSpecification.class), any(Pageable.class))).willReturn(page);

        Page<Software> result = softwareService.getPage(spec, pageRequest);
        assertEquals(page, result);

        verify(softwareRepository).findAll(any(SoftwareSpecification.class), any(Pageable.class));
    }

    @Test
    public void get() {
        Category category1 = category(1L, "category-01");
        Software software1 = software(1L, "software-01", category1);

        given(softwareRepository.findById(1L)).willReturn(Optional.of(software1));

        Software result = softwareService.get(1L);
        assertEquals(software1, result);

        verify(softwareRepository).findById(1L);
    }

    @Test
    public void getByName() {
        Category category1 = category(1L, "category-01");
        Software software1 = software(1L, "software-01", category1);

        given(softwareRepository.findByName(software1.getName())).willReturn(software1);

        Software result = softwareService.getByName(software1.getName());
        assertEquals(software1, result);

        verify(softwareRepository).findByName(software1.getName());
    }

    @Test
    public void update() {
        Category category1 = category(1L, "category-01");
        Software software1 = software(1L, "software-01", category1);

        given(softwareRepository.findById(1L)).willReturn(Optional.of(software1));

        Software result = softwareService.update(software1, null);
        assertEquals(software1, result);

        verify(softwareRepository).findById(1L);
    }

    @Test
    public void updateWithPlan() {
        Category category1 = category(1L, "category-01");
        Software software1 = software(1L, "software-01", category1);
        SoftwarePlan softwarePlan1 = new SoftwarePlan();
        softwarePlan1.setId(1L);
        softwarePlan1.setName("name-1");
        SoftwarePlan softwarePlan2 = new SoftwarePlan();
        softwarePlan2.setName("name-2");
        List<SoftwarePlan> softwarePlanList = new ArrayList<>();
        softwarePlanList.add(softwarePlan1);
        softwarePlanList.add(softwarePlan2);
        software1.setSoftwarePlanList(softwarePlanList);

        given(softwareRepository.findById(1L)).willReturn(Optional.of(software1));

        Software result = softwareService.update(software1, "1\\^2\\^3");
        assertEquals(software1, result);

        verify(softwareRepository).findById(1L);
        verify(softwarePlanRepository, atLeastOnce()).save(any(SoftwarePlan.class));
    }

    @Test
    public void updateMetadata() {
        Category category1 = category(1L, "category-01");
        Software software1 = software(1L, "software-01", category1);

        given(softwareRepository.findById(1L)).willReturn(Optional.of(software1));

        Software result = null;
        result = softwareService.updateMetadata(software1);
        assertEquals(software1, result);

        Software software1_1 = software(1L, "software-01_1", category1);
        software1_1.setStatus(Software.Status.Rejected);
        software1_1.setName("updated");

        result = softwareService.updateMetadata(software1_1);
        assertEquals(software1_1.getName(), result.getName());

        verify(softwareRepository, atLeastOnce()).findById(1L);
    }

    @Test
    public void getHistoryList() {
        SoftwareHistorySpecification spec = new SoftwareHistorySpecification();
        Sort sort = Sort.by("id");

        Category category1 = category(1L, "category-01");
        Software software1 = software(1L, "software-01", category1);
        SoftwareHistory SoftwareHistory1 = softwareHistory(1L, software1, "softwareHistory-01");
        SoftwareHistory SoftwareHistory2 = softwareHistory(2L, software1, "softwareHistory-02");

        List<SoftwareHistory> softwareHistoryList = new ArrayList<SoftwareHistory>();
        softwareHistoryList.add(SoftwareHistory1);
        softwareHistoryList.add(SoftwareHistory2);

        given(softwareHistoryRepository.findAll(any(SoftwareHistorySpecification.class), any(Sort.class)))
                .willReturn(softwareHistoryList);

        List<SoftwareHistory> result = softwareService.getHistoryList(spec, sort);
        assertEquals(softwareHistoryList, result);

        verify(softwareHistoryRepository).findAll(any(SoftwareHistorySpecification.class), any(Sort.class));
    }

    @Test
    public void getSwByCreatedBy() {
        Category category1 = category(1L, "category-01");
        Software software1 = software(1L, "software-01", category1);
        Software software2 = software(2L, "software-02", category1);

        List<Software> softwareList = new ArrayList<Software>();
        softwareList.add(software1);
        softwareList.add(software2);

        given(softwareRepository.findByCreatedBy(userId)).willReturn(softwareList);

        List<Software> result = softwareService.getSwByCreatedBy(userId);
        assertEquals(softwareList, result);

        verify(softwareRepository).findByCreatedBy(userId);
    }

}
