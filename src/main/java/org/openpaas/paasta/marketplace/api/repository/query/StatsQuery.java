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
		// Query 생성
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
		
		// Query 생성
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
		
		// Query 생성
		StringBuffer excQuery = new StringBuffer();
		excQuery.append("SELECT  it.created_by AS userId \n");
		excQuery.append("        ,COUNT(0) AS instanceCount \n");
		excQuery.append("        ,cj.totalInstanceCount \n");
		excQuery.append("        ,(COUNT(0) / MIN(cj.totalInstanceCount)) * 100 AS purchaserPercent \n");
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
	public List<Map<String,Object>> queryPurchaseTransitionMonth(String userId) {
		// 조회 결과값 반환 객체 생성
		List<Map<String,Object>> excuteResult = new ArrayList<Map<String,Object>>();
		
		// Query 생성
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
}
