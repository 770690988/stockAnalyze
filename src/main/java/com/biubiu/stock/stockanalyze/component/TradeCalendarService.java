package com.biubiu.stock.stockanalyze.component;

import com.biubiu.stock.stockanalyze.mapper.TradeCalendarMapper;
import com.biubiu.stock.stockanalyze.model.TradeCalendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @Author biubiu
 * @Description Add TradeCalendar
 * @Date 2026/4/4 12:38
 */

@Component
public class TradeCalendarService {

    @Autowired
    private TradeCalendarMapper tradeCalendarMapper;

    /**
     * 判断今天是否是交易日
     */
    public boolean isTradingDay() {
        LocalDate today = LocalDate.now();
        // 先看数据库有没有记录
        TradeCalendar calendar = tradeCalendarMapper.selectByDate(today);
        if (calendar != null) {
            return calendar.getIsTrading() != 1;
        }
        // 数据库没有记录，降级判断：周一到周五视为交易日
        DayOfWeek dow = today.getDayOfWeek();
        return dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY;
    }

    public boolean isTradingDay(LocalDate date) {
        // 先看数据库有没有记录
        TradeCalendar calendar = tradeCalendarMapper.selectByDate(date);
        if (calendar != null) {
            return calendar.getIsTrading() != 0;
        }
        // 数据库没有记录，降级判断：周一到周五视为交易日
        DayOfWeek dow = date.getDayOfWeek();
        return dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY;
    }

    public LocalDateTime getLatestWorkDay(LocalDateTime dateTime) {
        LocalTime marketOpen  = LocalTime.of(9, 15);
        LocalTime marketClose = LocalTime.of(15, 0);
        LocalTime time = dateTime.toLocalTime();
        if (time.isBefore(marketOpen)) {
            dateTime = dateTime.minusDays(1);
            dateTime = LocalDateTime.of(dateTime.toLocalDate(), marketClose);
        } else if (time.isAfter(marketClose)) {
            dateTime = LocalDateTime.of(dateTime.toLocalDate(), marketClose);
        }
        LocalDate date = dateTime.toLocalDate();
        if (isTradingDay(date)) {
            return dateTime;
        }
        LocalDate lastWorkDay = getLastWorkDay(date);

        return LocalDateTime.of(lastWorkDay, marketClose);
    }

    // 获取对应时间前几天的交易日时间
    public LocalDateTime getPeriodWorkDayBefore(LocalDateTime dateTime, Integer periodDay) {
        LocalTime marketClose = LocalTime.of(15, 0);
        LocalDateTime latestWorkDay = getLatestWorkDay(dateTime);
        LocalDate date = latestWorkDay.toLocalDate();
        for (int i = 0; i < periodDay - 1; i++) {
            date = getLastWorkDay(date);
        }

        return LocalDateTime.of(date, marketClose);
    }

    public LocalDate getLastWorkDay(LocalDate date) {
        LocalDate lastDay = date.minusDays(1);
        // 日期往前推 直到推到最近一次交易日
        while (!isTradingDay(lastDay)) {
            lastDay = lastDay.minusDays(1);
        }
        return lastDay;
    }

    public LocalDateTime getDayStart(LocalDateTime startDay) {
        LocalTime marketOpen  = LocalTime.of(9, 15);
        return LocalDateTime.of(startDay.toLocalDate(), marketOpen);
    }
}
