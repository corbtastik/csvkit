-- ----------------------------
--  Table structure for `Master`
-- ----------------------------
DROP TABLE IF EXISTS `csv_lines`;
CREATE TABLE `csv_lines` (
  `id`     BIGINT AUTO_INCREMENT,
  `tag`    VARCHAR(255) DEFAULT NULL,
  `text`   VARCHAR(4096) DEFAULT NULL
)