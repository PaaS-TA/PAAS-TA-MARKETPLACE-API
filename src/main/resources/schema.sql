CREATE TABLE IF NOT EXISTS `calendar` (
  `dt` char(8) CHARACTER SET latin1 DEFAULT NULL COMMENT '날짜(연월일)',
  `ym` char(6) CHARACTER SET latin1 DEFAULT NULL COMMENT '연월',
  `days` int(11) DEFAULT NULL COMMENT '월별 일수',
  `last_modified_date` datetime DEFAULT NULL COMMENT '최종 수정일',
  PRIMARY KEY (`dt`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='통계용 날짜 테이블'
;