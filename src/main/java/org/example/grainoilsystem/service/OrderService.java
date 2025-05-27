package org.example.grainoilsystem.service;

import org.example.grainoilsystem.entity.Order;
import java.util.List;
import java.util.Map;

public interface OrderService {
    boolean buy(Integer userId, Integer productId, Integer merchantId, Integer quantity);
    List<Order> getOrdersByUserId(Integer userId);
    List<Map<String, Object>> getOrderList(Map<String, Object> params);
    int countOrderList(Map<String, Object> params);
    Map<String, Object> getOrderDetailById(Integer id);
    boolean deleteOrderById(Integer id);
    boolean removeOrderByUser(Integer orderId, Integer userId);
} 