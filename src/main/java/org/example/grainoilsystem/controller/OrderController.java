package org.example.grainoilsystem.controller;

import org.example.grainoilsystem.entity.Order;
import org.example.grainoilsystem.entity.Result;
import org.example.grainoilsystem.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单相关接口
 * 提供下单和订单查询功能。
 */
@RestController
@RequestMapping("/api/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 下单接口
     * @param params 请求参数：
     *               userId - 用户ID（int，必填）
     *               productId - 商品ID（int，必填）
     *               merchantId - 商家ID（int，必填）
     *               quantity - 购买数量（int，必填）
     * @return 返回字段：
     *         success - 是否下单成功（boolean）
     *         msg - 提示信息（String）
     */
    @PostMapping("/buy")
    public Result<Map<String, Object>> buy(@RequestBody Map<String, Object> params) {
        Integer userId = (Integer) params.get("userId");
        Integer productId = (Integer) params.get("productId");
        Integer merchantId = (Integer) params.get("merchantId");
        Integer quantity = (Integer) params.get("quantity");
        
        if (userId == null || productId == null || merchantId == null || quantity == null) {
            return Result.error("缺少必要参数");
        }
        
        boolean success = orderService.buy(userId, productId, merchantId, quantity);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("msg", success ? "下单成功" : "下单失败，商品不存在或未上架");
        return Result.success(result);
    }

    /**
     * 获取用户订单列表接口
     * @param params 请求参数：
     *               userId - 用户ID（int，必填）
     * @return 返回字段：
     *         List<Order> - 订单列表
     */
    @PostMapping("/list")
    public Result<List<Order>> list(@RequestBody Map<String, Object> params) {
        Integer userId = (Integer) params.get("userId");
        return Result.success(orderService.getOrdersByUserId(userId));
    }

    /**
     * 删除订单接口
     * @param params 请求参数：
     *               orderId - 订单ID（int，必填）
     *               userId - 用户ID（int，必填）
     * @return 返回字段：
     *         success - 是否删除成功（boolean）
     *         msg - 提示信息（String）
     */
    @PostMapping("/remove")
    public Result<Map<String, Object>> remove(@RequestBody Map<String, Object> params) {
        Integer orderId = (Integer) params.get("orderId");
        Integer userId = (Integer) params.get("userId");
        
        if (orderId == null || userId == null) {
            return Result.error("缺少必要参数");
        }
        
        boolean success = orderService.removeOrderByUser(orderId, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("msg", success ? "删除成功" : "删除失败，订单不存在或无权限删除");
        return Result.success(result);
    }
} 