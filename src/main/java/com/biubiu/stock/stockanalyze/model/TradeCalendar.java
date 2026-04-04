package com.biubiu.stock.stockanalyze.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

/**
 * @Author biubiu
 * @Description trade_calendar model
 * @Date 2026/4/4 12:43
 */

@Data
@TableName("trade_calendar")
public class TradeCalendar {

    @TableId(type = IdType.INPUT)
    private LocalDate tradeDate;

    private Integer isTrading;

    private String remark;
}