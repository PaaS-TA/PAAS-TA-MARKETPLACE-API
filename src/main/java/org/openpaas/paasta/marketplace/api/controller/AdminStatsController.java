
package org.openpaas.paasta.marketplace.api.controller;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.openpaas.paasta.marketplace.api.domain.Software;
import org.openpaas.paasta.marketplace.api.domain.SoftwareSpecification;
import org.openpaas.paasta.marketplace.api.domain.Stats;
import org.openpaas.paasta.marketplace.api.domain.Stats.Term;
import org.openpaas.paasta.marketplace.api.domain.User;
import org.openpaas.paasta.marketplace.api.domain.UserSpecification;
import org.openpaas.paasta.marketplace.api.service.SoftwareService;
import org.openpaas.paasta.marketplace.api.service.StatsService;
import org.openpaas.paasta.marketplace.api.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/admin/stats")
@RequiredArgsConstructor
public class AdminStatsController {

    private final StatsService statsService;

    private final SoftwareService softwareService;

    private final UserService userService;


    @GetMapping("/softwares/counts/total/sum")
    public long countOfTotalSws() {
        long count = statsService.countOfTotalSws();

        return count;
    }

    @GetMapping("/softwares/counts/sum")
    public long countOfSwsUsing() {
        long count = statsService.countOfSwsCurrent();

        return count;
    }

    @GetMapping("/softwares/counts/provider")
    public long countOfSwsUsingProvider(@RequestParam(name = "providerId") String providerId) {
        long count = statsService.countOfSwsCurrent(providerId);

        return count;
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
        long count = 0;
        if (StringUtils.isNotBlank(categoryId) || (StringUtils.isNotBlank(srchStartDate) && StringUtils.isNotBlank(srchEndDate))) {
        	count = statsService.queryCountOfInstsCurrent(categoryId, srchStartDate, srchEndDate);
        } else {
        	count = statsService.countOfInstsCurrent();
        }
        return count;
    }


    /**
     * 한 명의 판매자의 총 판매량
     *
     * @param providerId
     * @return
     */
    @GetMapping("/instances/counts/provider")
    public long countOfInstsUsingProvider(@RequestParam(name = "providerId") String providerId) {
        long count = statsService.countOfInstsCurrent(providerId);

        return count;
    }

    /**
     * 현재 상품을 사용중인 User 카운트
     * @return
     */
    @GetMapping("/users/counts/sum")
    public long countOfUsersUsing(@RequestParam(name="categoryId", required=false) String categoryId
								, @RequestParam(name="srchStartDate", required=false) String srchStartDate
								, @RequestParam(name="srchEndDate", required=false) String srchEndDate) {
    	long count = 0;
        if (StringUtils.isNotBlank(categoryId) || (StringUtils.isNotBlank(srchStartDate) && StringUtils.isNotBlank(srchEndDate))) {
        	count = statsService.queryCountOfUsersUsing(categoryId, srchStartDate, srchEndDate);
        } else {
        	count = statsService.countOfUsersCurrent();
        }
        return count;
    }

    @GetMapping("/providers/counts/sum")
    public long countOfProvidersUsing() {
        long count = statsService.countOfProvidersCurrent();

        return count;
    }

    @GetMapping("/providers/total/softwares/counts")
    public Map<String, Long> countsOfTotalSwsProvider(
            @RequestParam(name = "maxResults", required = false, defaultValue = "-1") int maxResults) {
        return statsService.countsOfTotalSwsProvider(maxResults);
    }

    @GetMapping("/providers/softwares/counts")
    public Map<String, Long> countsOfSwsProvider(
            @RequestParam(name = "maxResults", required = false, defaultValue = "-1") int maxResults) {
        return statsService.countsOfSwsProvider(maxResults);
    }

    @GetMapping("/providers/instances/counts")
    public Map<String, Long> countsOfInstsProvider(
            @RequestParam(name = "maxResults", required = false, defaultValue = "-1") int maxResults) {
        return statsService.countsOfInstsProvider(maxResults);
    }

