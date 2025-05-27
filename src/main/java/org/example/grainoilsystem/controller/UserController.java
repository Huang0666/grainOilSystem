package org.example.grainoilsystem.controller;

import org.example.grainoilsystem.entity.User;
import org.example.grainoilsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.example.grainoilsystem.entity.Result;
import org.example.grainoilsystem.util.JwtUtil;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户相关接口
 * 提供用户登录和注册功能。
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 用户登录接口
     * @param user 请求体参数：
     *             username - 用户名（String，必填）
     *             password - 密码（String，必填）
     * @return 返回：
     *         登录成功/用户名或密码错误（String）
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody User user) {
        User loginUser = userService.login(user.getUsername(), user.getPassword());
        if (loginUser == null) {
            return Result.error("账号或密码错误");
        }
        if (!"user".equals(loginUser.getRole())) {
            return Result.error("无普通用户权限");
        }
        String token = JwtUtil.createToken(loginUser.getId(), loginUser.getRole());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", loginUser.getId());
        userInfo.put("username", loginUser.getUsername());
        userInfo.put("role", loginUser.getRole());
        userInfo.put("phone", loginUser.getPhone());
        data.put("user", userInfo);
        return Result.success(data);
    }

    /**
     * 用户注册接口
     * @param user 请求体参数：
     *             username - 用户名（String，必填）
     *             password - 密码（String，必填）
     *             role - 角色（String，可选，默认值为 user）
     *             phone - 手机号（String，可选）
     * @return 返回：
     *         注册成功/用户名已存在（String）
     */
    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        if (user.getRole() == null) {
            user.setRole("user");  // 设置默认角色为普通用户
        } else if (!user.getRole().equals("user") && !user.getRole().equals("admin")) {
            return Result.error("角色只能是 user 或 admin");
        }
        int status = userService.register(user);
        switch (status) {
            case 0: return Result.success("注册成功");
            case 1: return Result.error("用户名已存在");
            case 2: return Result.error("密码不能低于6位");
            default: return Result.error("注册失败");
        }
    }

    @PostMapping("/update")
    public Result<String> update(@RequestBody User user) {
        boolean success = userService.updateUser(user);
        return success ? Result.success("修改成功") : Result.error("修改失败");
    }

    @PostMapping("/delete")
    public Result<String> delete(@RequestBody User user) {
        boolean success = userService.deleteUserById(user.getId());
        return success ? Result.success("注销成功") : Result.error("注销失败");
    }
} 