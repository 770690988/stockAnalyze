package com.biubiu.stock.stockanalyze.controller;

import com.biubiu.stock.stockanalyze.service.RootService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class RootController {
    @Autowired
    private RootService rootService;  // 注入接口

    @RequestMapping("/testInfo")
    private void testInfo() {
        try {
            rootService.getBKInfo();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping("/getStockInfo")
    private void getStockInfo() {
        try {
            rootService.getStockInfo();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping("/freshStockData")
    private void freshStockData() {
        try {
            rootService.freshStockData();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping("/freshStockMoneyFlowDataAll")
    private void freshStockMoneyFlowDataAll() {
        try {
            rootService.freshStockMoneyFlowDataAll();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping("/freshStockPriceDataAll")
    private void freshStockPriceDataAll() {
        try {
            rootService.freshStockPriceDataAll();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    //发送股票统计数据
    @RequestMapping("/sendWxMessage")
    private void sendWxMessage() {
        rootService.getWxAnalyzeMessage();
    }
}
