/*
 Navicat Premium Dump SQL

 Source Server         : doj
 Source Server Type    : MySQL
 Source Server Version : 90400 (9.4.0)
 Source Host           : localhost:3306
 Source Schema         : doj_problem

 Target Server Type    : MySQL
 Target Server Version : 90400 (9.4.0)
 File Encoding         : 65001

 Date: 25/01/2026 16:43:21
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for problem
-- ----------------------------
DROP TABLE IF EXISTS `problem`;
CREATE TABLE `problem` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `description` mediumtext NOT NULL,
  `input_style` text NOT NULL,
  `output_style` text NOT NULL,
  `input_sample` json NOT NULL,
  `output_sample` json NOT NULL,
  `difficulty` varchar(10) NOT NULL,
  `time_limit` int NOT NULL,
  `memory_limit` int NOT NULL,
  `hint` text,
  `total_pass` int NOT NULL DEFAULT '0',
  `total_attempt` int NOT NULL DEFAULT '0',
  `test_data` longtext NOT NULL,
  `test_ans` longtext NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_problem_difficulty` (`difficulty`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for problem_tag
-- ----------------------------
DROP TABLE IF EXISTS `problem_tag`;
CREATE TABLE `problem_tag` (
  `problem_id` bigint NOT NULL,
  `tag_id` bigint NOT NULL,
  PRIMARY KEY (`problem_id`,`tag_id`),
  KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

SET FOREIGN_KEY_CHECKS = 1;
