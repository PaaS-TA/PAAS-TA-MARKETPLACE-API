package org.openpaas.paasta.marketplace.api.query;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openpaas.paasta.marketplace.api.repository.query.StatsQuery;

public class StatsQueryTest extends AbstractQueryMockTest {

	@InjectMocks
	StatsQuery<?> statsQuery;
	
	@Mock
    private EntityManager entityManager;

	@Mock
	private Query typedQuery;
	
	
    @Before
    public void setUp() throws Exception {
    	super.setUp();
    }
    
    
    // Seller 요금통계 정보조회 총카운터
    @Test
 	public void querySoftwareSellPriceTotalCount() throws Exception {
    	List<Long> mockList = Arrays.asList(1L);
    	
    	when(entityManager.createNativeQuery(any(String.class))).thenReturn(typedQuery);
    	when(typedQuery.getResultList()).thenReturn(mockList);
    	
    	Integer result = statsQuery.querySoftwareSellPriceTotalCount(userId, categoryId, current.toString(), 0, 10);
    	assertEquals(1, result.intValue());
    	
    	verify(entityManager, atLeastOnce()).createNativeQuery(any(String.class));
 	}

 	// Seller 요금통계 정보조회 리스트
    @Test
 	public void querySoftwareSellPriceList() throws Exception {
    	List<Object[]> mockResultList = Arrays.asList(new Object[]{1, 1, "cate-01", "soft-01", "1.0", current.toString(), 1000, 18}
    												, new Object[]{2, 1, "cate-02", "soft-02", "1.0", current.toString(), 2000, 69});
    	
    	when(entityManager.createNativeQuery(any(String.class))).thenReturn(typedQuery);
    	when(typedQuery.getResultList()).thenReturn(mockResultList);
    	
    	List<Map<String,Object>> result = statsQuery.querySoftwareSellPriceList(userId, categoryId, current.toString(), 0, 10);
    	assertEquals(2, result.size());
    	assertEquals("soft-01", result.get(0).get("softawareName"));
    	assertEquals("soft-02", result.get(1).get("softawareName"));
    	
    	verify(entityManager, atLeastOnce()).createNativeQuery(any(String.class));
 	}
    
    // Seller 요금통계 정보조회 리스트
    @Test
    public void querySoftwareSellPriceListWithEmpty() throws Exception {
    	List<Object[]> mockResultList = new ArrayList<Object[]>();
    	
    	when(entityManager.createNativeQuery(any(String.class))).thenReturn(typedQuery);
    	when(typedQuery.getResultList()).thenReturn(mockResultList);
    	
    	List<Map<String,Object>> result = statsQuery.querySoftwareSellPriceList(userId, categoryId, current.toString(), 0, 10);
    	assertEquals(0, result.size());
    	
    	verify(entityManager, atLeastOnce()).createNativeQuery(any(String.class));
    }

 	// 사용자별 구매 퍼센트 통계
    @Test
 	public void queryPurchaserPercent() throws Exception {
    	List<Object[]> mockResultList = Arrays.asList(new Object[]{"user-01", 10, 1000, 18}
													, new Object[]{"user-02", 20, 2000, 69});
    	
    	when(entityManager.createNativeQuery(any(String.class))).thenReturn(typedQuery);
    	when(typedQuery.getResultList()).thenReturn(mockResultList);
    	
    	List<Map<String,Object>> result = statsQuery.queryPurchaserPercent();
    	assertEquals(2, result.size());
    	assertEquals("user-01", result.get(0).get("userId"));
    	assertEquals("user-02", result.get(1).get("userId"));
    	
    	verify(entityManager, atLeastOnce()).createNativeQuery(any(String.class));
 	}
    
