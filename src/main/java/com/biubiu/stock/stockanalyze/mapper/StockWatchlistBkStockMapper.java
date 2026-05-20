package com.biubiu.stock.stockanalyze.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.biubiu.stock.stockanalyze.model.StockMoneyFlow;
import com.biubiu.stock.stockanalyze.model.StockWatchlistBkStock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author biubiu
 * @Description 自选板块股票对照Mapper
 * @Date 2026/5/2 12:33
 */
@Mapper
public interface StockWatchlistBkStockMapper extends BaseMapper<StockWatchlistBkStock> {

    // 根据板块ID查询该板块下所有股票的最新资金流向
    List<StockMoneyFlow> getLatestMoneyFlowByWatchBkId(@Param("bkId") Integer bkId);

    List<String> getStockListByBkId(@Param("id") Integer id);

    StockWatchlistBkStock getByBkIdAndStockCode(@Param("bkId") Integer bkId, @Param("stockCode") String stockCode);

    Integer deleteBatch(List<Integer> ids);

    StockWatchlistBkStock getByBkIdAndStockCodeAndReason(
            @Param("bkId") Integer bkId,
            @Param("stockCode") String stockCode,
            @Param("addReason") String addReason);
}
