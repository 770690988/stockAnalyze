package com.biubiu.stock.stockanalyze.filter;

import com.biubiu.stock.stockanalyze.utils.JwtUtil;
import com.biubiu.stock.stockanalyze.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @Author biubiu
 * @Description Jwt切面
 * @Date 2026/5/3 14:58
 */
@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String header = request.getHeader("Authorization");

            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);

                if (jwtUtil.validateToken(token)) {
                    Long userId = jwtUtil.parseUserId(token);
                    UserContext.set(userId);

                    // 告诉 Spring Security 这个用户已认证
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    log.warn("JWT 校验失败，来源IP: {}, URI: {}", getClientIp(request), request.getRequestURI());
                }
            }

            filterChain.doFilter(request, response);

        } finally {
            UserContext.clear(); // 必须清理，防止线程复用导致数据串用
        }
    }


    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip)) {
            // 经过多层代理时，第一个才是真实IP，格式: "client, proxy1, proxy2"
            return ip.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
