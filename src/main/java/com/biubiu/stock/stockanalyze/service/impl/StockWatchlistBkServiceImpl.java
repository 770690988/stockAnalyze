package com.biubiu.stock.stockanalyze.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.biubiu.stock.stockanalyze.mapper.StockMoneyFlowMapper;
import com.biubiu.stock.stockanalyze.mapper.StockWatchlistBkMapper;
import com.biubiu.stock.stockanalyze.mapper.StockWatchlistBkStockMapper;
import com.biubiu.stock.stockanalyze.model.StockMoneyFlow;
import com.biubiu.stock.stockanalyze.model.StockWatchlistBk;
import com.biubiu.stock.stockanalyze.model.StockWatchlistBkStock;
import com.biubiu.stock.stockanalyze.service.StockWatchlistBkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author biubiu
 * @Description 自选板块信息服务实现
 * @Date 2026/5/2 12:42
 */

@Service
@RequiredArgsConstructor
public class StockWatchlistBkServiceImpl implements StockWatchlistBkService {

    private final StockWatchlistBkMapper stockBkMapper;
    private final StockWatchlistBkStockMapper stockBkStockMapper;
    private final StockMoneyFlowMapper stockMoneyFlowMapper;

    @Override
    public List<StockWatchlistBk> listAll() {
        return stockBkMapper.selectList(null);
    }

    @Override
    public void add(StockWatchlistBk bk) {
        bk.setCreateTime(LocalDateTime.now());
        bk.setUpdateTime(LocalDateTime.now());
        stockBkMapper.insert(bk);
    }

    @Override
    public void update(StockWatchlistBk bk) {
        bk.setUpdateTime(LocalDateTime.now());
        stockBkMapper.updateById(bk);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Integer id) {
        // 删除板块
        stockBkMapper.deleteById(id);
        // 同时删除该板块下所有关联股票
        stockBkStockMapper.delete(
                new LambdaQueryWrapper<StockWatchlistBkStock>()
                        .eq(StockWatchlistBkStock::getBkId, id)
        );
    }
}
