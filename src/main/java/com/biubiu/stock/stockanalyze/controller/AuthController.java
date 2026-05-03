package com.biubiu.stock.stockanalyze.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.biubiu.stock.stockanalyze.mapper.UserMapper;
import com.biubiu.stock.stockanalyze.model.User;
import com.biubiu.stock.stockanalyze.utils.JwtUtil;
import com.biubiu.stock.stockanalyze.utils.UserContext;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author biubiu
 * @Description 登录控制层
 * @Date 2026/5/3 14:42
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    /**
     * 注册
     * POST /api/auth/register
     * body: { "username": "xxx", "password": "xxx" }
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest req) {
        // 校验参数
        if (req.getUsername() == null || req.getUsername().isBlank()) {
            return fail("用户名不能为空");
        }
        if (req.getPassword() == null || req.getPassword().length() < 6) {
            return fail("密码不能少于6位");
        }

        // 检查用户名是否已存在
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, req.getUsername())
        );
        if (count > 0) {
            return fail("用户名已存在");
        }

        // 创建用户
        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        userMapper.insert(user);

        return ok("注册成功", null);
    }

    /**
     * 登录
     * POST /api/auth/login
     * body: { "username": "xxx", "password": "xxx" }
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest req) {
        // 校验参数
        if (req.getUsername() == null || req.getPassword() == null) {
            return fail("用户名或密码不能为空");
        }

        // 查用户
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, req.getUsername())
        );

        // 故意不区分"用户不存在"和"密码错误"，防止用户名枚举攻击
        if (user == null || !passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            return fail("用户名或密码错误");
        }

        // 生成 token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        return ok("登录成功", Map.of(
                "token", token,
                "userId", user.getId(),
                "username", user.getUsername()
        ));
    }

    /**
     * 获取当前登录用户信息
     * GET /api/auth/me
     * Header: Authorization: Bearer xxx
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me() {
        Long userId = UserContext.get();
        if (userId == null) {
            return fail("未登录");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            return fail("用户不存在");
        }

        return ok("success", Map.of(
                "userId", user.getId(),
                "username", user.getUsername(),
                "createdAt", user.getCreatedAt()
        ));
    }

    // ---------------------- 请求体 ----------------------

    @Data
    public static class RegisterRequest {
        private String username;
        private String password;
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }

    // ---------------------- 统一响应 ----------------------

    private ResponseEntity<Map<String, Object>> ok(String message, Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", message);
        result.put("data", data);
        return ResponseEntity.ok(result);
    }

    private ResponseEntity<Map<String, Object>> fail(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", message);
        result.put("data", null);
        return ResponseEntity.badRequest().body(result);
    }
}
