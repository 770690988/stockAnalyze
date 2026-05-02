package com.biubiu.stock.stockanalyze.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author biubiu
 * @Description 自选板块股票对照model
 * @Date 2026/5/2 12:31
 */
@Data
@TableName("stock_watchlist_bk_stock")
public class StockWatchlistBkStock {

    @TableId(type = IdType.AUTO)
    private Integer id;

    // 板块ID
    @TableField("bk_id")
    private Integer bkId;

    // 股票代码
    @TableField("stock_code")
    private String stockCode;

    // 股票名称
    @TableField("stock_name")
    private String stockName;

    // 加入自选的理由
    @TableField("add_reason")
    private String addReason;

    // 备注
    @TableField("remark")
    private String remark;

    // 排序权重
    @TableField("sort")
    private Integer sort;

    // 创建时间
    @TableField("create_time")
    private LocalDateTime createTime;

    // 更新时间
    @TableField("update_time")
    private LocalDateTime updateTime;
}