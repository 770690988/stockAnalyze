/*
 Navicat Premium Data Transfer

 Source Server         : biubiucomputer
 Source Server Type    : MySQL
 Source Server Version : 90600
 Source Host           : 120.55.248.31:4488
 Source Schema         : stock_analyze

 Target Server Type    : MySQL
 Target Server Version : 90600
 File Encoding         : 65001

 Date: 20/05/2026 22:53:56
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sector_stock
-- ----------------------------
DROP TABLE IF EXISTS `sector_stock`;
CREATE TABLE `sector_stock`  (
  `sector_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `stock_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`sector_code`, `stock_code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for selected_stock
-- ----------------------------
DROP TABLE IF EXISTS `selected_stock`;
CREATE TABLE `selected_stock`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '股票编码（唯一标识）',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '股票名称',
  `type` int(0) NULL DEFAULT 4 COMMENT '类型 1=板块 2=概念 3=地域（关联BKType枚举） 4=个股',
  `current_price` decimal(10, 3) NULL DEFAULT NULL COMMENT '当前价格',
  `take_num` int(0) NULL DEFAULT NULL COMMENT '持股数量',
  `cost_price` decimal(10, 3) NULL DEFAULT NULL COMMENT '成本价',
  `upgrade_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `code`(`code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for stock_bk
-- ----------------------------
DROP TABLE IF EXISTS `stock_bk`;
CREATE TABLE `stock_bk`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '版块编码（唯一标识）',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '版块名称',
  `type` int(0) NULL DEFAULT 1 COMMENT '类型 1=板块 2=概念 3=地域（关联BKType枚举） 4=个股',
  `hot_num` decimal(10, 0) NULL DEFAULT 0 COMMENT '热度（用于排序展示）',
  `create_date` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `ths_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '同花顺版块Code（多源数据兼容）',
  `ths_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '同花顺版块Name（多源数据兼容）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `code`(`code`) USING BTREE,
  INDEX `idx_stock_bk_1`(`code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5995 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '股票版块信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for stock_bk_stock
-- ----------------------------
DROP TABLE IF EXISTS `stock_bk_stock`;
CREATE TABLE `stock_bk_stock`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `stock_code` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '股票编码',
  `stock_name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '股票名称',
  `bk_code` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '版块编码',
  `bk_name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '版块名称',
  `bk_type` tinyint(1) NULL DEFAULT 2 COMMENT '版块类型 1为版块 2为概念 3为地区',
  `create_date` timestamp(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '创建日期',
  `score_type` int(0) NULL DEFAULT NULL COMMENT '版块内排名 优良中及差',
  `score_message` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '排名的原因',
  `market_num` int(0) NULL DEFAULT NULL COMMENT '市值排名',
  `market_percent` decimal(10, 2) NULL DEFAULT NULL COMMENT '排名比例',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_stock_bk_stock_1`(`stock_code`) USING BTREE,
  INDEX `idx_stock_bk_stock_2`(`bk_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100832 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for stock_money_flow
-- ----------------------------
DROP TABLE IF EXISTS `stock_money_flow`;
CREATE TABLE `stock_money_flow`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `stock_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '股票代码',
  `stock_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '股票名称',
  `stock_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '股票当前价',
  `stock_price_max` decimal(10, 2) NULL DEFAULT NULL COMMENT '股票最高价',
  `stock_price_min` decimal(10, 2) NULL DEFAULT NULL COMMENT '股票最低价',
  `stock_price_start` decimal(10, 2) NULL DEFAULT NULL COMMENT '股票开盘价格',
  `turnover_rate` decimal(10, 2) NULL DEFAULT NULL COMMENT '换手率',
  `volume_ratio` decimal(10, 2) NULL DEFAULT NULL COMMENT '量比',
  `per_roll` decimal(10, 2) NULL DEFAULT NULL COMMENT '市盈率',
  `volume` decimal(14, 2) NULL DEFAULT NULL COMMENT '成交量',
  `amount` decimal(20, 2) NULL DEFAULT NULL COMMENT '成交额',
  `stock_price_rate` decimal(10, 2) NULL DEFAULT NULL COMMENT '股票今日涨跌幅',
  `main_net` decimal(20, 2) NULL DEFAULT NULL COMMENT '主力净流入（元）',
  `small_net` decimal(20, 2) NULL DEFAULT NULL COMMENT '净小单流入（元）',
  `middle_net` decimal(20, 2) NULL DEFAULT NULL COMMENT '净中单流入（元）',
  `large_net` decimal(20, 2) NULL DEFAULT NULL COMMENT '净大单流入（元）',
  `super_net` decimal(20, 2) NULL DEFAULT NULL COMMENT '净超大单流入（元）',
  `trade_date` datetime(0) NULL DEFAULT NULL COMMENT '交易日期',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `stock_code`(`stock_code`, `trade_date`) USING BTREE,
  INDEX `idx_stock_code`(`stock_code`) USING BTREE,
  INDEX `idx_trade_date`(`trade_date`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2546121 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '股票资金流向表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for stock_watchlist_bk
-- ----------------------------
DROP TABLE IF EXISTS `stock_watchlist_bk`;
CREATE TABLE `stock_watchlist_bk`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID（板块ID，从990000开始）',
  `bk_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '板块名称',
  `user_id` bigint(0) NOT NULL DEFAULT 1 COMMENT '用户id  默认是主用户',
  `type` int(0) NULL DEFAULT NULL COMMENT '板块类型',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '板块备注',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_type`(`type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 990010 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '自选板块表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for stock_watchlist_bk_stock
-- ----------------------------
DROP TABLE IF EXISTS `stock_watchlist_bk_stock`;
CREATE TABLE `stock_watchlist_bk_stock`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `bk_id` int(0) NULL DEFAULT NULL COMMENT '板块ID（关联stock_watchlist_bk.id）',
  `stock_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '股票代码',
  `stock_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '股票名称',
  `add_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '加入自选的理由',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `sort` int(0) NULL DEFAULT 0 COMMENT '排序权重',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_bk_id`(`bk_id`) USING BTREE,
  INDEX `idx_stock_code`(`stock_code`) USING BTREE,
  UNIQUE INDEX `uk_bk_stock_reason`(`bk_id`, `stock_code`, `add_reason`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 304 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '自选板块股票关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for stock_watchlist_bk_type
-- ----------------------------
DROP TABLE IF EXISTS `stock_watchlist_bk_type`;
CREATE TABLE `stock_watchlist_bk_type`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `type_label` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型名称',
  `type_value` int(0) NOT NULL COMMENT '类型值',
  `user_id` bigint(0) NOT NULL DEFAULT 1 COMMENT '用户id  默认是主用户',
  `sort` int(0) NULL DEFAULT 0 COMMENT '排序',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_type_value`(`type_value`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '自选板块类型表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for trade_calendar
-- ----------------------------
DROP TABLE IF EXISTS `trade_calendar`;
CREATE TABLE `trade_calendar`  (
  `trade_date` date NOT NULL COMMENT '日期',
  `is_trading` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否交易日 1=是 0=否',
  `remark` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注 如：春节、国庆等',
  PRIMARY KEY (`trade_date`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `created_at` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
