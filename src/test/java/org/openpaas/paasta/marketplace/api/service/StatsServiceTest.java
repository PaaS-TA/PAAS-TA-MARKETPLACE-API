package org.openpaas.paasta.marketplace.api.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openpaas.paasta.marketplace.api.domain.Instance;
import org.openpaas.paasta.marketplace.api.domain.Software;
import org.openpaas.paasta.marketplace.api.domain.Stats;
import org.openpaas.paasta.marketplace.api.domain.Stats.Term;
import org.openpaas.paasta.marketplace.api.repository.StatsRepository;
import org.springframework.data.domain.Pageable;

public class StatsServiceTest extends AbstractMockTest {

    StatsService statsService;

    @Mock
    StatsRepository statsRepository;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        statsService = new StatsService(statsRepository);
    }

    @Test
    public void countOfTotalSws() {
        given(statsRepository.countOfTotalSws()).willReturn(7L);

        long result = statsService.countOfTotalSws();
        assertEquals(7L, result);

        verify(statsRepository).countOfTotalSws();
    }

    @Test
    public void countOfSwsCurrent() {
        given(statsRepository.countOfSws(Software.Status.Approval)).willReturn(7L);

        long result = statsService.countOfSwsCurrent();
        assertEquals(7L, result);

        verify(statsRepository).countOfSws(Software.Status.Approval);
    }

    @Test
    public void countOfInstsCurrent() {
        given(statsRepository.countOfInsts(Instance.Status.Approval)).willReturn(7L);

        long result = statsService.countOfInstsCurrent();
        assertEquals(7L, result);

        verify(statsRepository).countOfInsts(Instance.Status.Approval);
    }

    @Test
    public void countOfUsersCurrent() {
        given(statsRepository.countOfUsers(Instance.Status.Approval)).willReturn(7L);

        long result = statsService.countOfUsersCurrent();
        assertEquals(7L, result);

        verify(statsRepository).countOfUsers(Instance.Status.Approval);
    }

    @Test
    public void countOfProvidersCurrent() {
        given(statsRepository.countOfProviders(Software.Status.Approval)).willReturn(7L);

        long result = statsService.countOfProvidersCurrent();
        assertEquals(7L, result);

        verify(statsRepository).countOfProviders(Software.Status.Approval);
    }

    @Test
    public void soldInstanceCountOfSw() {
        given(statsRepository.soldInstanceCountOfSw(any(Long.class))).willReturn(7L);

        long result = statsService.soldInstanceCountOfSw(1L);
        assertEquals(7L, result);

        verify(statsRepository).soldInstanceCountOfSw(any(Long.class));
    }

    @Test
    public void countsOfInstsProviderCurrent() {
        List<String> ids = Arrays.asList("foo", "bar");
        given(statsRepository.countsOfInstsByProviderIds(Instance.Status.Approval, ids))
                .willReturn(Arrays.asList(new Object[] { "foo", 7L }, new Object[] { "bar", 13L }));

        Map<String, Long> result = statsService.countsOfInstsProviderCurrent(ids);
        assertEquals(Long.valueOf(7L), result.get("foo"));
        assertEquals(Long.valueOf(13L), result.get("bar"));

        verify(statsRepository).countsOfInstsByProviderIds(Instance.Status.Approval, ids);
    }

    @Test
    public void countsOfInstsUserCurrent() {
        List<String> ids = Arrays.asList("foo", "bar");
        given(statsRepository.countsOfInstsByUserIds(Instance.Status.Approval, ids))
                .willReturn(Arrays.asList(new Object[] { "foo", 7L }, new Object[] { "bar", 13L }));

        Map<String, Long> result = statsService.countsOfInstsUserCurrent(ids);
        assertEquals(Long.valueOf(7L), result.get("foo"));
        assertEquals(Long.valueOf(13L), result.get("bar"));

        verify(statsRepository).countsOfInstsByUserIds(Instance.Status.Approval, ids);
    }

    @Test
    public void countsOfInstsCurrent() {
        List<Long> ids = Arrays.asList(1L, 2L);
        given(statsRepository.countsOfInsts(Instance.Status.Approval, ids))
                .willReturn(Arrays.asList(new Object[] { 1L, 7L }, new Object[] { 2L, 13L }));

        Map<Long, Long> result = statsService.countsOfInstsCurrent(ids);
        assertEquals(Long.valueOf(7L), result.get(1L));
        assertEquals(Long.valueOf(13L), result.get(2L));

        verify(statsRepository).countsOfInsts(Instance.Status.Approval, ids);
    }

    @Test
    public void countsOfInstsCurrent2() {
        List<Long> ids = Arrays.asList(1L, 2L);
        given(statsRepository.countsOfInsts(userId, Instance.Status.Approval, ids))
                .willReturn(Arrays.asList(new Object[] { 1L, 7L }, new Object[] { 2L, 13L }));

        Map<Long, Long> result = statsService.countsOfInstsCurrent(userId, ids);
        assertEquals(Long.valueOf(7L), result.get(1L));
        assertEquals(Long.valueOf(13L), result.get(2L));

        verify(statsRepository).countsOfInsts(userId, Instance.Status.Approval, ids);
    }

    @Test
    public void countOfSwsCurrentWithString() {
        given(statsRepository.countOfSws(userId, Software.Status.Approval)).willReturn(7L);

        long result = statsService.countOfSwsCurrent(userId);

        assertEquals(7L, result);

        verify(statsRepository).countOfSws(userId, Software.Status.Approval);
    }

    @Test
    public void countOfInstsCurrentWithString() {
        given(statsRepository.countOfInsts(userId)).willReturn(7L);

        long result = statsService.countOfInstsCurrent(userId);
        assertEquals(7L, result);

        verify(statsRepository).countOfInsts(userId);
    }

    @Test
    public void countOfUsersCurrentWithString() {
        given(statsRepository.countOfUsers(userId, Instance.Status.Approval)).willReturn(7L);

        long result = statsService.countOfUsersCurrent(userId);
        assertEquals(7L, result);

        verify(statsRepository).countOfUsers(userId, Instance.Status.Approval);
    }

    @Test
    public void countsOfTotalSwsProvider() {
        given(statsRepository.countsOfTotalSwsProvider(any(Pageable.class)))
                .willReturn(Arrays.asList(new Object[] { "foo", 7L }, new Object[] { "bar", 13L }));

        Map<String, Long> result = null;

        result = statsService.countsOfTotalSwsProvider(2);
        assertEquals(Long.valueOf(7L), result.get("foo"));
        assertEquals(Long.valueOf(13L), result.get("bar"));

        result = statsService.countsOfTotalSwsProvider(-1);
        assertEquals(Long.valueOf(7L), result.get("foo"));
        assertEquals(Long.valueOf(13L), result.get("bar"));

        verify(statsRepository, atLeastOnce()).countsOfTotalSwsProvider(any(Pageable.class));
    }

    @Test
    public void countsOfSwsProvider() {
        given(statsRepository.countsOfSwsProvider(any(Software.Status.class), any(Pageable.class)))
                .willReturn(Arrays.asList(new Object[] { "foo", 7L }, new Object[] { "bar", 13L }));

        Map<String, Long> result = null;

        result = statsService.countsOfSwsProvider(2);
        assertEquals(Long.valueOf(7L), result.get("foo"));
        assertEquals(Long.valueOf(13L), result.get("bar"));

        result = statsService.countsOfSwsProvider(-1);
        assertEquals(Long.valueOf(7L), result.get("foo"));
        assertEquals(Long.valueOf(13L), result.get("bar"));

        verify(statsRepository, atLeastOnce()).countsOfSwsProvider(any(Software.Status.class), any(Pageable.class));
    }

    @Test
    public void countsOfInstsProvider() {
        given(statsRepository.countsOfTotalInstsProvider(any(Pageable.class)))
                .willReturn(Arrays.asList(new Object[] { "foo", 7L }, new Object[] { "bar", 13L }));

        Map<String, Long> result = null;

        result = statsService.countsOfInstsProvider(2);
        assertEquals(Long.valueOf(7L), result.get("foo"));
        assertEquals(Long.valueOf(13L), result.get("bar"));

        result = statsService.countsOfInstsProvider(-1);
        assertEquals(Long.valueOf(7L), result.get("foo"));
        assertEquals(Long.valueOf(13L), result.get("bar"));

        verify(statsRepository, atLeastOnce()).countsOfTotalInstsProvider(any(Pageable.class));
    }

    @Test
    public void countsOfInstsUser() {
        given(statsRepository.countsOfInstsUser(any(Instance.Status.class), any(Pageable.class)))
                .willReturn(Arrays.asList(new Object[] { "foo", 7L }, new Object[] { "bar", 13L }));

        Map<String, Long> result = null;

        result = statsService.countsOfInstsUser(2);
        assertEquals(Long.valueOf(7L), result.get("foo"));
        assertEquals(Long.valueOf(13L), result.get("bar"));

        result = statsService.countsOfInstsUser(-1);
        assertEquals(Long.valueOf(7L), result.get("foo"));
        assertEquals(Long.valueOf(13L), result.get("bar"));

        verify(statsRepository, atLeastOnce()).countsOfInstsUser(any(Instance.Status.class), any(Pageable.class));
    }

    @Test
    public void countsOfInstsSumUser() {
        given(statsRepository.countsOfInstsSumUsers(any(Pageable.class)))
                .willReturn(Arrays.asList(new Object[] { "foo", 7L }, new Object[] { "bar", 13L }));

        Map<String, Long> result = null;

        result = statsService.countsOfInstsSumUser(2);
        assertEquals(Long.valueOf(7L), result.get("foo"));
        assertEquals(Long.valueOf(13L), result.get("bar"));

        result = statsService.countsOfInstsSumUser(-1);
        assertEquals(Long.valueOf(7L), result.get("foo"));
        assertEquals(Long.valueOf(13L), result.get("bar"));

        verify(statsRepository, atLeastOnce()).countsOfInstsSumUsers(any(Pageable.class));
    }

    @Test
    public void countsOfInstsCurrent3() {
        given(statsRepository.countsOfInsts(any(Instance.Status.class), any(Pageable.class)))
                .willReturn(Arrays.asList(new Object[] { 1L, 7L }, new Object[] { 2L, 13L }));

        Map<Long, Long> result = null;

        result = statsService.countsOfInstsCurrent(2);
        assertEquals(Long.valueOf(7L), result.get(1L));
        assertEquals(Long.valueOf(13L), result.get(2L));

        result = statsService.countsOfInstsCurrent(-1);
        assertEquals(Long.valueOf(7L), result.get(1L));
        assertEquals(Long.valueOf(13L), result.get(2L));

        verify(statsRepository, atLeastOnce()).countsOfInsts(any(Instance.Status.class), any(Pageable.class));
    }

    @Test
    public void countsOfInstsCurrent4() {
        given(statsRepository.countsOfInsts(any(String.class), any(Instance.Status.class), any(Pageable.class)))
                .willReturn(Arrays.asList(new Object[] { 1L, 7L }, new Object[] { 2L, 13L }));

        Map<Long, Long> result = null;

        result = statsService.countsOfInstsCurrent(userId, 2);
        assertEquals(Long.valueOf(7L), result.get(1L));
        assertEquals(Long.valueOf(13L), result.get(2L));

        result = statsService.countsOfInstsCurrent(userId, -1);
        assertEquals(Long.valueOf(7L), result.get(1L));
        assertEquals(Long.valueOf(13L), result.get(2L));

        verify(statsRepository, atLeastOnce()).countsOfInsts(any(String.class), any(Instance.Status.class),
                any(Pageable.class));
    }

    @Test
    public void countsOfInsts() {
        List<Term> days = Stats.termsOf(current, 30, ChronoUnit.DAYS);
        List<Term> months = Stats.termsOf(null, 12, ChronoUnit.MONTHS);

        given(statsRepository.countOfInsts(any(LocalDateTime.class), any(LocalDateTime.class), any(Boolean.class)))
                .willReturn(7L);

        List<Long> result = null;

        result = statsService.countsOfInsts(days, true);
        assertEquals(days.size(), result.size());
        assertEquals(Long.valueOf(7L), result.get(0));

        result = statsService.countsOfInsts(months, true);
        assertEquals(months.size(), result.size());
        assertEquals(Long.valueOf(7L), result.get(0));

        verify(statsRepository, atLeastOnce()).countOfInsts(any(LocalDateTime.class), any(LocalDateTime.class),
                any(Boolean.class));
    }

    @Test
    public void countsOfInstsUser2() {
        List<Term> days = Stats.termsOf(current, 30, ChronoUnit.DAYS);
        List<Term> months = Stats.termsOf(null, 12, ChronoUnit.MONTHS);

        given(statsRepository.countOfInstsUser(any(String.class), any(LocalDateTime.class), any(LocalDateTime.class),
                any(Boolean.class))).willReturn(7L);

        List<Long> result = null;

        result = statsService.countsOfInstsUser(userId, days, true);
        assertEquals(days.size(), result.size());
        assertEquals(Long.valueOf(7L), result.get(0));

        result = statsService.countsOfInstsUser(userId, months, true);
        assertEquals(months.size(), result.size());
        assertEquals(Long.valueOf(7L), result.get(0));

        verify(statsRepository, atLeastOnce()).countOfInstsUser(any(String.class), any(LocalDateTime.class),
                any(LocalDateTime.class), any(Boolean.class));
    }

    @Test
    public void countsOfInstsUsers() {
        List<Term> days = Stats.termsOf(current, 30, ChronoUnit.DAYS);
        List<Term> months = Stats.termsOf(null, 12, ChronoUnit.MONTHS);

        given(statsRepository.countOfInstsUsers(anyList(), any(LocalDateTime.class), any(LocalDateTime.class),
                any(Boolean.class))).willReturn(Arrays.asList(new Object[] { "foo", 7L }, new Object[] { "bar", 13L }));

        Map<String, List<Long>> result = null;

        result = statsService.countsOfInstsUsers(Arrays.asList("foo", "bar"), days, true);
        assertEquals(2, result.size());
        assertEquals(days.size(), result.get("foo").size());

        result = statsService.countsOfInstsUsers(Arrays.asList("foo", "bar"), months, true);
        assertEquals(2, result.size());
        assertEquals(months.size(), result.get("foo").size());

        verify(statsRepository, atLeastOnce()).countOfInstsUsers(anyList(), any(LocalDateTime.class),
                any(LocalDateTime.class), any(Boolean.class));
    }

    @Test
    public void countsOfInsts2() {
        List<Term> days = Stats.termsOf(current, 30, ChronoUnit.DAYS);
        List<Term> months = Stats.termsOf(null, 12, ChronoUnit.MONTHS);

        given(statsRepository.countsOfInsts(anyList(), any(LocalDateTime.class), any(LocalDateTime.class),
                any(Boolean.class))).willReturn(Arrays.asList(new Object[] { 1L, 7L }, new Object[] { 2L, 13L }));

        Map<Long, List<Long>> result = null;

        result = statsService.countsOfInsts(Arrays.asList(1L, 2L), days, true);
        assertEquals(2, result.size());
        assertEquals(days.size(), result.get(1L).size());

        result = statsService.countsOfInsts(Arrays.asList(1L, 2L), months, true);
        assertEquals(2, result.size());
        assertEquals(months.size(), result.get(1L).size());

        verify(statsRepository, atLeastOnce()).countsOfInsts(anyList(), any(LocalDateTime.class),
                any(LocalDateTime.class), any(Boolean.class));
    }

    @Test
    public void countsOfInstsProvider2() {
        List<Term> days = Stats.termsOf(current, 30, ChronoUnit.DAYS);
        List<Term> months = Stats.termsOf(null, 12, ChronoUnit.MONTHS);

        given(statsRepository.countsOfInsts(anyList(), any(LocalDateTime.class), any(LocalDateTime.class),
                any(Boolean.class))).willReturn(Arrays.asList(new Object[] { 1L, 7L }, new Object[] { 2L, 13L }));

        List<Long> result = null;

        result = statsService.countsOfInstsProvider(Arrays.asList(1L, 2L), days, true);
        assertEquals(days.size(), result.size());
        assertEquals(Long.valueOf(7L + 13L), result.get(0));

        result = statsService.countsOfInstsProvider(Arrays.asList(1L, 2L), months, true);
        assertEquals(Long.valueOf(7L + 13L), result.get(0));

        verify(statsRepository, atLeastOnce()).countsOfInsts(anyList(), any(LocalDateTime.class),
                any(LocalDateTime.class), any(Boolean.class));
    }

    @Test
    public void countsOfInsts3() {
        List<Term> days = Stats.termsOf(current, 30, ChronoUnit.DAYS);
        List<Term> months = Stats.termsOf(null, 12, ChronoUnit.MONTHS);

        given(statsRepository.countsOfInsts(any(String.class), anyList(), any(LocalDateTime.class),
                any(LocalDateTime.class), any(Boolean.class)))
                        .willReturn(Arrays.asList(new Object[] { 1L, 7L }, new Object[] { 2L, 13L }));

        Map<Long, List<Long>> result = null;

        result = statsService.countsOfInsts(userId, Arrays.asList(1L, 2L), days, true);
        assertEquals(2, result.size());
        assertEquals(days.size(), result.get(1L).size());

        result = statsService.countsOfInsts(userId, Arrays.asList(1L, 2L), months, true);
        assertEquals(2, result.size());
        assertEquals(months.size(), result.get(1L).size());

        verify(statsRepository, atLeastOnce()).countsOfInsts(any(String.class), anyList(), any(LocalDateTime.class),
                any(LocalDateTime.class), any(Boolean.class));
    }

    @Test
    public void getSoldInstanceCount() {
        given(statsRepository.countsOfSodInsts(anyList()))
                .willReturn(Arrays.asList(new Object[] { 1L, 7L }, new Object[] { 2L, 13L }));

        Map<Long, Long> result = statsService.getSoldInstanceCount(Arrays.asList(1L, 2L));
        assertEquals(2, result.size());
        assertEquals(Long.valueOf(7L), result.get(1L));

        verify(statsRepository).countsOfSodInsts(anyList());
    }

    @Test
    public void getDayOfUseInstsPeriod() {
        given(statsRepository.dayOfUseInstsPeriod(any(String.class), anyList()))
                .willReturn(Arrays.asList(new Object[] { 1L, 7000 }, new Object[] { 2L, 13000 }));

        Map<Long, Integer> result = statsService.getDayOfUseInstsPeriod(userId, Arrays.asList(1L, 2L));
        assertEquals(2, result.size());
        assertEquals(Integer.valueOf(7000), result.get(1L));

        verify(statsRepository).dayOfUseInstsPeriod(any(String.class), anyList());
    }

    @Test
    public void getDayOfUseInstsPeriod2() {
        given(statsRepository.dayOfUseInstsPeriodMonth(any(String.class), anyList(), any(String.class),
                any(String.class))).willReturn(Arrays.asList(new Object[] { "a", "A" }, new Object[] { "b", "B" }));

        Map<String, String> result = statsService.getDayOfUseInstsPeriod(userId, Arrays.asList(1L, 2L), "20191108",
                "20191208");
        assertEquals(2, result.size());

        verify(statsRepository).dayOfUseInstsPeriodMonth(any(String.class), anyList(), any(String.class),
                any(String.class));
    }

    @Test
    public void getUsingPerInstanceByProvider() {
        given(statsRepository.usingPerInstanceByProvider(any(String.class), any(Long.class)))
                .willReturn(Arrays.asList(new Object[] { "a", "A" }, new Object[] { "b", "B" }));

        Map<Long, Object> result = statsService.getUsingPerInstanceByProvider(userId, Arrays.asList(1L, 2L));
        assertEquals(2, result.size());

        verify(statsRepository, atLeastOnce()).usingPerInstanceByProvider(any(String.class), any(Long.class));
    }

    @Test
    public void soldInstanceByProvider() {
        given(statsRepository.soldInstanceByProvider(any(String.class), any(Long.class)))
                .willReturn(Arrays.asList(new Object[] { "a", "A" }, new Object[] { "b", "B" }));

        Map<Long, Object> result = statsService.soldInstanceByProvider(userId, Arrays.asList(1L, 2L));
        assertEquals(2, result.size());

        verify(statsRepository, atLeastOnce()).soldInstanceByProvider(any(String.class), any(Long.class));
    }

    @Test
    public void getSalesAmount() {
        given(statsRepository.getSalesAmount(any(String.class), anyList(), any(LocalDateTime.class),
                any(LocalDateTime.class), any(LocalDateTime.class)))
                        .willReturn(Arrays.asList(new Object[] { 1L, 1000L, 31L }, new Object[] { 2L, 2000L, 12L }));

        Map<Long, Long> result = statsService.getSalesAmount(userId, Arrays.asList(1L, 2L), current, current);
        assertEquals(2, result.size());

        verify(statsRepository, atLeastOnce()).getSalesAmount(any(String.class), anyList(), any(LocalDateTime.class),
                any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test(expected = IllegalStateException.class)
    public void getSalesAmountWithError() {
        statsService.getSalesAmount(userId, Arrays.asList(1L, 2L), LocalDateTime.of(2019, 11, 8, 0, 0),
                LocalDateTime.of(2019, 12, 8, 0, 0));
    }

    @Test
    public void getPurchaseAmount() {
        given(statsRepository.getPurchaseAmount(any(String.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(Arrays.asList(new Object[] { "a", "b", "c", "d" }, new Object[] { "e", "f", "g", "h" }));

        Map<Long, Object> result = statsService.getPurchaseAmount(userId, current, current);
        assertEquals(2, result.size());

        verify(statsRepository, atLeastOnce()).getPurchaseAmount(any(String.class), any(LocalDateTime.class),
                any(LocalDateTime.class));
    }

}