    // 사용자별 구매 퍼센트 통계
    @Test
    public void queryPurchaserPercentWithEmpty() throws Exception {
    	List<Object[]> mockResultList = new ArrayList<Object[]>();
    	
    	when(entityManager.createNativeQuery(any(String.class))).thenReturn(typedQuery);
    	when(typedQuery.getResultList()).thenReturn(mockResultList);
    	
    	List<Map<String,Object>> result = statsQuery.queryPurchaserPercent();
    	assertEquals(0, result.size());
    	
    	verify(entityManager, atLeastOnce()).createNativeQuery(any(String.class));
    }

 	// 월별 상품구매 통계
	@Test
 	public void queryPurchaseTransitionMonth() throws Exception {
    	List<Object[]> mockResultList = Arrays.asList(new Object[]{"2019-11-01", 1018}
													, new Object[]{"2019-12-01", 2069});
		
		when(entityManager.createNativeQuery(any(String.class))).thenReturn(typedQuery);
		when(typedQuery.getResultList()).thenReturn(mockResultList);
		
		List<Map<String,Object>> result = statsQuery.queryPurchaseTransitionMonth();
		assertEquals(2, result.size());
		assertEquals("2019-11-01", result.get(0).get("yearMonth"));
		assertEquals("2019-12-01", result.get(1).get("yearMonth"));
		
		verify(entityManager, atLeastOnce()).createNativeQuery(any(String.class));
 	}
	
	// 월별 상품구매 통계
	@Test
	public void queryPurchaseTransitionMonthWithEmpty() throws Exception {
		List<Object[]> mockResultList = new ArrayList<Object[]>();
		
		when(entityManager.createNativeQuery(any(String.class))).thenReturn(typedQuery);
		when(typedQuery.getResultList()).thenReturn(mockResultList);
		
		List<Map<String,Object>> result = statsQuery.queryPurchaseTransitionMonth();
		assertEquals(0, result.size());
		
		verify(entityManager, atLeastOnce()).createNativeQuery(any(String.class));
	}

 	// 앱사용 사용자 추이
	@Test
 	public void queryUsageTransition() throws Exception {
		List<Object[]> mockResultList = Arrays.asList(new Object[]{"2019-11-01", 1018}
													, new Object[]{"2019-12-01", 2069});
		
		when(entityManager.createNativeQuery(any(String.class))).thenReturn(typedQuery);
		when(typedQuery.getResultList()).thenReturn(mockResultList);
		
		List<Map<String,Object>> result = statsQuery.queryUsageTransition();
		assertEquals(2, result.size());
		assertEquals(1018, result.get(0).get("usageUserCount"));
		assertEquals(2069, result.get(1).get("usageUserCount"));
		
		verify(entityManager, atLeastOnce()).createNativeQuery(any(String.class));
 	}
	
	// 앱사용 사용자 추이
	@Test
	public void queryUsageTransitionWithEmpty() throws Exception {
		List<Object[]> mockResultList = new ArrayList<Object[]>();
	
		when(entityManager.createNativeQuery(any(String.class))).thenReturn(typedQuery);
		when(typedQuery.getResultList()).thenReturn(mockResultList);
		
		List<Map<String,Object>> result = statsQuery.queryUsageTransition();
		assertEquals(0, result.size());
		
		verify(entityManager, atLeastOnce()).createNativeQuery(any(String.class));
	}
    
    // 현재 사용중인 상품 카운트
    @Test
    public void queryCountOfInstsCurrent() throws Exception {
    	List<Long> mockList = Arrays.asList(1L);
    	
    	when(entityManager.createNativeQuery(any(String.class))).thenReturn(typedQuery);
    	when(typedQuery.getResultList()).thenReturn(mockList);
    	
    	Long result = statsQuery.queryCountOfInstsCurrent(categoryId, current.toString(), current.toString());
    	assertEquals(1L, result.longValue());
    	
    	verify(entityManager, atLeastOnce()).createNativeQuery(any(String.class));
    }
    
