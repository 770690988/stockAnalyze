package com.biubiu.stock.stockanalyze.service;

import com.biubiu.stock.stockanalyze.model.StockWatchlistBkType;

import java.util.List;

public interface StockWatchlistBkTypeService {

    // 查询所有类型（按排序）
    List<StockWatchlistBkType> listAll();

    // 新增类型
    void add(StockWatchlistBkType bkType);

    // 修改类型
    void update(StockWatchlistBkType bkType);

    // 删除类型
    void deleteById(Integer id);
}
