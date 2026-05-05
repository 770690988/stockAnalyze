package com.biubiu.stock.stockanalyze.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author biubiu
 * @Description 自选板块类型model
 * @Date 2026/5/2 12:31
 */

@Data
@TableName("stock_watchlist_bk_type")
public class StockWatchlistBkType {

    @TableId(type = IdType.AUTO)
    private Integer id;

    // 类型名称
    @TableField("type_label")
    private String typeLabel;

    // 类型值
    @TableField("type_value")
    private Integer typeValue;

    // 用户Id
    @TableField("user_id")
    private Long userId;

    // 排序
    @TableField("sort")
    private Integer sort;

    // 备注
    @TableField("remark")
    private String remark;

    // 创建时间
    @TableField("create_time")
    private LocalDateTime createTime;

    // 更新时间
    @TableField("update_time")
    private LocalDateTime updateTime;
}
