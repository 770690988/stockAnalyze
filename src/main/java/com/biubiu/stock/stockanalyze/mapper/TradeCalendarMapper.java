package com.biubiu.stock.stockanalyze.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.biubiu.stock.stockanalyze.model.TradeCalendar;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * @Author biubiu
 * @Description trade_calendar table mapper
 * @Date 2026/4/4 12:38
 */
@Mapper
public interface TradeCalendarMapper extends BaseMapper<TradeCalendar> {
    TradeCalendar selectByDate(@Param("date") LocalDate date);

    int batchUpsert(@Param("list") List<TradeCalendar> list);

    List<TradeCalendar> selectByDateRange(@Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);
}
