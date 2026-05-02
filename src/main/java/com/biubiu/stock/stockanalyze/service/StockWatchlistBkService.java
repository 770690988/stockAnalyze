package com.biubiu.stock.stockanalyze.service;

/**
 * @Author biubiu
 * @Description 自选板块信息服务
 * @Date 2026/5/2 12:40
 */
import com.biubiu.stock.stockanalyze.model.StockWatchlistBk;

import java.util.List;

public interface StockWatchlistBkService {

    // 查询所有板块
    List<StockWatchlistBk> listAll();

    // 新增板块
    void add(StockWatchlistBk bk);

    // 修改板块
    void update(StockWatchlistBk bk);

    // 删除板块（同时删除关联股票）
    void deleteById(Integer id);
}
