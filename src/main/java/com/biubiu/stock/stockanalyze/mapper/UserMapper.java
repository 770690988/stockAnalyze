package com.biubiu.stock.stockanalyze.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.biubiu.stock.stockanalyze.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

/**
 * @Author biubiu
 * @Description 用户Mapper
 * @Date 2026/5/3 14:47
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 基础的 CRUD MyBatis Plus 已经自动提供
    // 按用户名查询需要自己加一个
    default Optional<User> findByUsername(String username) {
        return Optional.ofNullable(
                selectOne(new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, username))
        );
    }
}