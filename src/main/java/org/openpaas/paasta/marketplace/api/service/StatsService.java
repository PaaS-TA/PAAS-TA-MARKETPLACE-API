package org.openpaas.paasta.marketplace.api.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.openpaas.paasta.marketplace.api.domain.Instance;
import org.openpaas.paasta.marketplace.api.domain.Software;
import org.openpaas.paasta.marketplace.api.domain.Stats;
import org.openpaas.paasta.marketplace.api.domain.Stats.Term;
import org.openpaas.paasta.marketplace.api.repository.StatsRepository;
import org.openpaas.paasta.marketplace.api.repository.query.StatsQuery;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class StatsService {

    private final StatsRepository statsRepository;
    private final StatsQuery statsQuery;
    
    // 총 등록된 상품 수
    public long countOfTotalSws() {
        return statsRepository.countOfTotalSws();
    }

    public long countOfSwsCurrent() {
        return statsRepository.countOfSws(Software.Status.Approval);
    }

    public long countOfInstsCurrent() {
        return statsRepository.countOfInsts(Instance.Status.Approval);
    }
    
    /**
     * 현재 사용중인 상품 카운트
     * @param categoryId
     * @param srchStartDate
     * @param srchEndDate
     * @return
     */
    public long queryCountOfInstsCurrent(String categoryId, String srchStartDate, String srchEndDate) {
    	return statsQuery.queryCountOfInstsCurrent(categoryId, srchStartDate, srchEndDate);
    }

    public long countOfUsersCurrent() {
        return statsRepository.countOfUsers(Instance.Status.Approval);
    }
    
    /**
     * 현재 상품을 사용중인 User 카운트
     * @param categoryId
     * @param srchStartDate
     * @param srchEndDate
     * @return
     */
    public long queryCountOfUsersUsing(String categoryId, String srchStartDate, String srchEndDate) {
    	return statsQuery.queryCountOfUsersUsing(categoryId, srchStartDate, srchEndDate);
    }

    public long countOfProvidersCurrent() {
        return statsRepository.countOfProviders(Software.Status.Approval);
    }

    public long soldInstanceCountOfSw(Long id) {
        return statsRepository.soldInstanceCountOfSw(id);
    }

    public Map<String, Long> countsOfInstsProviderCurrent(List<String> idIn) {
        List<Object[]> values = statsRepository.countsOfInstsByProviderIds(Instance.Status.Approval, idIn);
        Map<String, Long> data = values.stream().collect(Collectors.toMap(v -> (String) v[0], v -> (Long) v[1]));

        return data;
    }

    public Map<String, Long> countsOfInstsUserCurrent(List<String> idIn) {
        List<Object[]> values = statsRepository.countsOfInstsByUserIds(Instance.Status.Approval, idIn);
        Map<String, Long> data = values.stream().collect(Collectors.toMap(v -> (String) v[0], v -> (Long) v[1]));

        return data;
    }

    public Map<Long, Long> countsOfInstsCurrent(List<Long> idIn) {
        List<Object[]> values = statsRepository.countsOfInsts(Instance.Status.Approval, idIn);
        Map<Long, Long> data = values.stream().collect(Collectors.toMap(v -> (Long) v[0], v -> (Long) v[1]));

        return data;
    }

    public Map<Long, Long> countsOfInstsCurrent(String providerId, List<Long> idIn) {
        List<Object[]> values = statsRepository.countsOfInsts(providerId, Instance.Status.Approval, idIn);
        Map<Long, Long> data = values.stream().collect(Collectors.toMap(v -> (Long) v[0], v -> (Long) v[1]));

        return data;
    }

    public long countOfSwsCurrent(String providerId) {
        return statsRepository.countOfSws(providerId, Software.Status.Approval);
    }

    public long countOfInstsCurrent(String providerId) {
        return statsRepository.countOfInsts(providerId);
    }

    public long countOfUsersCurrent(String providerId) {
        return statsRepository.countOfUsers(providerId, Instance.Status.Approval);
    }

    public Map<String, Long> countsOfTotalSwsProvider(int maxResults) {
        if (maxResults == -1) {
            maxResults = Integer.MAX_VALUE;
        }

        List<Object[]> values = statsRepository.countsOfTotalSwsProvider(PageRequest.of(0, maxResults));
        Map<String, Long> data = new LinkedHashMap<>();
        for (Object[] v : values) {
            data.put((String) v[0], (Long) v[1]);
        }

        return data;
    }

    public Map<String, Long> countsOfSwsProvider(int maxResults) {
        if (maxResults == -1) {
            maxResults = Integer.MAX_VALUE;
        }

        List<Object[]> values = statsRepository.countsOfSwsProvider(Software.Status.Approval,
                PageRequest.of(0, maxResults));
        Map<String, Long> data = new LinkedHashMap<>();
        for (Object[] v : values) {
            data.put((String) v[0], (Long) v[1]);
        }

        return data;
    }

    public Map<String, Long> countsOfInstsProvider(int maxResults) {
        if (maxResults == -1) {
            maxResults = Integer.MAX_VALUE;
        }

        //List<Object[]> values = statsRepository.countsOfInstsProvider(Instance.Status.Approval,
        //        PageRequest.of(0, maxResults));
        List<Object[]> values = statsRepository.countsOfTotalInstsProvider(PageRequest.of(0, maxResults));
        Map<String, Long> data = new LinkedHashMap<>();
        for (Object[] v : values) {
            data.put((String) v[0], (Long) v[1]);
        }

        return data;
    }

    public Map<String, Long> countsOfInstsUser(int maxResults) {
        if (maxResults == -1) {
            maxResults = Integer.MAX_VALUE;
        }

        List<Object[]> values = statsRepository.countsOfInstsUser(Instance.Status.Approval,
                PageRequest.of(0, maxResults));
        Map<String, Long> data = new LinkedHashMap<>();
        for (Object[] v : values) {
            data.put((String) v[0], (Long) v[1]);
        }

        return data;
    }

    public Map<String, Long> countsOfInstsSumUser(int maxResults) {
        if (maxResults == -1) {
            maxResults = Integer.MAX_VALUE;
        }

        List<Object[]> values = statsRepository.countsOfInstsSumUsers(PageRequest.of(0, maxResults));
        Map<String, Long> data = new LinkedHashMap<>();
        for (Object[] v : values) {
            data.put((String) v[0], (Long) v[1]);
        }

        return data;
    }

    public Map<Long, Long> countsOfInstsCurrent(int maxResults) {
        if (maxResults == -1) {
            maxResults = Integer.MAX_VALUE;
        }

        List<Object[]> values = statsRepository.countsOfInsts(Instance.Status.Approval,
                PageRequest.of(0, maxResults));
        Map<Long, Long> data = new LinkedHashMap<>();
        for (Object[] v : values) {
            data.put((Long) v[0], (Long) v[1]);
        }

        return data;
    }

    public Map<Long, Long> countsOfInstsCurrent(String providerId, int maxResults) {
        if (maxResults == -1) {
            maxResults = Integer.MAX_VALUE;
        }

        List<Object[]> values = statsRepository.countsOfInsts(providerId, Instance.Status.Approval,
                PageRequest.of(0, maxResults));
        Map<Long, Long> data = new LinkedHashMap<>();
        for (Object[] v : values) {
            data.put((Long) v[0], (Long) v[1]);
        }

        return data;
    }

    public List<Long> countsOfInsts(List<Term> terms, boolean using) {
        List<Long> vs = new ArrayList<>();
        terms.forEach(t -> {
            long v = statsRepository.countOfInsts(t.getStart(), t.getEnd(), using);
            vs.add(v);
        });

        return vs;
    }

    public List<Long> countsOfInstsUser(String createdId, List<Term> terms, boolean using) {
        List<Long> vs = new ArrayList<>();
        terms.forEach(t -> {
            long v = statsRepository.countOfInstsUser(createdId, t.getStart(), t.getEnd(), using);
            vs.add(v);
        });

        return vs;
    }

    //countsOfInstsUser :: countOfInstsUserApprovalList
    public  Map<String, List<Long>> countsOfInstsUsers(List<String> createdBy, List<Term> terms, boolean using) {
        Map<Pair<String, String>, Long> values = new HashMap<>();
        Set<String> ids = new TreeSet<>();
        terms.forEach(t -> {
            List<Object[]> vs = statsRepository.countOfInstsUsers(createdBy, t.getStart(), t.getEnd(),using);
            vs.forEach(v -> {
                String id =  (String)v[0];
                Long count = (Long) v[1];
                ids.add(id);
                Pair<String, String> pair = Pair.of(id, Stats.toString(t.getStart()));
                values.put(pair, count);
            });
        });

        Map<String, List<Long>> datas = new HashMap<>();
        ids.forEach(id -> {
            List<Long> data = new ArrayList<>();
            terms.forEach(t -> {
                Long count = values.getOrDefault(Pair.of(id, Stats.toString(t.getStart())), 0L);
                data.add(count);
            });
            datas.put(id, data);
        });

        return datas;
    }

    public Map<Long, List<Long>> countsOfInsts(List<Long> idIn, List<Term> terms, boolean using) {
        Map<Pair<Long, String>, Long> values = new HashMap<>();
        Set<Long> ids = new TreeSet<>();
        terms.forEach(t -> {
            List<Object[]> vs = statsRepository.countsOfInsts(idIn, t.getStart(), t.getEnd(), using);
            vs.forEach(v -> {
                Long id = (Long) v[0];
                Long count = (Long) v[1];
                ids.add(id);
                Pair<Long, String> pair = Pair.of(id, Stats.toString(t.getStart()));
                values.put(pair, count);
            });
        });

        Map<Long, List<Long>> datas = new HashMap<>();
        ids.forEach(id -> {
            List<Long> data = new ArrayList<>();
            terms.forEach(t -> {
                Long count = values.getOrDefault(Pair.of(id, Stats.toString(t.getStart())), 0L);
                data.add(count);
            });
            datas.put(id, data);
        });

        return datas;
    }

    public List<Long> countsOfInstsProvider(List<Long> idIn, List<Term> terms, boolean using) {
        Map<String, Long> values = new LinkedHashMap<>();
        Set<Long> ids = new TreeSet<>();
        terms.forEach(t -> {
            List<Object[]> vs = statsRepository.countsOfInsts(idIn, t.getStart(), t.getEnd(), using);

            final Long[] count = {0L};
            final Long[] count2 = {0L};
            vs.forEach(v -> {
                Long id = (Long) v[0];
                count2[0] = (Long) v[1];
                ids.add(id);
                count[0] += count2[0];
            });

            String pair = Stats.toString(t.getStart());
            values.put(pair, count[0]);
        });

        List<Long> data = new ArrayList<>();
        terms.forEach(t -> {
            Long count = values.getOrDefault(Stats.toString(t.getStart()), 0L);
            data.add(count);
        });

        return data;
    }

    public Map<Long, List<Long>> countsOfInsts(String providerId, List<Long> idIn, List<Term> terms, boolean using) {
        Map<Pair<Long, String>, Long> values = new HashMap<>();
        Set<Long> ids = new TreeSet<>();
        terms.forEach(t -> {
            List<Object[]> vs = statsRepository.countsOfInsts(providerId, idIn, t.getStart(), t.getEnd(), using);
            vs.forEach(v -> {
                Long id = (Long) v[0];
                Long count = (Long) v[1];
                ids.add(id);
                Pair<Long, String> pair = Pair.of(id, Stats.toString(t.getStart()));
                values.put(pair, count);
            });
        });

        Map<Long, List<Long>> datas = new HashMap<>();
        ids.forEach(id -> {
            List<Long> data = new ArrayList<>();
            terms.forEach(t -> {
                Long count = values.getOrDefault(Pair.of(id, Stats.toString(t.getStart())), 0L);
                data.add(count);
            });
            datas.put(id, data);
        });

        return datas;
    }

    public Map<Long, Long> getSoldInstanceCount(List<Long> idIn) {
        List<Object[]> values = statsRepository.countsOfSodInsts(idIn);
        Map<Long, Long> data = values.stream().collect(Collectors.toMap(v -> (Long) v[0], v -> (Long) v[1]));

        return data;
    }

    /**
     * 구매 상품 사용한 일(Day) 수
     *
     * @param providerId
     * @param idIn
     * @return
     */
    public Map<Long, Integer> getDayOfUseInstsPeriod(String providerId, List<Long> idIn) {
        List<Object[]> values = statsRepository.dayOfUseInstsPeriod(providerId, idIn);
        Map<Long, Integer> data = values.stream().collect(Collectors.toMap(v -> (Long) v[0], v -> (Integer) v[1]));
        return data;
    }
    
    /**
     * 구매 상품 사용한 일(Day) 수 - 입력한 달 기준
     * @param providerId
     * @param idIn
     * @param usageStartDate
     * @param usageEndDate
     * @return
     */
    public Map<String, String> getDayOfUseInstsPeriod(String providerId, List<Long> idIn, String usageStartDate, String usageEndDate) {
    	List<Object[]> values = statsRepository.dayOfUseInstsPeriodMonth(providerId, idIn, usageStartDate, usageEndDate);
    	Map<String, String> data = new HashMap<String, String>();
    	if (values != null && !values.isEmpty()) {
    		data = values.stream().collect(Collectors.toMap(v -> (String) v[0], v -> (String) v[1]));
    	}
    	return data;
    }

    /**
     * [Admin] 판매자가 판매한 상품 중 사용중(status = Approval)인 상품 수
     *
     * @param providerId
     * @param idIn
     * @return
     */
    public Map<Long, Object> getUsingPerInstanceByProvider(String providerId, List<Long> idIn) {
        Object obj;
        Map<Long, Object> data = new HashMap<>();
        for (Long id:idIn) {
            obj = statsRepository.usingPerInstanceByProvider(providerId, id);
            data.put(id, obj);
        }

        return data;
    }


    /**
     * 판매자의 상품별 총 판매량(사용 + 중지)
     *
     * @param providerId
     * @param idIn
     * @return
     */
    public Map<Long, Object> soldInstanceByProvider(String providerId, List<Long> idIn) {
        Object obj;
        Map<Long, Object> data = new HashMap<>();
        for (Long id:idIn) {
            obj = statsRepository.soldInstanceByProvider(providerId, id);
            data.put(id, obj);
        }

        return data;
    }

    public Map<Long, Long> getSalesAmount(String providerId, List<Long> idIn, LocalDateTime start, LocalDateTime end) {
    	LocalDateTime diffEnd = end.minusDays(1);

    	if (start.getMonthValue() != diffEnd.getMonthValue()) {
            throw new IllegalStateException("Invalid term: start=" + start + ", end=" + end);
        }

        int lengthOfMonth = start.toLocalDate().lengthOfMonth();

        Map<Long, Long> data = new HashMap<>();

        List<Object[]> values = statsRepository.getSalesAmount(providerId, idIn, start, end, diffEnd);
        for (Object[] value : values) {
            Long id = (Long) value[0];
            Long pricePerMonth = (Long) value[1];
            Long days = (Long) value[2];
            long price = (long) (Math.ceil(pricePerMonth * ((double) days / lengthOfMonth)));

            data.put(id, price);
        }

        return data;
    }

    // 요금 통계
    public Map<Long, Object> getPurchaseAmount(String createrId, LocalDateTime start, LocalDateTime end) {
        LocalDateTime startDate = start;
        LocalDateTime endDate = end.minusDays(1);
 
        //int lengthOfMonth = start.toLocalDate().lengthOfMonth();
        long i = 0;

        Map<Long, Object> data = new HashMap<>();

        List<Object[]> values = statsRepository.getPurchaseAmount(createrId, startDate, endDate);
        if (!CollectionUtils.isEmpty(values)) {
	        for (Object[] value : values) { 
	            data.put(i, value); 
	            i++;
	        }
        }

        return data;
    }
    
    /**
     * Seller 요금통계 정보조회 총카운터
     * @param userId
     * @param categoryId
     * @param srchDate
     * @param page
     * @param size
     * @return
     */
    public Integer querySoftwareSellPriceTotalCount(String userId, String categoryId, String srchDate, Integer page, Integer size) {
    	return statsQuery.querySoftwareSellPriceTotalCount(userId, categoryId, srchDate, page, size);
    }
    
    /**
     * Seller 요금통계 정보조회 리스트
     * @param userId
     * @param categoryId
     * @param srchDate
     * @param page
     * @param size
     * @return
     */
    public List<Map<String, Object>> querySoftwareSellPriceList(String userId, String categoryId, String srchDate, Integer page, Integer size) {
    	return statsQuery.querySoftwareSellPriceList(userId, categoryId, srchDate, page, size);
    }
 
    /**
     * 사용자별 구매 퍼센트 통계
     * @param userId
     * @return
     */
    public List<Map<String, Object>> getPurchaserPercent() {
    	return statsQuery.queryPurchaserPercent();
    }
    
    /**
     * 월별 상품구매 통계
     * @param userId
     * @return
     */
    public List<Map<String, Object>> getPurchaseTransitionMonth() {
    	return statsQuery.queryPurchaseTransitionMonth();
    }
    
    /**
     * 앱사용 사용자 추이
     * @param userId
     * @return
     */
    public List<Map<String, Object>> getUsageTransition() {
    	return statsQuery.queryUsageTransition();
    }
}
