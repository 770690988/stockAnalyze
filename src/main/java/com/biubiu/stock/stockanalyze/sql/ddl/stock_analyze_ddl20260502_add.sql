DROP TABLE IF EXISTS `stock_watchlist_bk`;
CREATE TABLE `stock_watchlist_bk` (
                                      `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID（板块ID，从990000开始）',
                                      `bk_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '板块名称',
                                      `type` int(0) NULL DEFAULT NULL COMMENT '板块类型',
                                      `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '板块备注',
                                      `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                                      `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
                                      PRIMARY KEY (`id`) USING BTREE,
                                      INDEX `idx_type` (`type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 990000 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '自选板块表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `stock_watchlist_bk_stock`;
CREATE TABLE `stock_watchlist_bk_stock` (
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
                                            UNIQUE INDEX `uk_bk_stock` (`bk_id`, `stock_code`) USING BTREE,
                                            INDEX `idx_bk_id` (`bk_id`) USING BTREE,
                                            INDEX `idx_stock_code` (`stock_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '自选板块股票关联表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `stock_watchlist_bk_type`;
CREATE TABLE `stock_watchlist_bk_type` (
                                           `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                           `type_label` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型名称',
                                           `type_value` int(0) NOT NULL COMMENT '类型值',
                                           `sort` int(0) NULL DEFAULT 0 COMMENT '排序',
                                           `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
                                           `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                                           `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
                                           PRIMARY KEY (`id`) USING BTREE,
                                           UNIQUE INDEX `uk_type_value` (`type_value`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '自选板块类型表' ROW_FORMAT = Dynamic;

-- 初始化数据
INSERT INTO `stock_watchlist_bk_type` (`type_label`, `type_value`, `sort`, `remark`, `create_time`, `update_time`)
VALUES
    ('5月主线_电池', 1, 1, NULL, NOW(), NOW()),
    ('5月主线_npu', 2, 2, NULL, NOW(), NOW()),
    ('5月主线_商业航天',   3, 3, NULL, NOW(), NOW());