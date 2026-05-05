package com.biubiu.stock.stockanalyze.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.biubiu.stock.stockanalyze.mapper.StockMoneyFlowMapper;
import com.biubiu.stock.stockanalyze.mapper.StockWatchlistBkMapper;
import com.biubiu.stock.stockanalyze.mapper.StockWatchlistBkStockMapper;
import com.biubiu.stock.stockanalyze.model.StockMoneyFlow;
import com.biubiu.stock.stockanalyze.model.StockWatchlistBk;
import com.biubiu.stock.stockanalyze.model.StockWatchlistBkStock;
import com.biubiu.stock.stockanalyze.service.StockWatchlistBkService;
import com.biubiu.stock.stockanalyze.utils.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class StockWatchlistBkServiceImpl implements StockWatchlistBkService {

    private final StockWatchlistBkMapper stockBkMapper;
    private final StockWatchlistBkStockMapper stockBkStockMapper;
    private final StockMoneyFlowMapper stockMoneyFlowMapper;

    @Override
    public List<StockWatchlistBk> listAll() {
        Long userId = UserContext.get();
        return stockBkMapper.selectList(
                new LambdaQueryWrapper<StockWatchlistBk>()
                        .eq(StockWatchlistBk::getUserId, userId)
        );
    }

    @Override
    public void add(StockWatchlistBk bk) {
        Long userId = UserContext.get();
        bk.setUserId(userId);  // ✅ 绑定当前用户
        bk.setCreateTime(LocalDateTime.now());
        bk.setUpdateTime(LocalDateTime.now());
        stockBkMapper.insert(bk);
    }

    @Override
    public void update(StockWatchlistBk bk) {
        Long userId = UserContext.get();
        // ✅ 防止用户修改别人的数据
        stockBkMapper.update(bk,
                new LambdaQueryWrapper<StockWatchlistBk>()
                        .eq(StockWatchlistBk::getId, bk.getId())
                        .eq(StockWatchlistBk::getUserId, userId)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Integer id) {
        Long userId = UserContext.get();
        // ✅ 防止用户删除别人的板块
        stockBkMapper.delete(
                new LambdaQueryWrapper<StockWatchlistBk>()
                        .eq(StockWatchlistBk::getId, id)
                        .eq(StockWatchlistBk::getUserId, userId)
        );
        // 删除该板块下所有关联股票
        stockBkStockMapper.delete(
                new LambdaQueryWrapper<StockWatchlistBkStock>()
                        .eq(StockWatchlistBkStock::getBkId, id)
        );
    }
}
