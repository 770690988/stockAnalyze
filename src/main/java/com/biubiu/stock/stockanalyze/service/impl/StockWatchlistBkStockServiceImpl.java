package com.biubiu.stock.stockanalyze.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.biubiu.stock.stockanalyze.component.TradeCalendarService;
import com.biubiu.stock.stockanalyze.mapper.StockMoneyFlowMapper;
import com.biubiu.stock.stockanalyze.mapper.StockWatchlistBkStockMapper;
import com.biubiu.stock.stockanalyze.model.StockMoneyFlow;
import com.biubiu.stock.stockanalyze.model.StockWatchlistBkStock;
import com.biubiu.stock.stockanalyze.model.request.MoneyFlowPeriodRequest;
import com.biubiu.stock.stockanalyze.service.StockWatchlistBkStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author biubiu
 * @Description 自选板块股票服务实现
 * @Date 2026/5/2 12:44
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class StockWatchlistBkStockServiceImpl implements StockWatchlistBkStockService {

    private final StockWatchlistBkStockMapper watchBkStockMapper;
    private final StockMoneyFlowMapper stockMoneyFlowMapper;
    private final TradeCalendarService tradeCalendarService;

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

    @Override
    public List<StockMoneyFlow> getMoneyFlowPeriod(MoneyFlowPeriodRequest request) {
        // 先校验参数
        if (request.getId() == null) {
            log.error("getMoneyFlowPeriod request id is null");
            return new ArrayList<>();
        }
        if (request.getPeriodDay() == null) {
            request.setPeriodDay(1);
        }

        LocalDateTime endDay = tradeCalendarService.getLatestWorkDay(LocalDateTime.now());
        LocalDateTime startDay = tradeCalendarService.getPeriodWorkDayBefore(endDay, request.getPeriodDay());
        List<String> stockCodeList = watchBkStockMapper.getStockListByBkId(request.getId());
        if (CollectionUtils.isEmpty(stockCodeList)){
            return new ArrayList<>();
        }
        return stockMoneyFlowMapper.getFinalTradeTimeBetween(stockCodeList, startDay, endDay);
    }
}