    //[Admin] 사용자 (status = Approval)인 상품 수
    @GetMapping("/users/instances/counts")
    public Map<String, Long> countsOfInstsUser(
            @RequestParam(name = "maxResults", required = false, defaultValue = "-1") int maxResults) {
        return statsService.countsOfInstsUser(maxResults);
    }

    //[Admin] 사용자 (status = Approval,Deleted)인 상품 수
    @GetMapping("/users/sum/instances/counts")
    public Map<String, Long> countsOfInstsSumUser(
            @RequestParam(name = "maxResults", required = false, defaultValue = "-1") int maxResults) {
        return statsService.countsOfInstsSumUser(maxResults);
    }

    @GetMapping("/providers/instances/counts/ids")
    public Map<String, Long> countsOfInstsProvider(@RequestParam(name = "idIn", required = false) List<String> idIn) {
        return statsService.countsOfInstsProviderCurrent(idIn);
    }

    @GetMapping("/users/instances/counts/ids")
    public Map<String, Long> countsOfInstsUser(@RequestParam(name = "idIn", required = false) List<String> idIn) {
        return statsService.countsOfInstsUserCurrent(idIn);
    }

    @GetMapping("/instances/counts")
    public Map<Long, Long> countsOfInsts(@RequestParam(name = "providerId", required = false) String providerId,
            @RequestParam(name = "maxResults", required = false, defaultValue = "-1") int maxResults) {
        if (providerId == null) {
            return statsService.countsOfInstsCurrent(maxResults);
        } else {
            return statsService.countsOfInstsCurrent(providerId, maxResults);
        }
    }

    @GetMapping("/instances/counts/ids")
    public Map<Long, Long> countsOfInsts(@RequestParam(name = "idIn", required = false) List<Long> idIn) {
        return statsService.countsOfInstsCurrent(idIn);
    }


    @GetMapping("/instances/sum/months")
    public Map<String, Object> countsOfInstsMonthly(
            @RequestParam(name = "epoch", required = false) LocalDateTime epoch,
            @RequestParam(name = "size", required = false, defaultValue = "12") int size) {
        List<Term> terms = Stats.termsOf(epoch, size, ChronoUnit.MONTHS);

        return countsOfInsts(terms, true);
    }


    @GetMapping("/instances/counts/months/ids")
    public Map<String, Object> countsOfInstCountMonthlyProvider(
            @RequestParam(name = "idIn", required = false) List<String> idIn,
            @RequestParam(name = "epoch", required = false) LocalDateTime epoch,
            @RequestParam(name = "size", required = false, defaultValue = "12") int size) {
        List<Term> terms = Stats.termsOf(epoch, size, ChronoUnit.MONTHS);

        return countsOfInstsProvider(idIn, terms, true);
    }

    @GetMapping("/instances/sum/months/ids")
    public Map<String, Object> countsOfInstsMonthlyProvider(
            @RequestParam(name = "idIn", required = false) List<String> idIn,
            @RequestParam(name = "epoch", required = false) LocalDateTime epoch,
            @RequestParam(name = "size", required = false, defaultValue = "6") int size) {
        List<Term> terms = Stats.termsOf(epoch, size, ChronoUnit.MONTHS);

        return countsOfInstsProvider(idIn, terms, true);
    }

    @GetMapping("/instances/sum/days")
    public Map<String, Object> countOfInstsDaily(@RequestParam(name = "epoch", required = false) LocalDateTime epoch,
            @RequestParam(name = "size", required = false, defaultValue = "180") int size) {
        List<Term> terms = Stats.termsOf(epoch, size, ChronoUnit.DAYS);

        return countsOfInsts(terms, true);
    }

    @GetMapping("/instances/sum/user/months")
    public Map<String, Object> countsOfInstsUserMonthly(
            @RequestParam(name = "createdBy") String createdBy,
            @RequestParam(name = "epoch", required = false) LocalDateTime epoch,
            @RequestParam(name = "size", required = false, defaultValue = "12") int size) {
        List<Term> terms = Stats.termsOf(epoch, size, ChronoUnit.MONTHS);

        return countsOfInstsUser(createdBy, terms, true);
    }

