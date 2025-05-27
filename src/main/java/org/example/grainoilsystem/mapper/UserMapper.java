package org.example.grainoilsystem.mapper;

import org.example.grainoilsystem.entity.User;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

public interface UserMapper {
    User selectByUsernameAndPassword(@Param("username") String username, @Param("password") String password);
    User selectByUsername(@Param("username") String username);
    int insertUser(User user);
    int updateUser(User user);
    int deleteUserById(@Param("id") Integer id);
    List<User> selectUserList(Map<String, Object> params);
    int countUserList(Map<String, Object> params);
    User selectUserById(@Param("id") Integer id);
    int updateUserPassword(@Param("id") Integer id, @Param("password") String password);
} 