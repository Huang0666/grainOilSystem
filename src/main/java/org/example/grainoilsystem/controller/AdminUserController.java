package org.example.grainoilsystem.controller;

import org.example.grainoilsystem.entity.User;
import org.example.grainoilsystem.service.UserService;
import org.example.grainoilsystem.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.example.grainoilsystem.entity.Result;
import org.example.grainoilsystem.util.JwtUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/user")
public class AdminUserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;

    /**
     * 用户列表接口
     * @param params 请求参数：
     *               page - 页码（int，可选，默认1）
     *               size - 每页数量（int，可选，默认10）
     *               username - 用户名（String，可选）
     *               role - 角色（String，可选）
     * @return 返回字段：
     *         list - 用户列表（List<User>）
     *         total - 用户总数（int）
     */
    @PostMapping("/list")
    public Result<Map<String, Object>> list(@RequestBody Map<String, Object> params) {
        int page = params.get("page") == null ? 1 : (int) params.get("page");
        int size = params.get("size") == null ? 10 : (int) params.get("size");
        String username = params.get("username") == null ? null : (String) params.get("username");
        String role = params.get("role") == null ? null : (String) params.get("role");
        int offset = (page - 1) * size;
        Map<String, Object> query = new HashMap<>();
        query.put("offset", offset);
        query.put("size", size);
        query.put("username", username);
        query.put("role", role);
        List<User> list = userMapper.selectUserList(query);
        int total = userMapper.countUserList(query);
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        return Result.success(result);
    }

    /**
     * 新增用户接口
     * @param user 请求体参数：用户对象，包含username、password、phone、role等
     * @return 返回：添加成功/添加失败/用户名已存在/密码不能低于6位（String）
     */
    @PostMapping("/add")
    public Result<String> add(@RequestBody User user) {
        if (user.getRole() == null) {
            return Result.error("角色不能为空");
        }
        if (!user.getRole().equals("user") && !user.getRole().equals("admin")) {
            return Result.error("角色只能是 user 或 admin");
        }
        int status = userService.register(user);
        switch (status) {
            case 0: return Result.success("添加成功");
            case 1: return Result.error("用户名已存在");
            case 2: return Result.error("密码不能低于6位");
            default: return Result.error("添加失败");
        }
    }

    /**
     * 修改用户接口
     * @param user 请求体参数：用户对象，包含id、username、password、phone、role等
     * @return 返回：修改成功/修改失败（String）
     */
    @PostMapping("/update")
    public Result<String> update(@RequestBody User user) {
        if (user.getId() == null) {
            return Result.error("用户ID不能为空");
        }
        if (user.getRole() != null && !user.getRole().equals("user") && !user.getRole().equals("admin")) {
            return Result.error("角色只能是 user 或 admin");
        }
        boolean success = userService.updateUser(user);
        return success ? Result.success("修改成功") : Result.error("修改失败");
    }

    /**
     * 删除用户接口
     * @param params 请求参数：
     *               id - 用户ID（int，必填）
     * @return 返回：删除成功/删除失败（String）
     */
    @PostMapping("/delete")
    public Result<String> delete(@RequestBody Map<String, Object> params) {
        Integer id = (Integer) params.get("id");
        boolean success = userService.deleteUserById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 用户详情接口
     * @param params 请求参数：
     *               id - 用户ID（int，必填）
     * @return 返回：用户对象（User）
     */
    @PostMapping("/detail")
    public Result<User> detail(@RequestBody Map<String, Object> params) {
        Integer id = (Integer) params.get("id");
        return Result.success(userMapper.selectUserById(id));
    }

    /**
     * 重置用户密码接口
     * @param params 请求参数：
     *               id - 用户ID（int，必填）
     *               password - 新密码（String，必填，长度≥6）
     * @return 返回：重置成功/重置失败/密码不能低于6位（String）
     */
    @PostMapping("/reset-password")
    public Result<String> resetPassword(@RequestBody Map<String, Object> params) {
        Object idObj = params.get("id");
        Integer id = null;
        if (idObj instanceof Integer) {
            id = (Integer) idObj;
        } else if (idObj instanceof String) {
            try {
                id = Integer.parseInt((String) idObj);
            } catch (NumberFormatException e) {
                return Result.error("用户ID格式错误");
            }
        }
        
        String password = (String) params.get("password");
        
        if (id == null) {
            return Result.error("用户ID不能为空");
        }
        if (password == null || password.length() < 6) {
            return Result.error("密码不能低于6位");
        }
        
        boolean success = userMapper.updateUserPassword(id, password) > 0;
        return success ? Result.success("重置成功") : Result.error("重置失败");
    }

    /**
     * 管理员登录接口
     * @param user 请求体参数：
     *             username - 用户名（String，必填）
     *             password - 密码（String，必填）
     * @return 返回：登录成功/账号或密码错误/无管理员权限（String）
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> adminLogin(@RequestBody User user) {
        User loginUser = userService.login(user.getUsername(), user.getPassword());
        if (loginUser == null) {
            return Result.error("账号或密码错误");
        }
        if (!"admin".equals(loginUser.getRole())) {
            return Result.error("无管理员权限");
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
     * 获取用户统计数据
     * @return 返回：
     *         total - 用户总数
     *         userCount - 普通用户数量
     *         adminCount - 管理员用户数量
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = userService.getUserStats();
        return Result.success(stats);
    }
} 