    // 현재 사용중인 상품 카운트
    @Test
    public void queryCountOfInstsCurrentWithFullArgs() throws Exception {
    	List<Long> mockList = Arrays.asList(1L);
    	
    	when(entityManager.createNativeQuery(any(String.class))).thenReturn(typedQuery);
    	when(typedQuery.getResultList()).thenReturn(mockList);
    	
    	Long result = statsQuery.queryCountOfInstsCurrent(categoryId, current.toString(), current.toString(), userId);
    	assertEquals(1L, result.longValue());
    	
    	verify(entityManager, atLeastOnce()).createNativeQuery(any(String.class));
    }
    
    // 현재 상품을 사용중인 User 카운트
    @Test
 	public void queryCountOfUsersUsing() throws Exception {
    	List<Long> mockList = Arrays.asList(1L);
    	
    	when(entityManager.createNativeQuery(any(String.class))).thenReturn(typedQuery);
    	when(typedQuery.getResultList()).thenReturn(mockList);
    	
    	Long result = statsQuery.queryCountOfUsersUsing(categoryId, current.toString(), current.toString());
    	assertEquals(1L, result.longValue());
    	
    	verify(entityManager, atLeastOnce()).createNativeQuery(any(String.class));
 	}
    
    // 현재 상품을 사용중인 User 카운트
    @Test
    public void queryCountOfUsersUsingWithFullArgs() throws Exception {
    	List<Long> mockList = Arrays.asList(1L);
    	
    	when(entityManager.createNativeQuery(any(String.class))).thenReturn(typedQuery);
    	when(typedQuery.getResultList()).thenReturn(mockList);
    	
    	Long result = statsQuery.queryCountOfUsersUsing(categoryId, current.toString(), current.toString(), userId);
    	assertEquals(1L, result.longValue());
    	
    	verify(entityManager, atLeastOnce()).createNativeQuery(any(String.class));
    }

 	// 상품별 사용앱 데이터 조회 (Chart)
    @Test
 	public void queryStatsUseApp() throws Exception {
	  	List<Object[]> mockResultList = Arrays.asList(new Object[]{"홍길동", 1018}
													, new Object[]{"김삿갓", 2069});
		
		when(entityManager.createNativeQuery(any(String.class))).thenReturn(typedQuery);
		when(typedQuery.getResultList()).thenReturn(mockResultList);
		
		List<Map<String,Object>> result = statsQuery.queryStatsUseApp(userId, categoryId, current.toString(), current.toString());
		assertEquals(2, result.size());
		assertEquals("홍길동", result.get(0).get("name"));
		assertEquals("김삿갓", result.get(1).get("name"));
		
		verify(entityManager, atLeastOnce()).createNativeQuery(any(String.class));
 	}
    
    // 상품별 사용앱 데이터 조회 (Chart)
    @Test
    public void queryStatsUseAppWithEmpty() throws Exception {
    	List<Object[]> mockResultList = new ArrayList<Object[]>();
    	
    	when(entityManager.createNativeQuery(any(String.class))).thenReturn(typedQuery);
    	when(typedQuery.getResultList()).thenReturn(mockResultList);
    	
    	List<Map<String,Object>> result = statsQuery.queryStatsUseApp(userId, categoryId, current.toString(), current.toString());
    	assertEquals(0, result.size());
    	
    	verify(entityManager, atLeastOnce()).createNativeQuery(any(String.class));
    }

 	// 상품별 사용추이 데이터 조회 (Chart)
    @Test
 	public void queryStatsUseTransition() throws Exception {
	  	List<Object[]> mockResultList = Arrays.asList(new Object[]{"2019-11-01", "홍길동", 1018}
													, new Object[]{"2019-12-01", "김삿갓", 2069});
		
		when(entityManager.createNativeQuery(any(String.class))).thenReturn(typedQuery);
		when(typedQuery.getResultList()).thenReturn(mockResultList);
		
		List<Map<String,Object>> result = statsQuery.queryStatsUseTransition(userId, categoryId, current.toString(), current.toString());
		assertEquals(2, result.size());
		assertEquals(1018, result.get(0).get("usageCount"));
		assertEquals(2069, result.get(1).get("usageCount"));
		
		verify(entityManager, atLeastOnce()).createNativeQuery(any(String.class));
 	}
    
