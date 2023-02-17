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

 Date: 10/02/2023 10:13:37
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for java_crawler_hot_point_content
-- ----------------------------
DROP TABLE IF EXISTS `java_crawler_hot_point_content`;
CREATE TABLE `java_crawler_hot_point_content`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type` char(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '\"baidu\"百度风云榜 \"weixin_reci\"微信热搜 \"weibo\" 实时热点  \"weibo_realtimehot\" 热搜榜 \"weibo_socialevent\" 新时代 \"qq\"',
  `group_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '热搜组 json ',
  `url` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `url_md5` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `gid` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '一组标识，整个榜单采集时间',
  `num` int(10) NULL DEFAULT NULL COMMENT '0',
  `add_time` datetime(0) NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '添加时间',
  `desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '导语或摘要',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `hot_num` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '热点值',
  `sort_num` varchar(60) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '排行',
  `status` tinyint(4) NULL DEFAULT 0 COMMENT '抓取状态码',
  `city_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `url_md5`(`url_md5`, `gid`) USING BTREE,
  INDEX `type`(`type`) USING BTREE,
  INDEX `add`(`add_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 258 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '热点头条-带正文摘要部分' ROW_FORMAT = Compact;

SET FOREIGN_KEY_CHECKS = 1;
