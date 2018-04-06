DROP TABLE IF EXISTS `csv_lines`;
CREATE TABLE `csv_lines` (
  `id`     BIGINT AUTO_INCREMENT,
  `tag`    VARCHAR(255) DEFAULT NULL,
  `text`   VARCHAR(4096) DEFAULT NULL,
  CONSTRAINT pk_csv_lines PRIMARY KEY (`id`)
);

CREATE INDEX idx_csv_lines_tag ON `csv_lines` (`tag`);
