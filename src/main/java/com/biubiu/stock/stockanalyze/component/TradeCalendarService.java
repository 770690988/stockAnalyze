package com.biubiu.stock.stockanalyze.component;

import com.biubiu.stock.stockanalyze.mapper.TradeCalendarMapper;
import com.biubiu.stock.stockanalyze.model.TradeCalendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;

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
            return calendar.getIsTrading() == 1;
        }
        // 数据库没有记录，降级判断：周一到周五视为交易日
        DayOfWeek dow = today.getDayOfWeek();
        return dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY;
    }
}
