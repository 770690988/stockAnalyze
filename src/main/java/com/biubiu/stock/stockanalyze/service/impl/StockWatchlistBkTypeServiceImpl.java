package com.biubiu.stock.stockanalyze.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.biubiu.stock.stockanalyze.mapper.StockWatchlistBkTypeMapper;
import com.biubiu.stock.stockanalyze.model.StockWatchlistBkType;
import com.biubiu.stock.stockanalyze.service.StockWatchlistBkTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockWatchlistBkTypeServiceImpl implements StockWatchlistBkTypeService {

    private final StockWatchlistBkTypeMapper bkTypeMapper;

    @Override
    public List<StockWatchlistBkType> listAll() {
        return bkTypeMapper.selectList(
            new LambdaQueryWrapper<StockWatchlistBkType>()
                .orderByAsc(StockWatchlistBkType::getSort)
        );
    }

    @Override
    public void add(StockWatchlistBkType bkType) {
        bkType.setCreateTime(LocalDateTime.now());
        bkType.setUpdateTime(LocalDateTime.now());
        bkTypeMapper.insert(bkType);
    }

    @Override
    public void update(StockWatchlistBkType bkType) {
        bkType.setUpdateTime(LocalDateTime.now());
        bkTypeMapper.updateById(bkType);
    }

    @Override
    public void deleteById(Integer id) {
        bkTypeMapper.deleteById(id);
    }
}
