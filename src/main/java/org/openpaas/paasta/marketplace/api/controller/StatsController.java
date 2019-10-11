package org.openpaas.paasta.marketplace.api.controller;

import lombok.RequiredArgsConstructor;
import org.openpaas.paasta.marketplace.api.domain.Software;
import org.openpaas.paasta.marketplace.api.domain.SoftwareSpecification;
import org.openpaas.paasta.marketplace.api.domain.Stats;
import org.openpaas.paasta.marketplace.api.domain.Stats.Term;
import org.openpaas.paasta.marketplace.api.service.SoftwareService;
import org.openpaas.paasta.marketplace.api.service.StatsService;
import org.openpaas.paasta.marketplace.api.util.SecurityUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    private final SoftwareService softwareService;

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
    public Map<Long, Integer> getDayOfUseInstsPeriod(@RequestParam(name = "idIn", required = false) List<Long> idIn) {
        String providerId = SecurityUtils.getUserId();
        return statsService.getDayOfUseInstsPeriod(providerId, idIn);
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
    public Map<Long, Long> _amount(@RequestParam(name = "idIn", required = false) List<Long> idIn,
            @RequestParam(name = "start", required = true) LocalDateTime start,
            @RequestParam(name = "end", required = true) LocalDateTime end) {
        String providerId = SecurityUtils.getUserId();
        return statsService.getSalesAmount(providerId, idIn, start, end);
    }

}
