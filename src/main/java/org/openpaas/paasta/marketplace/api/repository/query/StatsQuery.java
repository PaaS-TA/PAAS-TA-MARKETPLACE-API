package org.openpaas.paasta.marketplace.api.repository.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class StatsQuery<T> {

	@PersistenceContext
	private EntityManager entityManager;
	
	/**
	 * Seller 요금통계 정보조회 총카운터
	 * @param userId
	 * @param categoryId
	 * @param srchDate
	 * @param page
	 * @param rowSize
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Integer querySoftwareSellPriceTotalCount(String userId, String categoryId, String srchDate, int page, int rowSize) {
		// SQL생성
		StringBuffer excQuery = new StringBuffer();
		excQuery.append("SELECT  COUNT(0) \n");
		excQuery.append("FROM	 ( \n");
		excQuery.append("			SELECT  COUNT(0) \n");
		excQuery.append("			FROM    ( \n");
		excQuery.append("            			SELECT  so.id AS softwareId \n");
		excQuery.append("                    			,so.category_id AS categoryId \n");
		excQuery.append("                    			,(SELECT name FROM category s1 WHERE s1.id = so.category_id) AS categoryName \n");
		excQuery.append("                    			,so.name AS softawareName \n");
		excQuery.append("                    			,so.version \n");
		excQuery.append("                    			,DATE_FORMAT(so.created_date,'%Y-%m-%d') AS createdDate \n");
		excQuery.append("                    			,IFNULL(sp.cpu_amt,0)+IFNULL(sp.memory_amt,0)+IFNULL(sp.disk_amt,0) AS softwarePlanAmtMonth \n");
		excQuery.append("            			FROM    software so \n");
		excQuery.append("                    			LEFT JOIN instance it \n");
		excQuery.append("                       			ON (it.software_id = so.id) \n");
		excQuery.append("                    			LEFT JOIN software_plan sp \n");
		excQuery.append("                        			ON (sp.id = it.software_plan_id) \n");
		excQuery.append("            			WHERE   1=1 \n");
		if (StringUtils.isNotBlank(srchDate)) {
			excQuery.append("            AND     DATE_FORMAT(:srchDate,'%Y%m') BETWEEN DATE_FORMAT(it.usage_start_date,'%Y%m') \n");
			excQuery.append("                                                      AND DATE_FORMAT(IFNULL(it.usage_end_date, NOW()),'%Y%m') \n");
		}
		if (StringUtils.isNotBlank(categoryId)) {
			excQuery.append("AND     so.category_id = :categoryId\n");
		}
		excQuery.append("            			AND     so.status = 'Approval' \n");
		excQuery.append("            			AND     so.created_by = :userId \n");
		excQuery.append("        			) t \n");
		excQuery.append("			GROUP BY softwareId, categoryId, categoryName, softawareName, version, createdDate \n");
		excQuery.append("	) r \n");
		
		// 쿼리생성
		Query typedQuery = entityManager.createNativeQuery(excQuery.toString());
		
		// Parameter 설정
		if (StringUtils.isNotBlank(srchDate)) {
			typedQuery.setParameter("srchDate", srchDate);
		}
		if (StringUtils.isNotBlank(categoryId)) {
			typedQuery.setParameter("categoryId", categoryId);
		}
		typedQuery.setParameter("userId", userId);
		
		// 결과 값 Binding
		Optional<T> first = typedQuery.getResultList().stream().findFirst();
		Integer totalCount = Integer.valueOf(first.get().toString());
		
		return totalCount;
	}
	
	/**
	 * Seller 요금통계 정보조회 리스트
	 * @param userId
	 * @param categoryId
	 * @param srchDate
	 * @param page
	 * @param rowSize
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> querySoftwareSellPriceList(String userId, String categoryId, String srchDate, int page, int rowSize) {
		// 조회 결과값 반환 객체 생성
		List<Map<String,Object>> excuteResult = new ArrayList<Map<String,Object>>();
		
		// SQL생성
		StringBuffer excQuery = new StringBuffer();
		excQuery.append("SELECT  softwareId \n");
		excQuery.append("        ,categoryId \n");
		excQuery.append("        ,categoryName \n");
		excQuery.append("        ,softawareName \n");
		excQuery.append("        ,version \n");
		excQuery.append("        ,createdDate \n");
		excQuery.append("        ,SUM(softwarePlanAmtMonth) AS softwarePlanAmtMonth \n");
		excQuery.append("        ,CASE WHEN SUM(softwarePlanAmtMonth) > 0 THEN COUNT(0) \n");
		excQuery.append("              ELSE 0 \n");
		excQuery.append("        END AS sellCount \n");
		excQuery.append("FROM    ( \n");
		excQuery.append("            SELECT  so.id AS softwareId \n");
		excQuery.append("                    ,so.category_id AS categoryId \n");
		excQuery.append("                    ,(SELECT name FROM category s1 WHERE s1.id = so.category_id) AS categoryName \n");
		excQuery.append("                    ,so.name AS softawareName \n");
		excQuery.append("                    ,so.version \n");
		excQuery.append("                    ,DATE_FORMAT(so.created_date,'%Y-%m-%d') AS createdDate \n");
		excQuery.append("                    ,IFNULL(sp.cpu_amt,0)+IFNULL(sp.memory_amt,0)+IFNULL(sp.disk_amt,0) AS softwarePlanAmtMonth \n");
		excQuery.append("            FROM    software so \n");
		excQuery.append("                    LEFT JOIN instance it \n");
		excQuery.append("                        ON (it.software_id = so.id) \n");
		excQuery.append("                    LEFT JOIN software_plan sp \n");
		excQuery.append("                        ON (sp.id = it.software_plan_id) \n");
		excQuery.append("            WHERE   1=1 \n");
		if (StringUtils.isNotBlank(srchDate)) {
			excQuery.append("            AND     DATE_FORMAT(:srchDate,'%Y%m') BETWEEN DATE_FORMAT(it.usage_start_date,'%Y%m') \n");
			excQuery.append("                                                      AND DATE_FORMAT(IFNULL(it.usage_end_date, NOW()),'%Y%m') \n");
		}
		if (StringUtils.isNotBlank(categoryId)) {
			excQuery.append("AND     so.category_id = :categoryId\n");
		}
		excQuery.append("            AND     so.status = 'Approval' \n");
		excQuery.append("            AND     so.created_by = :userId \n");
		excQuery.append("        ) t \n");
		excQuery.append("GROUP BY softwareId, categoryId, categoryName, softawareName, version, createdDate \n");
		excQuery.append("ORDER BY softwareId DESC \n");
		excQuery.append("LIMIT :pageStart, :pageEnd \n");
		
		// 쿼리생성
		Query typedQuery = entityManager.createNativeQuery(excQuery.toString());
		
		// Parameter 설정
		if (StringUtils.isNotBlank(srchDate)) {
			typedQuery.setParameter("srchDate", srchDate);
		}
		if (StringUtils.isNotBlank(categoryId)) {
			typedQuery.setParameter("categoryId", categoryId);
		}
		typedQuery.setParameter("userId", userId);
		typedQuery.setParameter("pageStart", (page * rowSize));
		typedQuery.setParameter("pageEnd", rowSize);
		
		// 결과 값 Binding
		List<Object[]> resultList = typedQuery.getResultList();
		if (CollectionUtils.isEmpty(resultList)) {
			return excuteResult;
		}
		Map<String,Object> tempRowValues = null;
		for (Object[] arrInfo : resultList) {
			tempRowValues = new HashMap<String,Object>();
			tempRowValues.put("softwareId", arrInfo[0]);
			tempRowValues.put("categoryId", arrInfo[1]);
			tempRowValues.put("categoryName", arrInfo[2]);
			tempRowValues.put("softawareName", arrInfo[3]);
			tempRowValues.put("version", arrInfo[4]);
			tempRowValues.put("createdDate", arrInfo[5]);
			tempRowValues.put("softwarePlanAmtMonth", arrInfo[6]);
			tempRowValues.put("sellCount", arrInfo[7]);
			excuteResult.add(tempRowValues);
		}
		
		return excuteResult;
	}
	
	/**
	 * 사용자별 구매 퍼센트 통계
	 * @param userId
	 * @param categoryId
	 * @param srchDate
	 * @param page
	 * @param rowSize
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> queryPurchaserPercent() {
		// 조회 결과값 반환 객체 생성
		List<Map<String,Object>> excuteResult = new ArrayList<Map<String,Object>>();
		
		// SQL생성
		StringBuffer excQuery = new StringBuffer();
		excQuery.append("SELECT  it.created_by AS userId \n");
		excQuery.append("        ,COUNT(0) AS instanceCount \n");
		excQuery.append("        ,cj.totalInstanceCount \n");
		excQuery.append("        ,ROUND((COUNT(0) / MIN(cj.totalInstanceCount)) * 100, 1) AS purchaserPercent \n");
		excQuery.append("FROM    instance it \n");
		excQuery.append("        CROSS JOIN ( \n");
		excQuery.append("            SELECT  COUNT(0) AS totalInstanceCount \n");
		excQuery.append("            FROM    instance \n");
		excQuery.append("            WHERE   DATE_FORMAT(IFNULL(usage_end_date, NOW()), '%Y%m') >= DATE_FORMAT(NOW(), '%Y%m') \n");
		excQuery.append("        ) cj \n");
		excQuery.append("WHERE   1=1 \n");
		excQuery.append("AND     DATE_FORMAT(IFNULL(it.usage_end_date, NOW()), '%Y%m') >= DATE_FORMAT(NOW(), '%Y%m') \n");
		excQuery.append("GROUP BY it.created_by \n");
		
		// 쿼리생성
		Query typedQuery = entityManager.createNativeQuery(excQuery.toString());
		
		// 결과 값 Binding
		List<Object[]> resultList = typedQuery.getResultList();
		if (CollectionUtils.isEmpty(resultList)) {
			return excuteResult;
		}
		Map<String,Object> tempRowValues = null;
		for (Object[] arrInfo : resultList) {
			tempRowValues = new HashMap<String,Object>();
			tempRowValues.put("userId", arrInfo[0]);
			tempRowValues.put("instanceCount", arrInfo[1]);
			tempRowValues.put("totalInstanceCount", arrInfo[2]);
			tempRowValues.put("purchaserPercent", arrInfo[3]);
			excuteResult.add(tempRowValues);
		}
		
		return excuteResult;
	}
	
	/**
	 * 월별 상품구매 통계
	 * @param userId
	 * @param categoryId
	 * @param srchDate
	 * @param page
	 * @param rowSize
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> queryPurchaseTransitionMonth() {
		// 조회 결과값 반환 객체 생성
		List<Map<String,Object>> excuteResult = new ArrayList<Map<String,Object>>();
		
		// SQL생성
		StringBuffer excQuery = new StringBuffer();
		excQuery.append("SELECT  c.yearMonth AS yearMonth \n");
		excQuery.append("        ,IFNULL(r.purchaseCount,0) AS purchaseCount \n");
		excQuery.append("FROM    ( \n");
		excQuery.append("            SELECT  DATE_FORMAT(cl.dt, '%Y-%m-01') AS yearMonth \n");
		excQuery.append("            FROM    calendar cl \n");
		excQuery.append("            WHERE   1=1 \n");
		excQuery.append("            AND     cl.ym BETWEEN DATE_FORMAT(DATE_ADD(NOW(), INTERVAL -11 MONTH), '%Y%m') \n");
		excQuery.append("                              AND DATE_FORMAT(NOW(), '%Y%m') \n");
		excQuery.append("            GROUP BY cl.ym \n");
		excQuery.append("        ) c \n");
		excQuery.append("        LEFT JOIN ( \n");
		excQuery.append("            SELECT  DATE_FORMAT(it.usage_start_date, '%Y-%m-01') AS yearMonth \n");
		excQuery.append("                    ,COUNT(0) AS purchaseCount \n");
		excQuery.append("            FROM    software so \n");
		excQuery.append("                    INNER JOIN instance it \n");
		excQuery.append("                        ON (it.software_id = so.id) \n");
		excQuery.append("            WHERE   1=1 \n");
		excQuery.append("            AND     DATE_FORMAT(it.usage_start_date, '%Y%m') BETWEEN DATE_FORMAT(DATE_ADD(NOW(), INTERVAL -11 MONTH), '%Y%m') \n");
		excQuery.append("                                                                 AND DATE_FORMAT(NOW(), '%Y%m') \n");
		excQuery.append("            GROUP BY DATE_FORMAT(it.usage_start_date, '%Y%m') \n");
		excQuery.append("        ) r \n");
		excQuery.append("        ON (r.yearMonth = c.yearMonth) \n");
		excQuery.append("ORDER BY c.yearMonth \n");
		
		// 쿼리생성
		Query typedQuery = entityManager.createNativeQuery(excQuery.toString());
		
		// 결과 값 Binding
		List<Object[]> resultList = typedQuery.getResultList();
		if (CollectionUtils.isEmpty(resultList)) {
			return excuteResult;
		}
		Map<String,Object> tempRowValues = null;
		for (Object[] arrInfo : resultList) {
			tempRowValues = new HashMap<String,Object>();
			tempRowValues.put("yearMonth", arrInfo[0]);
			tempRowValues.put("purchaseCount", arrInfo[1]);
			excuteResult.add(tempRowValues);
		}
		
		return excuteResult;
	}
	
	/**
	 * 앱사용 사용자 추이
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> queryUsageTransition() {
		// 조회 결과값 반환 객체 생성
		List<Map<String,Object>> excuteResult = new ArrayList<Map<String,Object>>();
		
		// SQL생성
		StringBuffer excQuery = new StringBuffer();
		excQuery.append("SELECT  cl.yearMonth AS yearMonth \n");
		excQuery.append("        ,IFNULL(r.usageUserCount,0) AS usageUserCount \n");
		excQuery.append("FROM    ( \n");
		excQuery.append("            SELECT  DATE_FORMAT(dt, '%Y-%m-01') AS yearMonth \n");
		excQuery.append("            FROM    calendar \n");
		excQuery.append("            WHERE   1=1 \n");
		excQuery.append("            AND     ym BETWEEN DATE_FORMAT(DATE_ADD(NOW(), INTERVAL -11 MONTH), '%Y%m') \n");
		excQuery.append("                              AND DATE_FORMAT(NOW(), '%Y%m') \n");
		excQuery.append("            GROUP BY ym \n");
		excQuery.append("        ) cl \n");
		excQuery.append("        LEFT JOIN ( \n");
		excQuery.append("            SELECT  us.yearMonth \n");
		excQuery.append("                    ,COUNT(0) AS usageUserCount \n");
		excQuery.append("            FROM    ( \n");
		excQuery.append("                        SELECT  cl.yearMonth  \n");
		excQuery.append("                                ,it.created_by AS userId \n");
		excQuery.append("                        FROM    software so  \n");
		excQuery.append("                                INNER JOIN instance it \n");
		excQuery.append("                                    ON (it.software_id = so.id) \n");
		excQuery.append("                                CROSS JOIN (  \n");
		excQuery.append("                                    SELECT  DATE_FORMAT(dt, '%Y-%m-01') AS yearMonth \n");
		excQuery.append("                                            ,ym AS compareDate  \n");
		excQuery.append("                                    FROM    calendar \n");
		excQuery.append("                                    WHERE   1=1 \n");
		excQuery.append("                                    AND     ym BETWEEN DATE_FORMAT(DATE_ADD(NOW(), INTERVAL -11 MONTH), '%Y%m') \n");
		excQuery.append("                                                   AND DATE_FORMAT(NOW(), '%Y%m')  \n");
		excQuery.append("                                    GROUP BY ym  \n");
		excQuery.append("                                ) cl \n");
		excQuery.append("                        WHERE   1=1 \n");
		excQuery.append("                        AND     1 = (CASE WHEN DATE_FORMAT(it.usage_start_date, '%Y%m') <= cl.compareDate \n");
		excQuery.append("                                           AND DATE_FORMAT(IFNULL(it.usage_end_date, NOW()), '%Y%m') >= cl.compareDate \n");
		excQuery.append("                                          THEN 1 \n");
		excQuery.append("                                          ELSE 0 \n");
		excQuery.append("                                    END) \n");
		excQuery.append("                        AND     DATE_FORMAT(it.usage_start_date, '%Y%m') BETWEEN DATE_FORMAT(DATE_ADD(NOW(), INTERVAL -11 MONTH), '%Y%m')  \n");
		excQuery.append("                                                                             AND DATE_FORMAT(NOW(), '%Y%m') \n");
		excQuery.append("                        GROUP BY cl.yearMonth, it.created_by \n");
		excQuery.append("                    ) us \n");
		excQuery.append("            GROUP BY us.yearMonth \n");
		excQuery.append("        ) r \n");
		excQuery.append("        ON (r.yearMonth = cl.yearMonth) \n");
		excQuery.append("ORDER BY cl.yearMonth \n");		
		// 쿼리생성
		Query typedQuery = entityManager.createNativeQuery(excQuery.toString());
		
		// 결과 값 Binding
		List<Object[]> resultList = typedQuery.getResultList();
		if (CollectionUtils.isEmpty(resultList)) {
			return excuteResult;
		}
		Map<String,Object> tempRowValues = null;
		for (Object[] arrInfo : resultList) {
			tempRowValues = new HashMap<String,Object>();
			tempRowValues.put("yearMonth", arrInfo[0]);
			tempRowValues.put("usageUserCount", arrInfo[1]);
			excuteResult.add(tempRowValues);
		}
		
		return excuteResult;
	}
	
	/**
	 * 현재 사용중인 상품 카운트
	 * @param categoryId
	 * @param srchStartDate
	 * @param srchEndDate
	 * @return
	 */
	public Long queryCountOfInstsCurrent(String categoryId, String srchStartDate, String srchEndDate) {
		return queryCountOfInstsCurrent(categoryId, srchStartDate, srchEndDate, null);
	}
	
	/**
	 * 현재 사용중인 상품 카운트
	 * @param categoryId
	 * @param srchStartDate
	 * @param srchEndDate
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Long queryCountOfInstsCurrent(String categoryId, String srchStartDate, String srchEndDate, String userId) {
		// SQL생성
		StringBuffer excQuery = new StringBuffer();
		excQuery.append("SELECT  COUNT(0) AS count \n");
		excQuery.append("FROM    instance it \n");
		excQuery.append("        INNER JOIN software so \n");
		excQuery.append("            ON (so.id = it.software_id) \n");
		excQuery.append("WHERE   1=1 \n");
		excQuery.append("AND     it.status = 'Approval' \n");
		if (StringUtils.isNotBlank(userId)) {
			excQuery.append("AND     so.created_by = :userId \n");
		}
		if (StringUtils.isNotBlank(categoryId)) {
			excQuery.append("AND     so.category_id = :categoryId \n");
		}
		if (StringUtils.isNotBlank(srchStartDate) && StringUtils.isNotBlank(srchEndDate)) {
			excQuery.append("AND     DATE_FORMAT(so.created_date, '%Y%m%d') BETWEEN DATE_FORMAT(:srchStartDate, '%Y%m%d') \n");
			excQuery.append("                                                   AND DATE_FORMAT(:srchEndDate, '%Y%m%d') \n");
		}
		
		// 쿼리생성
		Query typedQuery = entityManager.createNativeQuery(excQuery.toString());
		
		// Parameter 설정
		if (StringUtils.isNotBlank(userId)) {
			typedQuery.setParameter("userId", userId);
		}
		if (StringUtils.isNotBlank(categoryId)) {
			typedQuery.setParameter("categoryId", categoryId);
		}
		if (StringUtils.isNotBlank(srchStartDate) && StringUtils.isNotBlank(srchEndDate)) {
			typedQuery.setParameter("srchStartDate", srchStartDate);
			typedQuery.setParameter("srchEndDate", srchEndDate);
		}
		
		// 결과 값 Binding
		Optional<T> first = typedQuery.getResultList().stream().findFirst();
		Long totalCount = Long.valueOf(first.get().toString());
		
		return totalCount;
	}
	
	/**
	 * 현재 상품을 사용중인 User 카운트
	 * @param categoryId
	 * @param srchStartDate
	 * @param srchEndDate
	 * @return
	 */
	public Long queryCountOfUsersUsing(String categoryId, String srchStartDate, String srchEndDate) {
		return queryCountOfUsersUsing(categoryId, srchStartDate, srchEndDate, null);
	}
	
	/**
	 * 현재 상품을 사용중인 User 카운트
	 * @param categoryId
	 * @param srchStartDate
	 * @param srchEndDate
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Long queryCountOfUsersUsing(String categoryId, String srchStartDate, String srchEndDate, String userId) {
		// SQL생성
		StringBuffer excQuery = new StringBuffer();
		excQuery.append("SELECT  COUNT(DISTINCT it.created_by) AS count \n");
		excQuery.append("FROM    instance it \n");
		excQuery.append("        INNER JOIN software so \n");
		excQuery.append("            ON (so.id = it.software_id) \n");
		excQuery.append("WHERE   1=1 \n");
		excQuery.append("AND     it.status = 'Approval' \n");
		if (StringUtils.isNotBlank(userId)) {
			excQuery.append("AND     so.created_by = :userId \n");
		}
		if (StringUtils.isNotBlank(categoryId)) {
			excQuery.append("AND     so.category_id = :categoryId \n");
		}
		if (StringUtils.isNotBlank(srchStartDate) && StringUtils.isNotBlank(srchEndDate)) {
			excQuery.append("AND     DATE_FORMAT(so.created_date, '%Y%m%d') BETWEEN DATE_FORMAT(:srchStartDate, '%Y%m%d') \n");
			excQuery.append("                                                   AND DATE_FORMAT(:srchEndDate, '%Y%m%d') \n");
		}
		
		// 쿼리생성
		Query typedQuery = entityManager.createNativeQuery(excQuery.toString());
		
		// Parameter 설정
		if (StringUtils.isNotBlank(userId)) {
			typedQuery.setParameter("userId", userId);
		}
		if (StringUtils.isNotBlank(categoryId)) {
			typedQuery.setParameter("categoryId", categoryId);
		}
		if (StringUtils.isNotBlank(srchStartDate) && StringUtils.isNotBlank(srchEndDate)) {
			typedQuery.setParameter("srchStartDate", srchStartDate);
			typedQuery.setParameter("srchEndDate", srchEndDate);
		}
		
		// 결과 값 Binding
		Optional<T> first = typedQuery.getResultList().stream().findFirst();
		Long totalCount = Long.valueOf(first.get().toString());
		
		return totalCount;
	}
	
	/**
	 * 상품별 사용앱 데이터 조회 (Chart)
	 * @param userId
	 * @param categoryId
	 * @param srchStartDate
	 * @param srchEndDate
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> queryStatsUseApp(String userId, String categoryId, String srchStartDate, String srchEndDate) {
		// 조회 결과값 반환 객체 생성
		List<Map<String,Object>> excuteResult = new ArrayList<Map<String,Object>>();
		
		// SQL생성
		StringBuffer excQuery = new StringBuffer();
		excQuery.append("SELECT  so.name \n");
		excQuery.append("        ,COUNT(0) AS totalCount \n");
		excQuery.append("FROM    software so \n");
		excQuery.append("        INNER JOIN instance it \n");
		excQuery.append("            ON(it.software_id = so.id) \n");
		excQuery.append("WHERE   1=1 \n");
		excQuery.append("AND     it.status = 'Approval' \n");
		if (StringUtils.isNotBlank(userId)) {
			excQuery.append("AND     so.created_by = :userId \n");
		}
		if (StringUtils.isNotBlank(categoryId)) {
			excQuery.append("AND     so.category_id = :categoryId \n");
		}
		if (StringUtils.isNotBlank(srchStartDate)) {
			excQuery.append("AND     DATE_FORMAT(so.created_date, '%Y%m%d') BETWEEN DATE_FORMAT(:srchStartDate, '%Y%m%d') \n");
			excQuery.append("                                                   AND DATE_FORMAT(:srchEndDate, '%Y%m%d') \n");
		}
		excQuery.append("GROUP BY so.name \n");
		excQuery.append("ORDER BY totalCount DESC \n");
		
		// 쿼리생성
		Query typedQuery = entityManager.createNativeQuery(excQuery.toString());
		
		// Parameter 설정
		if (StringUtils.isNotBlank(userId)) {
			typedQuery.setParameter("userId", userId);
		}
		if (StringUtils.isNotBlank(categoryId)) {
			typedQuery.setParameter("categoryId", categoryId);
		}
		if (StringUtils.isNotBlank(srchStartDate) && StringUtils.isNotBlank(srchEndDate)) {
			typedQuery.setParameter("srchStartDate", srchStartDate);
			typedQuery.setParameter("srchEndDate", srchEndDate);
		}
		
		// 결과 값 Binding
		List<Object[]> resultList = typedQuery.getResultList();
		if (CollectionUtils.isEmpty(resultList)) {
			return excuteResult;
		}
		Map<String,Object> tempRowValues = null;
		for (Object[] arrInfo : resultList) {
			tempRowValues = new HashMap<String,Object>();
			tempRowValues.put("name", arrInfo[0]);
			tempRowValues.put("data", arrInfo[1]);
			excuteResult.add(tempRowValues);
		}
		
		return excuteResult;
	}
	
	/**
	 * 상품별 사용추이 데이터 조회 (Chart)
	 * @param userId
	 * @param categoryId
	 * @param srchStartDate
	 * @param srchEndDate
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> queryStatsUseTransition(String userId, String categoryId, String srchStartDate, String srchEndDate) {
		// 조회 결과값 반환 객체 생성
		List<Map<String,Object>> excuteResult = new ArrayList<Map<String,Object>>();
		
		// SQL생성
		StringBuffer excQuery = new StringBuffer();
		excQuery.append("SELECT  bs.yearMonth  \n");
		excQuery.append("        ,bs.softwareName \n");
		excQuery.append("        ,IFNULL(dt.usageCount,0) AS usageCount \n");
		excQuery.append("FROM    ( \n");
		excQuery.append("            SELECT  a.yearMonth \n");
		excQuery.append("                    ,b.softwareName \n");
		excQuery.append("            FROM    ( \n");
		excQuery.append("                        SELECT  DATE_FORMAT(dt, '%Y-%m-01') AS yearMonth \n");
		excQuery.append("                                ,ym AS compareDate  \n");
		excQuery.append("                        FROM    calendar \n");
		excQuery.append("                        WHERE   1=1 \n");
		if (StringUtils.isNotBlank(srchStartDate) && StringUtils.isNotBlank(srchEndDate)) {
			excQuery.append("                    AND     ym BETWEEN DATE_FORMAT(:srchStartDate, '%Y%m') \n");
			excQuery.append("                                   AND DATE_FORMAT(:srchEndDate, '%Y%m') \n");
		} else {
			excQuery.append("                    AND     ym BETWEEN DATE_FORMAT(DATE_ADD(NOW(), INTERVAL -5 MONTH), '%Y%m') \n");
			excQuery.append("                                   AND DATE_FORMAT(NOW(), '%Y%m')  \n");
		}
		excQuery.append("                        GROUP BY ym  \n");
		excQuery.append("                    ) a  \n");
		excQuery.append("                    CROSS JOIN ( \n");
		excQuery.append("                        SELECT  so.name  AS softwareName \n");
		excQuery.append("                        FROM    software so  \n");
		excQuery.append("                                INNER JOIN instance it \n");
		excQuery.append("                                    ON (it.software_id = so.id) \n");
		excQuery.append("                        WHERE   1=1 \n");
		if (StringUtils.isNotBlank(userId)) {
			excQuery.append("                    AND     so.created_by = :userId \n");
		}
		if (StringUtils.isNotBlank(categoryId)) {
			excQuery.append("                    AND     so.category_id = :categoryId \n");
		}
		if (StringUtils.isNotBlank(srchStartDate) && StringUtils.isNotBlank(srchEndDate)) {
			excQuery.append("                    AND     DATE_FORMAT(so.created_date, '%Y%m%d') BETWEEN DATE_FORMAT(:srchStartDate, '%Y%m%d') \n");
			excQuery.append("                                                                       AND DATE_FORMAT(:srchEndDate, '%Y%m%d') \n");
		} else {
			
		}
		excQuery.append("                        GROUP BY so.name \n");
		excQuery.append("                    ) b \n");
		excQuery.append("        ) bs \n");
		excQuery.append("        LEFT JOIN ( \n");
		excQuery.append("            SELECT  cl.yearMonth  \n");
		excQuery.append("                    ,so.name  AS softwareName \n");
		excQuery.append("                    ,COUNT(0) AS usageCount \n");
		excQuery.append("            FROM    software so  \n");
		excQuery.append("                    INNER JOIN instance it \n");
		excQuery.append("                        ON (it.software_id = so.id) \n");
		excQuery.append("                    CROSS JOIN (  \n");
		excQuery.append("                        SELECT  DATE_FORMAT(dt, '%Y-%m-01') AS yearMonth \n");
		excQuery.append("                                ,ym AS compareDate  \n");
		excQuery.append("                        FROM    calendar \n");
		excQuery.append("                        WHERE   1=1 \n");
		if (StringUtils.isNotBlank(srchStartDate) && StringUtils.isNotBlank(srchEndDate)) {
			excQuery.append("                    AND     ym BETWEEN DATE_FORMAT(:srchStartDate, '%Y%m') \n");
			excQuery.append("                                   AND DATE_FORMAT(:srchEndDate, '%Y%m') \n");
		} else {
			excQuery.append("                    AND     ym BETWEEN DATE_FORMAT(DATE_ADD(NOW(), INTERVAL -5 MONTH), '%Y%m') \n");
			excQuery.append("                                   AND DATE_FORMAT(NOW(), '%Y%m')  \n");
		}
		excQuery.append("                        GROUP BY ym  \n");
		excQuery.append("                    ) cl \n");
		excQuery.append("            WHERE   1=1 \n");
		excQuery.append("            AND     1 = (CASE WHEN DATE_FORMAT(it.usage_start_date, '%Y%m') <= cl.compareDate \n");
		excQuery.append("                               AND DATE_FORMAT(IFNULL(it.usage_end_date, NOW()), '%Y%m') >= cl.compareDate \n");
		excQuery.append("                              THEN 1 \n");
		excQuery.append("                              ELSE 0 \n");
		excQuery.append("                        END) \n");
		if (StringUtils.isNotBlank(userId)) {
			excQuery.append("        AND     so.created_by = :userId \n");
		}
		if (StringUtils.isNotBlank(categoryId)) {
			excQuery.append("        AND     so.category_id = :categoryId \n");
		}
		if (StringUtils.isNotBlank(srchStartDate) && StringUtils.isNotBlank(srchEndDate)) {
			excQuery.append("        AND     DATE_FORMAT(so.created_date, '%Y%m%d') BETWEEN DATE_FORMAT(:srchStartDate, '%Y%m%d') \n");
			excQuery.append("                                                           AND DATE_FORMAT(:srchEndDate, '%Y%m%d') \n");
		} else {
			excQuery.append("        AND     DATE_FORMAT(so.created_date, '%Y%m%d') BETWEEN DATE_FORMAT(DATE_ADD(NOW(), INTERVAL -5 MONTH), '%Y%m%d') \n");
			excQuery.append("                                                           AND DATE_FORMAT(NOW(), '%Y%m%d')  \n");
		}
		excQuery.append("            GROUP BY cl.yearMonth, so.name \n");
		excQuery.append("        ) dt \n");
		excQuery.append("        ON (dt.yearMonth = bs.yearMonth AND dt.softwareName = bs.softwareName) \n");
		
		// 쿼리생성
		Query typedQuery = entityManager.createNativeQuery(excQuery.toString());
		
		// Parameter 설정
		if (StringUtils.isNotBlank(userId)) {
			typedQuery.setParameter("userId", userId);
		}
		if (StringUtils.isNotBlank(categoryId)) {
			typedQuery.setParameter("categoryId", categoryId);
		}
		if (StringUtils.isNotBlank(srchStartDate) && StringUtils.isNotBlank(srchEndDate)) {
			typedQuery.setParameter("srchStartDate", srchStartDate);
			typedQuery.setParameter("srchEndDate", srchEndDate);
		}
		
		// 결과 값 Binding
		List<Object[]> resultList = typedQuery.getResultList();
		if (CollectionUtils.isEmpty(resultList)) {
			return excuteResult;
		}
		Map<String,Object> tempRowValues = null;
		for (Object[] arrInfo : resultList) {
			tempRowValues = new HashMap<String,Object>();
			tempRowValues.put("yearMonth", arrInfo[0]);
			tempRowValues.put("name", arrInfo[1]);
			tempRowValues.put("usageCount", arrInfo[2]);
			excuteResult.add(tempRowValues);
		}
		
		return excuteResult;
	}
}
