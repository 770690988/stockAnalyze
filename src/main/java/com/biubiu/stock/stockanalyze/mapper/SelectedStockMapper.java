package com.biubiu.stock.stockanalyze.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.biubiu.stock.stockanalyze.model.SelectedStock;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SelectedStockMapper extends BaseMapper<SelectedStock> {
    List<SelectedStock> selectAll();
}
