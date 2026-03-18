package com.biubiu.stock.stockanalyze.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.biubiu.stock.stockanalyze.model.StockBk;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StockBkMapper extends BaseMapper<StockBk> {
}
