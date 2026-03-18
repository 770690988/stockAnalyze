package com.biubiu.stock.stockanalyze.component;

import com.biubiu.stock.stockanalyze.service.RootService;
import com.biubiu.stock.stockanalyze.utils.WxPostUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class StockScheduleTask {
    private final RootService rootService;

//    @Scheduled(cron = "0 * * * * ?")  // 每分钟执行
//    public void testSceduledTask() throws Exception {
//        //分析数据
//        rootService.getWxAnalyzeMessage();
//    }

    // 工作日 09:45 - 11:30 每30分钟执行
    @Scheduled(cron = "0 15/30 9-11 ? * MON-FRI")
    public void freshStockDataMorning() throws Exception {
        LocalTime now = LocalTime.now();
        if (now.isBefore(LocalTime.of(9, 45)) || now.isAfter(LocalTime.of(11, 30))) {
            return;
        }
        log.info("早盘统计开始...");
        try {
            rootService.freshStockMoneyFlowDataAll();
        } catch (Exception e) {
            WxPostUtils wxPostUtils = new WxPostUtils();
            wxPostUtils.postMessage("刷新股票数据失败", e.getMessage());
        }
        log.info("早盘统计完成");
        rootService.getWxAnalyzeMessage();
    }

    // 工作日 13:00 - 15:00 每30分钟执行
    @Scheduled(cron = "0 0/30 13-15 ? * MON-FRI")
    public void freshStockDataAfternoon() throws Exception {
        LocalTime now = LocalTime.now();
        // 过滤掉15:30（只要13:00 13:30 14:00 14:30 15:00）
        if (now.isAfter(LocalTime.of(15, 10))) {
            return;
        }
        log.info("午盘统计开始...");
        try {
            rootService.freshStockMoneyFlowDataAll();
        } catch (Exception e) {
            WxPostUtils wxPostUtils = new WxPostUtils();
            wxPostUtils.postMessage("刷新股票数据失败", e.getMessage());
        }
        log.info("午盘统计完成");
        rootService.getWxAnalyzeMessage();
    }


    @Scheduled(cron = "0 30 15 ? * MON-FRI")
    public void freshStockDataEvening() throws Exception {
        log.info("晚盘统计开始...");
        try {
            rootService.freshStockPriceDataAll();
            WxPostUtils wxPostUtils = new WxPostUtils();
            wxPostUtils.postMessage("刷新数据结束", "当日股票数据刷新完成！");
        } catch (Exception e) {
            WxPostUtils wxPostUtils = new WxPostUtils();
            wxPostUtils.postMessage("刷新晚盘数据失败", e.getMessage());
        }
        log.info("晚盘统计完成");
        rootService.getWxAnalyzeMessage();
    }
}
