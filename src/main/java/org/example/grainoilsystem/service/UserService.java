package org.example.grainoilsystem.service;

import org.example.grainoilsystem.entity.User;
import java.util.Map;

public interface UserService {
    User login(String username, String password);
    int register(User user);
    boolean updateUser(User user);
    boolean deleteUserById(Integer id);
    
    // 统计方法
    Map<String, Object> getUserStats();
} 