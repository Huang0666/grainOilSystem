package org.example.grainoilsystem.service.impl;

import org.example.grainoilsystem.entity.Order;
import org.example.grainoilsystem.entity.ProductMerchant;
import org.example.grainoilsystem.mapper.OrderMapper;
import org.example.grainoilsystem.mapper.ProductMerchantMapper;
import org.example.grainoilsystem.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private ProductMerchantMapper productMerchantMapper;

    @Override
    public boolean buy(Integer userId, Integer productId, Integer merchantId, Integer quantity) {
        // 检查商品是否存在且上架
        ProductMerchant productMerchant = productMerchantMapper.selectByProductAndMerchant(productId, merchantId);
        if (productMerchant == null || productMerchant.getStatus() != 1) {
            return false; // 商品不存在或未上架
        }
        
        // 获取当前价格
        Double price = productMerchant.getCurrentPrice();
        if (price == null) {
            return false; // 价格未设置
        }
        
        double totalPrice = price * quantity;
        
        // 创建订单
        Order order = new Order();
        order.setUserId(userId);
        order.setProductId(productId);
        order.setMerchantId(merchantId);
        order.setQuantity(quantity);
        order.setPrice(price);
        order.setTotalPrice(totalPrice);
        order.setCreateTime(new Date());
        
        return orderMapper.insertOrder(order) > 0;
    }

    @Override
    public List<Order> getOrdersByUserId(Integer userId) {
        return orderMapper.selectOrdersByUserId(userId);
    }

    @Override
    public List<Map<String, Object>> getOrderList(Map<String, Object> params) {
        return orderMapper.selectOrderList(params);
    }

    @Override
    public int countOrderList(Map<String, Object> params) {
        return orderMapper.countOrderList(params);
    }

    @Override
    public Map<String, Object> getOrderDetailById(Integer id) {
        return orderMapper.selectOrderDetailById(id);
    }

    @Override
    public boolean deleteOrderById(Integer id) {
        return orderMapper.deleteOrderById(id) > 0;
    }

    @Override
    public boolean removeOrderByUser(Integer orderId, Integer userId) {
        return orderMapper.deleteOrderByUser(orderId, userId) > 0;
    }
} 