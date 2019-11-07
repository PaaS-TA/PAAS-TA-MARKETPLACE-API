INSERT INTO calendar
SELECT dt, ym, DATE_FORMAT(LAST_DAY(dt), '%d') AS days, NOW()
  FROM (SELECT date_format(dt,'%Y%m') ym
             , Day(dt) d, DATE_FORMAT(dt,'%Y%m%d') dt
          FROM (SELECT CONCAT(y, '0101') + INTERVAL a*100 + b*10 + c DAY dt
                  FROM (SELECT 0 a
                        UNION ALL SELECT 1
                        UNION ALL SELECT 2
                        UNION ALL SELECT 3
                        ) a
                     , (SELECT 0 b
                        UNION ALL SELECT 1
                        UNION ALL SELECT 2
                        UNION ALL SELECT 3
                        UNION ALL SELECT 4
                        UNION ALL SELECT 5
                        UNION ALL SELECT 6
                        UNION ALL SELECT 7
                        UNION ALL SELECT 8
                        UNION ALL SELECT 9
                        ) b
                     , (SELECT 0 c
                        UNION ALL SELECT 1
                        UNION ALL SELECT 2
                        UNION ALL SELECT 3
                        UNION ALL SELECT 4
                        UNION ALL SELECT 5
                        UNION ALL SELECT 6
                        UNION ALL SELECT 7
                        UNION ALL SELECT 8
                        UNION ALL SELECT 9
                        ) c
                     , (
                        SELECT '2019' y union
                        SELECT '2020' y union
                        SELECT '2021' y union
                        SELECT '2022' y union
                        SELECT '2023' y union
                        SELECT '2024' y union
                        SELECT '2025' y union
                        SELECT '2026' y union
                        SELECT '2027' y union
                        SELECT '2028' y union
                        SELECT '2029' y union
                        SELECT '2030' y union
                        SELECT '2031' y union
                        SELECT '2032' y union
                        SELECT '2033' y union
                        SELECT '2034' y union
                        SELECT '2035' y union
                        SELECT '2036' y union
                        SELECT '2037' y union
                        SELECT '2038' y union
                        SELECT '2039' y union
                        SELECT '2040' y union
                        SELECT '2041' y union
                        SELECT '2042' y union
                        SELECT '2043' y union
                        SELECT '2044' y union
                        SELECT '2045' y union
                        SELECT '2046' y union
                        SELECT '2047' y union
                        SELECT '2048' y union
                        SELECT '2049' y union
                        SELECT '2050' y
                        ) d
                 WHERE a*100 + b*10 + c < DayOfYear(CONCAT(y, '1231'))
                ) a
        ) a
 WHERE dt >= STR_TO_DATE(CONCAT('201901', '01'), '%Y%m%d')
   AND dt < DATE_FORMAT(date_add(DATE_FORMAT(STR_TO_DATE(CONCAT('205012', '01'), '%Y%m%d'),'%Y-%m-%d:%H%i%S'), interval 1 month), '%Y%m%d')
 ORDER BY ym, d
ON
	DUPLICATE KEY
UPDATE
	last_modified_date = NOW()
;