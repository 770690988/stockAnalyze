package com.biubiu.stock.stockanalyze.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("selected_stock")
public class SelectedStock {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /** 股票编码（唯一标识） */
    private String code;

    /** 股票名称 */
    private String name;

    /** 类型 1=板块 2=概念 3=地域 4=个股 */
    private Integer type;

    /** 当前价格 */
    private BigDecimal currentPrice;

    /** 持股数量 */
    private Integer takeNum;

    /** 成本价 */
    private BigDecimal costPrice;

    /** 更新时间 */
    private LocalDateTime upgradeTime;
}