package org.example.grainoilsystem.service.impl;

import org.example.grainoilsystem.entity.User;
import org.example.grainoilsystem.mapper.UserMapper;
import org.example.grainoilsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(String username, String password) {
        return userMapper.selectByUsernameAndPassword(username, password);
    }

    @Override
    public int register(User user) {
        if (userMapper.selectByUsername(user.getUsername()) != null) {
            return 1; // 用户名已存在
        }
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            return 2; // 密码过短
        }
        return userMapper.insertUser(user) > 0 ? 0 : 3; // 0成功，3其它失败
    }

    @Override
    public boolean updateUser(User user) {
        return userMapper.updateUser(user) > 0;
    }

    @Override
    public boolean deleteUserById(Integer id) {
        return userMapper.deleteUserById(id) > 0;
    }

    @Override
    public Map<String, Object> getUserStats() {
        Map<String, Object> stats = new HashMap<>();
        Map<String, Object> params = new HashMap<>();
        
        // 获取总用户数
        int total = userMapper.countUserList(params);
        stats.put("total", total);
        
        // 获取普通用户数量
        params.put("role", "user");
        int userCount = userMapper.countUserList(params);
        stats.put("userCount", userCount);
        
        // 获取管理员数量
        params.put("role", "admin");
        int adminCount = userMapper.countUserList(params);
        stats.put("adminCount", adminCount);
        
        return stats;
    }
} 