package com.biubiu.stock.stockanalyze.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.biubiu.stock.stockanalyze.model.StockWatchlistBkType;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StockWatchlistBkTypeMapper extends BaseMapper<StockWatchlistBkType> {

    Integer getMaxTypeValue();
}
