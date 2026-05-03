package com.biubiu.stock.stockanalyze.model.request;

import lombok.Data;

/**
 * @Author biubiu
 * @Description 获取资金量趋势的请求体
 * @Date 2026/5/3 16:30
 */
@Data
public class MoneyFlowPeriodRequest {
    private Integer id;

    // 区间时长
    private Integer periodDay;
}
