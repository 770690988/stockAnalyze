package com.biubiu.stock.stockanalyze.controller;

import com.biubiu.stock.stockanalyze.component.StockScheduleTask;
import com.biubiu.stock.stockanalyze.component.TradeCalendarService;
import com.biubiu.stock.stockanalyze.service.RootService;
import com.biubiu.stock.stockanalyze.service.WxNotifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class RootController {
    @Autowired
    private RootService rootService;  // 注入接口

    @Autowired
    private TradeCalendarService tradeCalendarService;

    @Autowired
    private StockScheduleTask stockScheduleTask; // 你的定时任务类

    @Autowired
    private WxNotifyService wxNotifyService;

    @RequestMapping("/testInfo")
    private void testInfo() {
        try {
            rootService.getBKInfo();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping("/changeSelectedStock")
    private void changeSelectedStock() {
        try {
            rootService.changeSelectedStock();
        } catch (Exception e) {
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

    @RequestMapping("/getWxSelectedAnalyzeMessage")
    private void getWxSelectedAnalyzeMessage() {
        try {
            rootService.getWxSelectedAnalyzeMessage();
        } catch (Exception e) {
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

    //保存数据库数据
    @RequestMapping("/saveDatabaseSql")
    private void saveDatabaseSql() {
        rootService.saveDatabaseSql();
    }

    @RequestMapping("/triggerMorning")
    public String triggerMorning() throws Exception {
        stockScheduleTask.freshStockDataMorning();
        wxNotifyService.sendInformation("triggerMorning", "triggerMorning complete");
        return "早盘任务已触发，查看日志";
    }
}
