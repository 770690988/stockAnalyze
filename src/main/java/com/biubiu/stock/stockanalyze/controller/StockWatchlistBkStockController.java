package com.biubiu.stock.stockanalyze.controller;

import com.biubiu.stock.stockanalyze.model.StockMoneyFlow;
import com.biubiu.stock.stockanalyze.model.StockWatchlistBkStock;
import com.biubiu.stock.stockanalyze.model.request.MoneyFlowPeriodRequest;
import com.biubiu.stock.stockanalyze.service.StockWatchlistBkStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author biubiu
 * @Description 自选板块股票入口控制
 * @Date 2026/5/2 12:48
 */

@RestController
@RequestMapping("/watchlist/bk/stock")
@RequiredArgsConstructor
@Slf4j
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

    @PostMapping("/addBatch")
    public ResponseEntity<Map<String, Object>> addBatch(@RequestBody List<StockWatchlistBkStock> stocks) {
        int success = 0, fail = 0;
        List<String> failedCodes = new ArrayList<>();

        for (StockWatchlistBkStock stock : stocks) {
            try {
                ResponseEntity<Boolean> response = add(stock);
                Boolean ok = response.getBody();
                if (Boolean.TRUE.equals(ok)) {
                    success++;
                } else {
                    fail++;
                    failedCodes.add(stock.getStockCode());
                }
            } catch (Exception e) {
                fail++;
                failedCodes.add(stock.getStockCode());
                log.error("批量导入异常跳过，code={}, error={}", stock.getStockCode(), e.getMessage());
            }
        }

        log.info("批量导入完成，成功={}，失败={}, 失败codes={}", success, fail, failedCodes);

        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("fail", fail);
        result.put("failedCodes", failedCodes);

        return ResponseEntity.ok(result);
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

    @PostMapping("/deleteBatch")
    public ResponseEntity<Integer> deleteBatch(@RequestBody List<Integer> ids) {
        return ResponseEntity.ok(bkStockService.deleteBatch(ids));
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