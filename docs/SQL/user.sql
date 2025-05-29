CREATE DATABASE doj_user;
USE doj_user;

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `avatar` VARCHAR(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `password` VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `score` INT DEFAULT NULL,
  `rank` INT DEFAULT NULL,
  `school` VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `gender` TINYINT(1) DEFAULT NULL,  -- 0: 男, 1: 女
  `easy_solve` INT DEFAULT NULL,
  `middle_solve` INT DEFAULT NULL,
  `hard_solve` INT DEFAULT NULL,
  `role` TINYINT(1) DEFAULT NULL,   -- 0: 管理员, 1: 普通用户
  `url` VARCHAR(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sign` LONGTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `fans` BIGINT DEFAULT NULL,
  `subscribe` BIGINT DEFAULT NULL,
  `ban` TINYINT(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

INSERT INTO `user` (`username`, `avatar`, `email`, `password`, `score`, `rank`, `school`, `gender`, `easy_solve`, `middle_solve`, `hard_solve`, `role`, `url`, `sign`, `fans`, `subscribe`, `ban`) VALUES
('decade', '', 'decade@example.com', '123', 100, 20, 'Some School', 1, 5, 3, 1, 1, 'http://example.com/decade', '普通用户', 50, 10, 0),
('root', '', 'root@example.com', '123', 200, 10, 'Some University', 0, 10, 8, 5, 0, 'http://example.com/root', '管理员', 200, 50, 0);