    // 상품별 사용추이 데이터 조회 (Chart)
    @Test
    public void queryStatsUseTransitionWithEmpty() throws Exception {
    	List<Object[]> mockResultList = new ArrayList<Object[]>();
    	
    	when(entityManager.createNativeQuery(any(String.class))).thenReturn(typedQuery);
    	when(typedQuery.getResultList()).thenReturn(mockResultList);
    	
    	List<Map<String,Object>> result = statsQuery.queryStatsUseTransition(userId, categoryId, null, null);
    	assertEquals(0, result.size());
    	
    	verify(entityManager, atLeastOnce()).createNativeQuery(any(String.class));
    }

 	// 판매자별 등록앱 퍼센트 분포
    @Test
 	public void querySellerCreatedAppPercent() throws Exception {
	  	List<Object[]> mockResultList = Arrays.asList(new Object[]{"홍길동", 1018}
													, new Object[]{"김삿갓", 2069});
		
		when(entityManager.createNativeQuery(any(String.class))).thenReturn(typedQuery);
		when(typedQuery.getResultList()).thenReturn(mockResultList);
		
		List<Map<String,Object>> result = statsQuery.querySellerCreatedAppPercent(sellerName);
		assertEquals(2, result.size());
		assertEquals("홍길동", result.get(0).get("name"));
		assertEquals("김삿갓", result.get(1).get("name"));
		
		verify(entityManager, atLeastOnce()).createNativeQuery(any(String.class));
 	}
    
    // 판매자별 등록앱 퍼센트 분포
    @Test
    public void querySellerCreatedAppPercentWithEmpty() throws Exception {
    	List<Object[]> mockResultList = new ArrayList<Object[]>();
    	
    	when(entityManager.createNativeQuery(any(String.class))).thenReturn(typedQuery);
    	when(typedQuery.getResultList()).thenReturn(mockResultList);
    	
    	List<Map<String,Object>> result = statsQuery.querySellerCreatedAppPercent(sellerName);
    	assertEquals(0, result.size());
    	
    	verify(entityManager, atLeastOnce()).createNativeQuery(any(String.class));
    }

 	// 판매자별 앱 사용 추이
    @Test
 	public void querySellerCreatedAppTransition() throws Exception {
 		// String sellerName
	  	List<Object[]> mockResultList = Arrays.asList(new Object[]{"2019-11-01", "홍길동", 1018}
	  												, new Object[]{"2019-12-01", "김삿갓", 2069});

		when(entityManager.createNativeQuery(any(String.class))).thenReturn(typedQuery);
		when(typedQuery.getResultList()).thenReturn(mockResultList);
		
		List<Map<String,Object>> result = statsQuery.querySellerCreatedAppTransition(sellerName);
		assertEquals(2, result.size());
		assertEquals(1018, result.get(0).get("useCount"));
		assertEquals(2069, result.get(1).get("useCount"));
		
		verify(entityManager, atLeastOnce()).createNativeQuery(any(String.class));
 	}
    
    // 판매자별 앱 사용 추이
    @Test
    public void querySellerCreatedAppTransitionWithEmpty() throws Exception {
    	// String sellerName
    	List<Object[]> mockResultList = new ArrayList<Object[]>();
    	
    	when(entityManager.createNativeQuery(any(String.class))).thenReturn(typedQuery);
    	when(typedQuery.getResultList()).thenReturn(mockResultList);
    	
    	List<Map<String,Object>> result = statsQuery.querySellerCreatedAppTransition(sellerName);
    	assertEquals(0, result.size());
    	
    	verify(entityManager, atLeastOnce()).createNativeQuery(any(String.class));
    }
}
