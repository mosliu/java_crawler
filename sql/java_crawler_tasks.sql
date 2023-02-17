/*
 Navicat Premium Data Transfer

 Source Server         : 172.23.16.80 3306
 Source Server Type    : MariaDB
 Source Server Version : 100038
 Source Host           : 172.23.16.80:3306
 Source Schema         : feeds_db

 Target Server Type    : MariaDB
 Target Server Version : 100038
 File Encoding         : 65001

 Date: 10/02/2023 10:13:48
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for java_crawler_tasks
-- ----------------------------
DROP TABLE IF EXISTS `java_crawler_tasks`;
CREATE TABLE `java_crawler_tasks`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '任务id',
  `task_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '任务名称',
  `task_type` int(4) NULL DEFAULT NULL COMMENT '任务类型',
  `baseUrl` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '基础url',
  `site_conf` varchar(10000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'site配置情况，json',
  `rule` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'json,解析规则',
  `downloader` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'downloader',
  `processor` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'processor',
  `pipelines` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'pipeline,逗号分隔',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `comment` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '任务说明',
  `is_active` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `task_name_unique`(`task_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

SET FOREIGN_KEY_CHECKS = 1;
