package org.example.grainoilsystem.util;

import io.jsonwebtoken.Claims;
import org.example.grainoilsystem.entity.Result;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        // 放行登录和注册接口
        if (uri.equals("/api/user/login") || uri.equals("/api/admin/user/login") || uri.equals("/api/user/register")) {
            return true;
        }
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            InterceptorResponseUtil.writeJson(response, Result.error("未登录"));
            return false;
        }
        String token = auth.substring(7);
        try {
            Claims claims = JwtUtil.parseToken(token);
            Integer userId = (Integer) claims.get("userId", Integer.class);
            String role = (String) claims.get("role", String.class);
            request.setAttribute("userId", userId);
            request.setAttribute("role", role);
            // 管理端权限校验
            if (uri.startsWith("/api/admin") && !"admin".equals(role)) {
                InterceptorResponseUtil.writeJson(response, Result.error("无权限"));
                return false;
            }
            return true;
        } catch (Exception e) {
            InterceptorResponseUtil.writeJson(response, Result.error("未登录"));
            return false;
        }
    }
} 