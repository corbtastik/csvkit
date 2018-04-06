DROP TABLE IF EXISTS `csv_metadata`;
CREATE TABLE `csv_metadata` (
  `id`       BIGINT AUTO_INCREMENT,
  `tag`      VARCHAR(255) DEFAULT NULL,
  `property` VARCHAR(255) DEFAULT NULL,
  `value`    VARCHAR(1024) DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX (`tag`)
);