package org.example.grainoilsystem.mapper;

import org.example.grainoilsystem.entity.Order;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

public interface OrderMapper {
    int insertOrder(Order order);
    List<Order> selectOrdersByUserId(Integer userId);
    List<Map<String, Object>> selectOrderList(Map<String, Object> params);
    int countOrderList(Map<String, Object> params);
    Map<String, Object> selectOrderDetailById(@Param("id") Integer id);
    int deleteOrderById(@Param("id") Integer id);
    int deleteOrderByUser(@Param("orderId") Integer orderId, @Param("userId") Integer userId);
}