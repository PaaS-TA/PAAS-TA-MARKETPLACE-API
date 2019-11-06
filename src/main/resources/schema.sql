CREATE TABLE IF NOT EXISTS `calendar` (
  `dt` char(8) CHARACTER SET latin1 DEFAULT NULL,
  `ym` char(6) CHARACTER SET latin1 DEFAULT NULL,
  `days` int(11) DEFAULT NULL,
  `last_modified_date` datetime DEFAULT NULL,
  PRIMARY KEY (`dt`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
;