package com.biubiu.stock.stockanalyze.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("stock_bk")
public class StockBk {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String code;

    private String name;

    @TableField("type")
    private Integer type;

    private BigDecimal hotNum;

    private LocalDateTime createDate;

    private String thsCode;

    private String thsName;
}