package com.biubiu.stock.stockanalyze.controller;

import com.biubiu.stock.stockanalyze.model.StockWatchlistBkType;
import com.biubiu.stock.stockanalyze.service.StockWatchlistBkTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/watchlist/bk/type")
@RequiredArgsConstructor
@Slf4j
public class StockWatchlistBkTypeController {

    private final StockWatchlistBkTypeService bkTypeService;

    // 查询所有板块类型
    @GetMapping("/list")
    public ResponseEntity<List<StockWatchlistBkType>> list() {
        log.info("enter /watchlist/bk/type/list");
        return ResponseEntity.ok(bkTypeService.listAll());
    }

    // 新增板块类型
    @PostMapping("/add")
    public ResponseEntity<Void> add(@RequestBody StockWatchlistBkType bkType) {
        bkTypeService.add(bkType);
        return ResponseEntity.ok().build();
    }

    // 修改板块类型
    @PostMapping("/update")
    public ResponseEntity<Void> update(@RequestBody StockWatchlistBkType bkType) {
        bkTypeService.update(bkType);
        return ResponseEntity.ok().build();
    }

    // 删除板块类型
    @PostMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        bkTypeService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
