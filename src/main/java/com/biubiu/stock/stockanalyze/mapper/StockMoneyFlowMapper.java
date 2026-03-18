package com.biubiu.stock.stockanalyze.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.biubiu.stock.stockanalyze.model.StockBk;
import com.biubiu.stock.stockanalyze.model.StockMoneyFlow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface StockMoneyFlowMapper extends BaseMapper<StockMoneyFlow> {

    void insertOrUpdate(@Param("list") List<StockMoneyFlow> stockMoneyFlow);

    LocalDateTime getLatesdTradeDate();

    List<StockMoneyFlow> getTradeTimeBetween(@Param("formerTime") LocalDateTime formerTime, @Param("latterTime") LocalDateTime latterTime);
}
