package org.openpaas.paasta.marketplace.api.controller;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.openpaas.paasta.marketplace.api.domain.Software;
import org.openpaas.paasta.marketplace.api.domain.SoftwareSpecification;
import org.openpaas.paasta.marketplace.api.domain.Stats;
import org.openpaas.paasta.marketplace.api.domain.Stats.Term;
import org.openpaas.paasta.marketplace.api.service.InstanceService;
import org.openpaas.paasta.marketplace.api.service.SoftwareService;
import org.openpaas.paasta.marketplace.api.service.StatsService;
import org.openpaas.paasta.marketplace.api.util.SecurityUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    private final SoftwareService softwareService;

    private final InstanceService instanceService;

    @GetMapping("/softwares/my/counts/sum")
    public long countOfSwsUsingProvider() {
        String providerId = SecurityUtils.getUserId();

        long count = statsService.countOfSwsCurrent(providerId);

        return count;
    }

    @GetMapping("/instances/my/counts/sum")
    public long countOfInstsUsingProvider() {
        String providerId = SecurityUtils.getUserId();

        long count = statsService.countOfInstsCurrent(providerId);

        return count;
    }

    @GetMapping("/users/my/counts/sum")
    public long countOfUsersUsingProvider() {
        String providerId = SecurityUtils.getUserId();

        long count = statsService.countOfUsersCurrent(providerId);

        return count;
    }

    @GetMapping("/software/{id}/sold/counts/sum")
    public long soldInstanceCountOfSw(@PathVariable Long id) {
        long count = statsService.soldInstanceCountOfSw(id);

        return count;
    }


    @GetMapping("/instances/my/counts")
    public Map<Long, Long> countsOfInstsUsingProvider(
            @RequestParam(name = "maxResults", required = false, defaultValue = "-1") int maxResults) {
        String providerId = SecurityUtils.getUserId();

        return statsService.countsOfInstsCurrent(providerId, maxResults);
    }

    @GetMapping("/instances/my/counts/ids")
    public Map<Long, Long> countsOfInstsUsingProvider(@RequestParam(name = "idIn", required = false) List<Long> idIn) {
        String providerId = SecurityUtils.getUserId();

        return statsService.countsOfInstsCurrent(providerId, idIn);
    }

    @GetMapping("/instances/my/counts/months")
    public Map<String, Object> countsOfInstsProviderMonthly(
            @RequestParam(name = "idIn", required = false) List<Long> idIn,
            @RequestParam(name = "epoch", required = false) LocalDateTime epoch,
            @RequestParam(name = "size", required = false, defaultValue = "6") int size) {
        String providerId = SecurityUtils.getUserId();

        List<Term> terms = Stats.termsOf(epoch, size, ChronoUnit.MONTHS);

        return countsOfInsts(providerId, idIn, terms, true);
    }

    @GetMapping("/instances/my/counts/days")
    public Map<String, Object> countsOfInstsProviderDaily(
            @RequestParam(name = "idIn", required = false) List<Long> idIn,
            @RequestParam(name = "epoch", required = false) LocalDateTime epoch,
            @RequestParam(name = "size", required = false, defaultValue = "30") int size) {
        String providerId = SecurityUtils.getUserId();

        List<Term> terms = Stats.termsOf(epoch, size, ChronoUnit.DAYS);

        return countsOfInsts(providerId, idIn, terms, true);
    }

    @GetMapping("/softwares/my")
    public Map<Long, Software> getSoftwares(@RequestParam(name = "idIn", required = false) List<Long> idIn) {
        String providerId = SecurityUtils.getUserId();

        SoftwareSpecification spec = new SoftwareSpecification();
        spec.setInUse(null);
        spec.setCreatedBy(providerId);
        spec.setIdIn(idIn);

        List<Software> softwareList = softwareService
                .getPage(spec, PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Direction.ASC, "id"))).getContent();

        Map<Long, Software> softwares = softwareList.stream().collect(Collectors.toMap(s -> s.getId(), s -> s));

        return softwares;
    }

    private Map<String, Object> countsOfInsts(String providerId, List<Long> idIn, List<Term> terms, boolean using) {
        List<String> termStrings = terms.stream().map(t -> Stats.toString(t.getStart())).collect(Collectors.toList());

        Map<Long, List<Long>> counts = statsService.countsOfInsts(providerId, idIn, terms, using);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("terms", termStrings);
        data.put("counts", counts);

        return data;
    }

    /**
     * 구매 상품 사용한 일(Day) 수
     *
     * @param idIn
     * @return
     */
    @GetMapping("/instances/my/usePeriod/days")
    public Map<String, String> getDayOfUseInstsPeriod(@RequestParam(name = "idIn", required = false) List<Long> idIn
    		,@RequestParam(name = "usageStartDate", required = false) String usageStartDate
    		,@RequestParam(name = "usageEndDate", required = false) String usageEndDate) {

    	if (idIn == null || idIn.isEmpty()) {
    		return new HashMap<String, String>();
    	}
    	if (StringUtils.isBlank(usageStartDate) || StringUtils.isBlank(usageEndDate)) {
    		return new HashMap<String, String>();
    	}

        String providerId = SecurityUtils.getUserId();
        return statsService.getDayOfUseInstsPeriod(providerId, idIn, usageStartDate, usageEndDate);
    }


    /**
     * 판매자의 상품별 총 판매량(사용 + 중지)
     *
     * @param idIn
     * @return
     */
    @GetMapping("/instances/sold/count")
    public Map<Long, Object> soldInstanceByProvider(@RequestParam(name = "idIn", required = false) List<Long> idIn) {
        String providerId = SecurityUtils.getUserId();
        return statsService.soldInstanceByProvider(providerId, idIn);
    }

    @GetMapping("/softwares/sales-amount")
    public Map<Long, Long> getSalesAmount(@RequestParam(name = "idIn", required = false) List<Long> idIn,
            @RequestParam(name = "usageStartDate", required = true) LocalDateTime start,
            @RequestParam(name = "usageEndDate", required = true) LocalDateTime end) {
        String providerId = SecurityUtils.getUserId();
        return statsService.getSalesAmount(providerId, idIn, start, end);
    }

    // 요금 통계
    @GetMapping("/instances/my/price/total")
    public Map<Long, Object> getPurchaseAmount(
            @RequestParam(name = "usageStartDate", required = true) LocalDateTime usageStartDate,
            @RequestParam(name = "usageEndDate", required = true) LocalDateTime usageEndDate) {
        String createrId = SecurityUtils.getUserId();
        return statsService.getPurchaseAmount(createrId, usageStartDate, usageEndDate);
    }

    @GetMapping("/{softwareId}/softwareUsagePriceTotal")
    public long getSoftwareUsagePriceTotal(@PathVariable Long softwareId) {
        long count = instanceService.getSoftwareUsagePriceTotal(softwareId);

        return count;
    }
    
    /**
     * Seller 요금통계 정보조회 리스트
     * @param categoryId
     * @param page
     * @param size
     * @param srchDate
     * @return
     */
    @GetMapping("/softwareSellPriceList")
    public List<Map<String,Object>> getsoftwareSellPriceList(@RequestParam(name="categoryId",required=false) String categoryId
    														,@RequestParam(name="page") Integer page
    														,@RequestParam(name="size") Integer size
    														,@RequestParam(name="srchDate",required=false) String srchDate) {
    	return statsService.querySoftwareSellPriceList(SecurityUtils.getUserId(), categoryId, srchDate, page, size);
    }
    
    /**
     * Seller 요금통계 정보조회 총카운트
     * @param categoryId
     * @param page
     * @param size
     * @param srchDate
     * @return
     */
    @GetMapping("/softwareSellPriceTotalCount")
    public Integer getsoftwareSellPriceTotalCount(@RequestParam(name="categoryId",required=false) String categoryId
    		,@RequestParam(name="page") Integer page
    		,@RequestParam(name="size") Integer size
    		,@RequestParam(name="srchDate",required=false) String srchDate) {
    	return statsService.querySoftwareSellPriceTotalCount(SecurityUtils.getUserId(), categoryId, srchDate, page, size);
    }

    /**
     * 사용자별 구매 퍼센트 통계
     * @return
     */
    @GetMapping("/purchaserPercent")
    public List<Map<String,Object>> purchaserPercent() {
    	return statsService.getPurchaserPercent();
    }
    
    /**
     * 월별 상품구매 통계
     * @return
     */
    @GetMapping("/purchaseTransitionMonth")
    public List<Map<String,Object>> purchaseTransitionMonth() {
    	return statsService.getPurchaseTransitionMonth();
    }
    
    /**
     * 앱사용 사용자 추이
     * @return
     */
    @GetMapping("/usageTransition")
    public List<Map<String,Object>> usageTransition() {
    	return statsService.getUsageTransition();
    }

    /**
     * 현재 사용중인 상품 카운트
     * @param categoryId
     * @param srchStartDate
     * @param srchEndDate
     * @return
     */
    @GetMapping("/instances/counts/sum")
    public long countOfInstsUsing(@RequestParam(name="categoryId", required=false) String categoryId
    							, @RequestParam(name="srchStartDate", required=false) String srchStartDate
    							, @RequestParam(name="srchEndDate", required=false) String srchEndDate) {
    	return statsService.queryCountOfInstsCurrent(categoryId, srchStartDate, srchEndDate, SecurityUtils.getUserId());
    }

    /**
     * 현재 상품을 사용중인 User 카운트
     * @return
     */
    @GetMapping("/users/counts/sum")
    public long countOfUsersUsing(@RequestParam(name="categoryId", required=false) String categoryId
								, @RequestParam(name="srchStartDate", required=false) String srchStartDate
								, @RequestParam(name="srchEndDate", required=false) String srchEndDate) {
    	return statsService.queryCountOfUsersUsing(categoryId, srchStartDate, srchEndDate, SecurityUtils.getUserId());
    }
}
