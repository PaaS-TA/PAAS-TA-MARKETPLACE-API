
package org.openpaas.paasta.marketplace.api.controller;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.openpaas.paasta.marketplace.api.domain.Software;
import org.openpaas.paasta.marketplace.api.domain.SoftwareSpecification;
import org.openpaas.paasta.marketplace.api.domain.Stats;
import org.openpaas.paasta.marketplace.api.domain.Stats.Term;
import org.openpaas.paasta.marketplace.api.domain.User;
import org.openpaas.paasta.marketplace.api.domain.UserSpecification;
import org.openpaas.paasta.marketplace.api.service.SoftwareService;
import org.openpaas.paasta.marketplace.api.service.StatsService;
import org.openpaas.paasta.marketplace.api.service.UserService;
import org.openpaas.paasta.marketplace.api.util.SecurityUtils;
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

    @GetMapping("/instances/counts/sum")
    public long countOfInstsUsing() {
        long count = statsService.countOfInstsCurrent();

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

    @GetMapping("/users/counts/sum")
    public long countOfUsersUsing() {
        long count = statsService.countOfUsersCurrent();

        return count;
    }

    @GetMapping("/providers/counts/sum")
    public long countOfProvidersUsing() {
        long count = statsService.countOfProvidersCurrent();

        return count;
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

    @GetMapping("/users/instances/counts")
    public Map<String, Long> countsOfInstsUser(
            @RequestParam(name = "maxResults", required = false, defaultValue = "-1") int maxResults) {
        return statsService.countsOfInstsUser(maxResults);
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
    public Map<String, Object> countsOfInstsMonthly(@RequestParam(name = "epoch", required = false) LocalDateTime epoch,
            @RequestParam(name = "size", required = false, defaultValue = "12") int size) {
        List<Term> terms = Stats.termsOf(epoch, size, ChronoUnit.MONTHS);

        return countsOfInsts(terms, true);
    }

    @GetMapping("/instances/sum/days")
    public Map<String, Object> countOfInstsDaily(@RequestParam(name = "epoch", required = false) LocalDateTime epoch,
            @RequestParam(name = "size", required = false, defaultValue = "180") int size) {
        List<Term> terms = Stats.termsOf(epoch, size, ChronoUnit.DAYS);

        return countsOfInsts(terms, true);
    }

    @GetMapping("/instances/sum/user/months")
    public Map<String, Object> countsOfInstsUserMonthly(@RequestParam(name = "createdBy") String createdBy, @RequestParam(name = "epoch", required = false) LocalDateTime epoch,
            @RequestParam(name = "size", required = false, defaultValue = "12") int size) {
        List<Term> terms = Stats.termsOf(epoch, size, ChronoUnit.MONTHS);

        return countsOfInstsUser(createdBy, terms, true);
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

}
