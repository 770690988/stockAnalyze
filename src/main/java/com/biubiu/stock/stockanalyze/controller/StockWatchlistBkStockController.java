package com.biubiu.stock.stockanalyze.controller;

import com.biubiu.stock.stockanalyze.model.StockMoneyFlow;
import com.biubiu.stock.stockanalyze.model.StockWatchlistBkStock;
import com.biubiu.stock.stockanalyze.model.request.MoneyFlowPeriodRequest;
import com.biubiu.stock.stockanalyze.service.StockWatchlistBkStockService;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author biubiu
 * @Description 自选板块股票入口控制
 * @Date 2026/5/2 12:48
 */

@RestController
@RequestMapping("/watchlist/bk/stock")
@RequiredArgsConstructor
public class StockWatchlistBkStockController {

    private final StockWatchlistBkStockService bkStockService;

    // 查询板块下的股票列表
    @GetMapping("/list/{bkId}")
    public ResponseEntity<List<StockWatchlistBkStock>> list(@PathVariable Integer bkId) {
        return ResponseEntity.ok(bkStockService.listByBkId(bkId));
    }

    // 新增股票到板块
    @PostMapping("/add")
    public ResponseEntity<Boolean> add(@RequestBody StockWatchlistBkStock stock) {
        return ResponseEntity.ok(bkStockService.add(stock));
    }

    // 修改板块股票信息
    @PostMapping("/update")
    public ResponseEntity<Void> update(@RequestBody StockWatchlistBkStock stock) {
        bkStockService.update(stock);
        return ResponseEntity.ok().build();
    }

    // 移除板块股票
    @PostMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        bkStockService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // 查询板块下所有股票最新资金流向
    @GetMapping("/moneyFlow/{bkId}")
    public ResponseEntity<List<StockMoneyFlow>> getLatestMoneyFlow(@PathVariable Integer bkId) {
        return ResponseEntity.ok(bkStockService.getLatestMoneyFlowByBkId(bkId));
    }

    // 获取板块下所有资金对应时间段内的资金量数据
    @PostMapping("/period")
    public ResponseEntity<List<StockMoneyFlow>> getMoneyFlowPeriod(@RequestBody MoneyFlowPeriodRequest request) {
        return ResponseEntity.ok(bkStockService.getMoneyFlowPeriod(request));
    }
}