    //모든 사용자 년(월)도 그래프추이
    @GetMapping("/instances/sum/users/months")
    public Map<String, Object> countsOfInstsUsersMonthly(
            @RequestParam(name = "createdBy", required = false) List<String> createdBy,
            @RequestParam(name = "epoch", required = false) LocalDateTime epoch,
            @RequestParam(name = "size", required = false, defaultValue = "12") int size) {
        List<Term> terms = Stats.termsOf(epoch, size, ChronoUnit.MONTHS);

        return countsOfInstsUsers(createdBy, terms, true);
    }

    @GetMapping("/instances/sum/user/days")
    public Map<String, Object> countOfInstsUserDaily(@RequestParam(name = "createdBy") String createdBy, @RequestParam(name = "epoch", required = false) LocalDateTime epoch,
            @RequestParam(name = "size", required = false, defaultValue = "30") int size) {
        List<Term> terms = Stats.termsOf(epoch, size, ChronoUnit.DAYS);

        return countsOfInstsUser(createdBy, terms, true);
    }

    @GetMapping("/instances/counts/months")
    public Map<String, Object> countOfInstsMonthly(@RequestParam(name = "idIn", required = false) List<Long> idIn,
            @RequestParam(name = "epoch", required = false) LocalDateTime epoch,
            @RequestParam(name = "size", required = false, defaultValue = "6") int size) {
        List<Term> terms = Stats.termsOf(epoch, size, ChronoUnit.MONTHS);

        return countsOfInsts(idIn, terms, true);
    }

    @GetMapping("/instances/counts/days")
    public Map<String, Object> countsOfInstsDaily(@RequestParam(name = "idIn", required = false) List<Long> idIn,
            @RequestParam(name = "epoch", required = false) LocalDateTime epoch,
            @RequestParam(name = "size", required = false, defaultValue = "30") int size) {
        List<Term> terms = Stats.termsOf(epoch, size, ChronoUnit.DAYS);

        return countsOfInsts(idIn, terms, true);
    }

