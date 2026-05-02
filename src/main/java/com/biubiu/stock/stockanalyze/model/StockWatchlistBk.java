package com.biubiu.stock.stockanalyze.model;

/**
 * @Author biubiu
 * @Description 自选板块model
 * @Date 2026/5/2 12:30
 */
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("stock_watchlist_bk")
public class StockWatchlistBk {

    @TableId(type = IdType.AUTO)
    private Integer id;

    // 板块名称
    @TableField("bk_name")
    private String bkName;

    // 板块类型
    @TableField("type")
    private Integer type;

    // 板块备注
    @TableField("remark")
    private String remark;

    // 创建时间
    @TableField("create_time")
    private LocalDateTime createTime;

    // 更新时间
    @TableField("update_time")
    private LocalDateTime updateTime;
}
