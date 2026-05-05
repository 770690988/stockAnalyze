package com.biubiu.stock.stockanalyze.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.biubiu.stock.stockanalyze.mapper.StockWatchlistBkTypeMapper;
import com.biubiu.stock.stockanalyze.model.StockWatchlistBkType;
import com.biubiu.stock.stockanalyze.service.StockWatchlistBkTypeService;
import com.biubiu.stock.stockanalyze.utils.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockWatchlistBkTypeServiceImpl implements StockWatchlistBkTypeService {

    private final StockWatchlistBkTypeMapper bkTypeMapper;

    @Override
    public List<StockWatchlistBkType> listAll() {
        Long userId = UserContext.get();
        return bkTypeMapper.selectList(
                new LambdaQueryWrapper<StockWatchlistBkType>()
                        .eq(StockWatchlistBkType::getUserId, userId)
                        .orderByAsc(StockWatchlistBkType::getSort)
        );
    }

    @Override
    public void add(StockWatchlistBkType bkType) {
        Long userId = UserContext.get();
        bkType.setUserId(userId);  // ✅ 绑定当前用户
        if (bkType.getTypeValue() == null) {
            bkType.setTypeValue(bkTypeMapper.getMaxTypeValue() + 1);
        }
        bkType.setCreateTime(LocalDateTime.now());
        bkType.setUpdateTime(LocalDateTime.now());
        bkTypeMapper.insert(bkType);
    }

    @Override
    public void update(StockWatchlistBkType bkType) {
        Long userId = UserContext.get();
        bkType.setUpdateTime(LocalDateTime.now());
        // ✅ 防止修改别人的数据
        bkTypeMapper.update(bkType,
                new LambdaQueryWrapper<StockWatchlistBkType>()
                        .eq(StockWatchlistBkType::getId, bkType.getId())
                        .eq(StockWatchlistBkType::getUserId, userId)
        );
    }

    @Override
    public void deleteById(Integer id) {
        Long userId = UserContext.get();
        // 防止删除别人的数据
        bkTypeMapper.delete(
                new LambdaQueryWrapper<StockWatchlistBkType>()
                        .eq(StockWatchlistBkType::getId, id)
                        .eq(StockWatchlistBkType::getUserId, userId)
        );
    }
}