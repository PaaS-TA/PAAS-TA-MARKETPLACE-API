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
		StringBuffer executeQuery = new StringBuffer();
		executeQuery.append("SELECT  COUNT(0) \n");
		executeQuery.append("FROM	 ( \n");
		executeQuery.append("			SELECT  COUNT(0) \n");
		executeQuery.append("			FROM    ( \n");
		executeQuery.append("            			SELECT  so.id AS softwareId \n");
		executeQuery.append("                    			,so.category_id AS categoryId \n");
		executeQuery.append("                    			,(SELECT name FROM category s1 WHERE s1.id = so.category_id) AS categoryName \n");
		executeQuery.append("                    			,so.name AS softawareName \n");
		executeQuery.append("                    			,so.version \n");
		executeQuery.append("                    			,DATE_FORMAT(so.created_date,'%Y-%m-%d') AS createdDate \n");
		executeQuery.append("                    			,IFNULL(sp.cpu_amt,0)+IFNULL(sp.memory_amt,0)+IFNULL(sp.disk_amt,0) AS softwarePlanAmtMonth \n");
		executeQuery.append("            			FROM    software so \n");
		executeQuery.append("                    			LEFT JOIN instance it \n");
		executeQuery.append("                       			ON (it.software_id = so.id) \n");
		executeQuery.append("                    			LEFT JOIN software_plan sp \n");
		executeQuery.append("                        			ON (sp.id = it.software_plan_id) \n");
		executeQuery.append("            			WHERE   1=1 \n");
		if (StringUtils.isNotBlank(srchDate)) {
			executeQuery.append("            AND     DATE_FORMAT(:srchDate,'%Y%m') BETWEEN DATE_FORMAT(it.usage_start_date,'%Y%m') \n");
			executeQuery.append("                                                      AND DATE_FORMAT(IFNULL(it.usage_end_date, NOW()),'%Y%m') \n");
		}
		if (StringUtils.isNotBlank(categoryId)) {
			executeQuery.append("AND     so.category_id = :categoryId\n");
		}
		executeQuery.append("            			AND     so.status = 'Approval' \n");
		executeQuery.append("            			AND     so.created_by = :userId \n");
		executeQuery.append("        			) t \n");
		executeQuery.append("			GROUP BY softwareId, categoryId, categoryName, softawareName, version, createdDate \n");
		executeQuery.append("	) r \n");
		
		// 쿼리생성
		Query typedQuery = entityManager.createNativeQuery(executeQuery.toString());
		
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
		StringBuffer executeQuery = new StringBuffer();
		executeQuery.append("SELECT  softwareId \n");
		executeQuery.append("        ,categoryId \n");
		executeQuery.append("        ,categoryName \n");
		executeQuery.append("        ,softawareName \n");
		executeQuery.append("        ,version \n");
		executeQuery.append("        ,createdDate \n");
		executeQuery.append("        ,SUM(softwarePlanAmtMonth) AS softwarePlanAmtMonth \n");
		executeQuery.append("        ,CASE WHEN SUM(softwarePlanAmtMonth) > 0 THEN COUNT(0) \n");
		executeQuery.append("              ELSE 0 \n");
		executeQuery.append("        END AS sellCount \n");
		executeQuery.append("FROM    ( \n");
		executeQuery.append("            SELECT  so.id AS softwareId \n");
		executeQuery.append("                    ,so.category_id AS categoryId \n");
		executeQuery.append("                    ,(SELECT name FROM category s1 WHERE s1.id = so.category_id) AS categoryName \n");
		executeQuery.append("                    ,so.name AS softawareName \n");
		executeQuery.append("                    ,so.version \n");
		executeQuery.append("                    ,DATE_FORMAT(so.created_date,'%Y-%m-%d') AS createdDate \n");
		executeQuery.append("                    ,IFNULL(sp.cpu_amt,0)+IFNULL(sp.memory_amt,0)+IFNULL(sp.disk_amt,0) AS softwarePlanAmtMonth \n");
		executeQuery.append("            FROM    software so \n");
		executeQuery.append("                    LEFT JOIN instance it \n");
		executeQuery.append("                        ON (it.software_id = so.id) \n");
		executeQuery.append("                    LEFT JOIN software_plan sp \n");
		executeQuery.append("                        ON (sp.id = it.software_plan_id) \n");
		executeQuery.append("            WHERE   1=1 \n");
		if (StringUtils.isNotBlank(srchDate)) {
			executeQuery.append("            AND     DATE_FORMAT(:srchDate,'%Y%m') BETWEEN DATE_FORMAT(it.usage_start_date,'%Y%m') \n");
			executeQuery.append("                                                      AND DATE_FORMAT(IFNULL(it.usage_end_date, NOW()),'%Y%m') \n");
		}
		if (StringUtils.isNotBlank(categoryId)) {
			executeQuery.append("AND     so.category_id = :categoryId\n");
		}
		executeQuery.append("            AND     so.status = 'Approval' \n");
		executeQuery.append("            AND     so.created_by = :userId \n");
		executeQuery.append("        ) t \n");
		executeQuery.append("GROUP BY softwareId, categoryId, categoryName, softawareName, version, createdDate \n");
		executeQuery.append("ORDER BY softwareId DESC \n");
		executeQuery.append("LIMIT :pageStart, :pageEnd \n");
		
		// 쿼리생성
		Query typedQuery = entityManager.createNativeQuery(executeQuery.toString());
		
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
}
