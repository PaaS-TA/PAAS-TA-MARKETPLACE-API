package org.openpaas.paasta.marketplace.api.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openpaas.paasta.marketplace.api.domain.SoftwarePlan;
import org.openpaas.paasta.marketplace.api.domain.SoftwarePlanSpecification;
import org.openpaas.paasta.marketplace.api.repository.SoftwarePlanRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class SoftwarePlanServiceTest extends AbstractMockTest {

    SoftwarePlanService softwarePlanService;

    @Mock
    SoftwarePlanRepository softwarePlanRepository;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        softwarePlanService = new SoftwarePlanService(softwarePlanRepository);
    }

    @Test
    public void get() {
        SoftwarePlan softwarePlan1 = softwarePlan(1L, 2L);

        given(softwarePlanRepository.findBySoftwareId(any(Long.class))).willReturn(softwarePlan1);

        SoftwarePlan result = softwarePlanService.get(2L);
        assertEquals(softwarePlan1, result);

        verify(softwarePlanRepository).findBySoftwareId(any(Long.class));
    }

    @Test
    public void getSWPId() {
        SoftwarePlan softwarePlan1 = softwarePlan(1L, 2L);

        given(softwarePlanRepository.findById(any(Long.class))).willReturn(Optional.of(softwarePlan1));

        SoftwarePlan result = softwarePlanService.getSWPId(1L);
        assertEquals(softwarePlan1, result);

        verify(softwarePlanRepository).findById(any(Long.class));
    }

    @Test
    public void create() {
        SoftwarePlan softwarePlan1 = softwarePlan(1L, 2L);

        given(softwarePlanRepository.save(any(SoftwarePlan.class))).willReturn(softwarePlan1);

        SoftwarePlan result = softwarePlanService.create(softwarePlan1);
        assertEquals(softwarePlan1, result);

        verify(softwarePlanRepository).save(any(SoftwarePlan.class));
    }

    @Test
    public void getByName() {
        SoftwarePlan softwarePlan1 = softwarePlan(1L, 2L);

        given(softwarePlanRepository.findByName(any(String.class))).willReturn(softwarePlan1);

        SoftwarePlan result = softwarePlanService.getByName("name-1");
        assertEquals(softwarePlan1, result);

        verify(softwarePlanRepository).findByName(any(String.class));
    }

    @Test
    public void getPage() {
        SoftwarePlanSpecification spec = new SoftwarePlanSpecification();
        Pageable pageRequest = PageRequest.of(0, 10);

        SoftwarePlan softwarePlan1 = softwarePlan(1L, 2L);
        SoftwarePlan softwarePlan2 = softwarePlan(2L, 2L);

        List<SoftwarePlan> softwarePlanList = new ArrayList<SoftwarePlan>();
        softwarePlanList.add(softwarePlan1);
        softwarePlanList.add(softwarePlan2);

        Page<SoftwarePlan> page = new PageImpl<>(softwarePlanList);

        given(softwarePlanRepository.findAll(any(SoftwarePlanSpecification.class), any(Pageable.class)))
                .willReturn(page);

        Page<SoftwarePlan> result = softwarePlanService.getPage(spec, pageRequest);
        assertEquals(page, result);

        verify(softwarePlanRepository).findAll(any(SoftwarePlanSpecification.class), any(Pageable.class));
    }

    @Test
    public void update() {
        SoftwarePlan softwarePlan1 = softwarePlan(1L, 2L);

        given(softwarePlanRepository.findBySoftwareId(2L)).willReturn(softwarePlan1);

        SoftwarePlan result = softwarePlanService.update(softwarePlan1);
        assertEquals(softwarePlan1, result);

        verify(softwarePlanRepository, atLeastOnce()).findBySoftwareId(2L);
    }

    @Test
    public void getSoftwarePlan() {
        SoftwarePlan softwarePlan1 = softwarePlan(1L, 2L);

        given(softwarePlanRepository.findOne(any(SoftwarePlanSpecification.class)))
                .willReturn(Optional.of(softwarePlan1));

        SoftwarePlan result = softwarePlanService.getSoftwarePlan("1");
        assertEquals(softwarePlan1, result);

        verify(softwarePlanRepository).findOne(any(SoftwarePlanSpecification.class));
    }

    @Test
    public void getList() {
        SoftwarePlanSpecification spec = new SoftwarePlanSpecification();
        Sort sort = Sort.by("id");

        SoftwarePlan softwarePlan1 = softwarePlan(1L, 2L);
        SoftwarePlan softwarePlan2 = softwarePlan(2L, 2L);

        List<SoftwarePlan> softwarePlanList = new ArrayList<SoftwarePlan>();
        softwarePlanList.add(softwarePlan1);
        softwarePlanList.add(softwarePlan2);

        given(softwarePlanRepository.findAll(any(SoftwarePlanSpecification.class), any(Sort.class)))
                .willReturn(softwarePlanList);

        List<SoftwarePlan> result = softwarePlanService.getList(spec, sort);
        assertEquals(softwarePlanList, result);

        verify(softwarePlanRepository).findAll(any(SoftwarePlanSpecification.class), any(Sort.class));
    }

    @Test
    public void delete() {
        softwarePlanService.delete(7L);

        verify(softwarePlanRepository).deleteById(7L);
    }

    @Test
    public void getPricePerMonth() {
        given(softwarePlanRepository.pricePerMonth(any(String.class), any(String.class))).willReturn(7L);

        Long result = softwarePlanService.getPricePerMonth("1", "3");
        assertEquals(Long.valueOf(7L), result);

        verify(softwarePlanRepository).pricePerMonth(any(String.class), any(String.class));
    }

    @Test
    public void getCurrentSoftwarePlanList() {
        SoftwarePlanSpecification spec = new SoftwarePlanSpecification();
        spec.setSoftwareId(2L);

        SoftwarePlan softwarePlan1 = softwarePlan(1L, 2L);
        SoftwarePlan softwarePlan2 = softwarePlan(2L, 2L);

        List<SoftwarePlan> softwarePlanList = new ArrayList<SoftwarePlan>();
        softwarePlanList.add(softwarePlan1);
        softwarePlanList.add(softwarePlan2);

        given(softwarePlanRepository.findCurrentSoftwarePlanList(any(Long.class))).willReturn(softwarePlanList);

        List<SoftwarePlan> result = softwarePlanService.getCurrentSoftwarePlanList(spec);
        assertEquals(softwarePlanList, result);

        verify(softwarePlanRepository).findCurrentSoftwarePlanList(any(Long.class));
    }

    @Test
    public void getApplyMonth() {
        SoftwarePlanSpecification spec = new SoftwarePlanSpecification();

        SoftwarePlan softwarePlan1 = softwarePlan(1L, 2L);
        SoftwarePlan softwarePlan2 = softwarePlan(2L, 2L);

        List<SoftwarePlan> softwarePlanList = new ArrayList<SoftwarePlan>();
        softwarePlanList.add(softwarePlan1);
        softwarePlanList.add(softwarePlan2);

        given(softwarePlanRepository.findAll(any(SoftwarePlanSpecification.class))).willReturn(softwarePlanList);

        List<SoftwarePlan> result = softwarePlanService.getApplyMonth(spec);
        assertEquals(softwarePlanList, result);

        verify(softwarePlanRepository).findAll(any(SoftwarePlanSpecification.class));
    }

    @Test
    public void getMinPricePerMonth() {
        given(softwarePlanRepository.minPricePerMonth(any(String.class))).willReturn(7L);

        Long result = softwarePlanService.getMinPricePerMonth("2");
        assertEquals(Long.valueOf(7L), result);

        verify(softwarePlanRepository).minPricePerMonth(any(String.class));
    }

    @Test
    public void getPricePerMonthList() {
        List<Object[]> priceList = new ArrayList<>();
        priceList.add(new String[] { "1", "2500" });
        priceList.add(new String[] { "2", "3000" });
        Map<String, Long> prices = new TreeMap<>();
        prices.put("1", 2500L);
        prices.put("2", 3000L);
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        ids.add(2L);

        given(softwarePlanRepository.pricePerMonthList(anyList())).willReturn(priceList);

        Map<String, Long> result = softwarePlanService.getPricePerMonthList(ids);
        assertEquals(prices, result);

        verify(softwarePlanRepository).pricePerMonthList(anyList());
    }

    @Test
    public void getPricePerMonthListNull() {
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        ids.add(2L);

        given(softwarePlanRepository.pricePerMonthList(anyList())).willReturn(null);

        Map<String, Long> result = softwarePlanService.getPricePerMonthList(ids);
        assertEquals(0, result.size());

        verify(softwarePlanRepository).pricePerMonthList(anyList());
    }

}
