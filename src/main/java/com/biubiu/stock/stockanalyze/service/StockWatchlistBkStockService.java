package com.biubiu.stock.stockanalyze.service;

import com.biubiu.stock.stockanalyze.model.StockMoneyFlow;
import com.biubiu.stock.stockanalyze.model.StockWatchlistBkStock;

import java.util.List;

/**
 * @Author biubiu
 * @Description 自选板块股票服务
 * @Date 2026/5/2 12:43
 */
public interface StockWatchlistBkStockService {

    // 查询板块下的股票列表
    List<StockWatchlistBkStock> listByBkId(Integer bkId);

    // 新增股票到板块
    boolean add(StockWatchlistBkStock stock);

    // 修改板块股票信息（理由/备注/排序）
    void update(StockWatchlistBkStock stock);

    // 从板块移除股票
    void deleteById(Integer id);

    // 查询板块下所有股票的最新资金流向
    List<StockMoneyFlow> getLatestMoneyFlowByBkId(Integer bkId);
}