    @GetMapping("/softwares")
    public Map<Long, Software> getSoftwares(@RequestParam(name = "idIn", required = true) List<Long> idIn) {
        SoftwareSpecification spec = new SoftwareSpecification();
        spec.setInUse(null);
        spec.setIdIn(idIn);

        List<Software> softwareList = softwareService
                .getPage(spec, PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Direction.ASC, "id"))).getContent();

        Map<Long, Software> softwares = softwareList.stream().collect(Collectors.toMap(s -> s.getId(), s -> s));

        return softwares;
    }

    @GetMapping("/users")
    public Map<String, User> getUsers(@RequestParam(name = "idIn", required = true) List<String> idIn) {
        UserSpecification spec = new UserSpecification();
        spec.setIdIn(idIn);

        List<User> userList = userService
                .getUserList(spec, PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Direction.ASC, "id"))).getContent();

        Map<String, User> users = userList.stream().collect(Collectors.toMap(u -> u.getId(), u -> u));

        return users;
    }

    /**
     * 상품 총 팔린 개수
     *
     * @param idIn
     * @return
     */
    @GetMapping("/instances/totalSold/counts/ids")
    public Map<Long, Long> getSoldInstanceCount(@RequestParam(name = "idIn", required = false) List<Long> idIn) {
        return statsService.getSoldInstanceCount(idIn);
    }

    private Map<String, Object> countsOfInsts(List<Term> terms, boolean using) {
        List<String> termStrings = terms.stream().map(t -> Stats.toString(t.getStart())).collect(Collectors.toList());

        List<Long> counts = statsService.countsOfInsts(terms, using);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("terms", termStrings);
        data.put("counts", counts);

        return data;
    }

    private Map<String, Object> countsOfInstsUser(String createdBy, List<Term> terms, boolean using) {
        List<String> termStrings = terms.stream().map(t -> Stats.toString(t.getStart())).collect(Collectors.toList());

        List<Long> counts = statsService.countsOfInstsUser(createdBy, terms, using);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("terms", termStrings);
        data.put("counts", counts);

        return data;
    }

    private Map<String, Object> countsOfInstsUsers(List<String> createdBy, List<Term> terms, boolean using) {
        List<String> termStrings = terms.stream().map(t -> Stats.toString(t.getStart())).collect(Collectors.toList());

        Map<String, List<Long>> counts = statsService.countsOfInstsUsers(createdBy, terms, using);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("terms", termStrings);
        data.put("counts", counts);

        return data;
    }

    private Map<String, Object> countsOfInstsProvider(List<String> idIn, List<Term> terms, boolean using) {
        List<String> termStrings = terms.stream().map(t -> Stats.toString(t.getStart())).collect(Collectors.toList());

        Map<String, Object> data = new LinkedHashMap<>();
        Map<String, List<Long>> usingSwCount = new LinkedHashMap<>();

        if (idIn != null) {
            for (int i = 0; i < idIn.size(); i++) {
                List<Long> newIdIn = new ArrayList<>();
                // 1. 공급자 별로 본인이 등록한 software id 목록 추출
                List<Software> softwares = softwareService.getSwByCreatedBy(idIn.get(i));

                List<Long> counts;
                if (softwares.size() > 0) {
                    for (int j = 0; j < softwares.size(); j++) {
                        newIdIn.add(softwares.get(j).getId());
                    }

                    // 2. 상품 id 목록으로 월 별 사용량 조회
                    counts = statsService.countsOfInstsProvider(newIdIn, terms, using);
                } else {
                    counts = null;
                }
                usingSwCount.put(idIn.get(i), counts);
            }
        }

        data.put("counts", usingSwCount);
        data.put("terms", termStrings);


        return data;
    }

    private Map<String, Object> countsOfInsts(List<Long> idIn, List<Term> terms, boolean using) {
        List<String> termStrings = terms.stream().map(t -> Stats.toString(t.getStart())).collect(Collectors.toList());

        Map<Long, List<Long>> counts = statsService.countsOfInsts(idIn, terms, using);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("terms", termStrings);
        data.put("counts", counts);

        return data;
    }

    /**
     * [Admin] 판매자가 판매한 상품 중 사용중(status = Approval)인 상품 수
     *
     * @param idIn
     * @return
     */
    @GetMapping("/instances/usingCount/provider/{providerId}")
    public Map<Long, Object> getUsingPerInstanceByProvider(@PathVariable String providerId, @RequestParam(name = "idIn", required = false) List<Long> idIn) {
        return statsService.getUsingPerInstanceByProvider(providerId, idIn);
    }

    /**
     * 상품별 사용앱 데이터 조회 (Chart)
     * @param userId
     * @param categoryId
     * @param srchStartDate
     * @param srchEndDate
     * @return
     */
    @GetMapping("/softwares/chart/statsUseApp")
    public List<Map<String,Object>> statsUseApp(@RequestParam(name="userId", required=false) String userId
    										  , @RequestParam(name="categoryId", required=false) String categoryId
				    						  , @RequestParam(name="srchStartDate", required=false) String srchStartDate
				    						  , @RequestParam(name="srchEndDate", required=false) String srchEndDate) {
        return statsService.getStatsUseApp(userId, categoryId, srchStartDate, srchEndDate);
    }
    
    /**
     * 상품별 사용추이 데이터 조회 (Chart)
     * @param userId
     * @param categoryId
     * @param srchStartDate
     * @param srchEndDate
     * @return
     */
    @GetMapping("/softwares/chart/statsUseTransition")
    public List<Map<String,Object>> statsUseTransitionList(@RequestParam(name="userId", required=false) String userId
												    	 , @RequestParam(name="categoryId", required=false) String categoryId
												    	 , @RequestParam(name="srchStartDate", required=false) String srchStartDate
												    	 , @RequestParam(name="srchEndDate", required=false) String srchEndDate) {
    	return statsService.getStatsUseTransition(userId, categoryId, srchStartDate, srchEndDate);
    }

}
