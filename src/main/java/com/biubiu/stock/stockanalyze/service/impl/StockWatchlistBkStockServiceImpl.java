package com.biubiu.stock.stockanalyze.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.biubiu.stock.stockanalyze.mapper.StockMoneyFlowMapper;
import com.biubiu.stock.stockanalyze.mapper.StockWatchlistBkStockMapper;
import com.biubiu.stock.stockanalyze.model.StockMoneyFlow;
import com.biubiu.stock.stockanalyze.model.StockWatchlistBkStock;
import com.biubiu.stock.stockanalyze.service.StockWatchlistBkStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author biubiu
 * @Description 自选板块股票服务实现
 * @Date 2026/5/2 12:44
 */
@Service
@RequiredArgsConstructor
public class StockWatchlistBkStockServiceImpl implements StockWatchlistBkStockService {

    private final StockWatchlistBkStockMapper watchBkStockMapper;
    private final StockMoneyFlowMapper stockMoneyFlowMapper;

    @Override
    public List<StockWatchlistBkStock> listByBkId(Integer bkId) {
        return watchBkStockMapper.selectList(
                new LambdaQueryWrapper<StockWatchlistBkStock>()
                        .eq(StockWatchlistBkStock::getBkId, bkId)
                        .orderByAsc(StockWatchlistBkStock::getSort)
        );
    }

    @Override
    public boolean add(StockWatchlistBkStock stock) {
        LocalDateTime latestTradeDate = stockMoneyFlowMapper.getLatestTradeDate();
        StockMoneyFlow stockMoneyFlow = stockMoneyFlowMapper.getByStockCodeAndTradeDate(
                stock.getStockCode(),
                latestTradeDate);
        stock.setStockName(stockMoneyFlow.getStockName());
        stock.setCreateTime(LocalDateTime.now());
        stock.setUpdateTime(LocalDateTime.now());
        try {
            watchBkStockMapper.insert(stock);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public void update(StockWatchlistBkStock stock) {
        LocalDateTime latestTradeDate = stockMoneyFlowMapper.getLatestTradeDate();
        StockMoneyFlow stockMoneyFlow = stockMoneyFlowMapper.getByStockCodeAndTradeDate(
                stock.getStockCode(),
                latestTradeDate);
        stock.setStockName(stockMoneyFlow.getStockName());
        stock.setUpdateTime(LocalDateTime.now());
        watchBkStockMapper.updateById(stock);
    }

    @Override
    public void deleteById(Integer id) {
        watchBkStockMapper.deleteById(id);
    }

    @Override
    public List<StockMoneyFlow> getLatestMoneyFlowByBkId(Integer bkId) {
        return watchBkStockMapper.getLatestMoneyFlowByWatchBkId(bkId);
    }
}
