package com.biubiu.stock.stockanalyze.component;

import com.biubiu.stock.stockanalyze.enums.EnvironmentEnum;
import com.biubiu.stock.stockanalyze.service.RootService;
import com.biubiu.stock.stockanalyze.service.WxNotifyService;
import com.biubiu.stock.stockanalyze.utils.WxPostUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class StockScheduleTask {
    private final RootService rootService;

    @Autowired
    private TradeCalendarService tradeCalendarService;

    @Autowired
    private WxNotifyService wxNotifyService;

    @Value("${environment.product}")
    private String environment;

    //发送手机消息
    private void sendWxMessage() throws InterruptedException {
        if (environment.equals(EnvironmentEnum.PRODUCT.environment)) {
            rootService.getWxAnalyzeMessage();
            Thread.sleep(2000);
            rootService.getWxSelectedAnalyzeMessage();
        } else {
            log.info("current environment is not product");
        }
    }

    // 工作日 09:45 - 11:30 每30分钟执行
    @Scheduled(cron = "0 15/30 9-11 ? * MON-FRI")
    public void freshStockDataMorning() throws Exception {
        //节假日跳过
        if (!tradeCalendarService.isTradingDay()) {
            log.info("current day is not trading day");
            return;
        }
        LocalTime now = LocalTime.now();
        if (now.isBefore(LocalTime.of(9, 45)) || now.isAfter(LocalTime.of(11, 30))) {
            return;
        }
        log.info("早盘统计开始...");
        try {
            rootService.freshStockMoneyFlowDataAll();
        } catch (Exception e) {
            wxNotifyService.sendInformation("刷新股票数据失败", e.getMessage());
        }
        log.info("早盘统计完成");
        sendWxMessage();
    }

    // 工作日 13:00 - 15:00 每30分钟执行
    @Scheduled(cron = "0 0/30 13-15 ? * MON-FRI")
    public void freshStockDataAfternoon() throws Exception {
        //节假日跳过
        if (!tradeCalendarService.isTradingDay()) {
            log.info("current day is not trading day");
            return;
        }
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
        sendWxMessage();
    }


    @Scheduled(cron = "0 30 15 ? * MON-FRI")
    public void freshStockDataEvening() throws Exception {
        //节假日跳过
        if (!tradeCalendarService.isTradingDay()) {
            log.info("current day is not trading day");
            return;
        }
        log.info("晚盘统计开始...");
        try {
            rootService.freshStockPriceDataAll();
            WxPostUtils wxPostUtils = new WxPostUtils();
            wxPostUtils.postMessage("刷新数据结束", "当日股票数据刷新完成！");
            Thread.sleep(2000);
        } catch (Exception e) {
            WxPostUtils wxPostUtils = new WxPostUtils();
            wxPostUtils.postMessage("刷新晚盘数据失败", e.getMessage());
        }
        log.info("晚盘统计完成");
        sendWxMessage();
    }


}
