package com.biubiu.stock.stockanalyze.controller;

import com.biubiu.stock.stockanalyze.model.StockWatchlistBk;
import com.biubiu.stock.stockanalyze.service.StockWatchlistBkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author biubiu
 * @Description 自选板块入口控制
 * @Date 2026/5/2 12:48
 */
@RestController
@RequestMapping("/watchlist/bk")
@RequiredArgsConstructor
public class StockWatchlistBkController {

    private final StockWatchlistBkService bkService;

    // 查询所有板块
    @GetMapping("/list")
    public ResponseEntity<List<StockWatchlistBk>> list() {
        return ResponseEntity.ok(bkService.listAll());
    }

    // 新增板块
    @PostMapping("/add")
    public ResponseEntity<Void> add(@RequestBody StockWatchlistBk bk) {
        bkService.add(bk);
        return ResponseEntity.ok().build();
    }

    // 修改板块
    @PostMapping("/update")
    public ResponseEntity<Void> update(@RequestBody StockWatchlistBk bk) {
        bkService.update(bk);
        return ResponseEntity.ok().build();
    }

    // 删除板块
    @PostMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        bkService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}