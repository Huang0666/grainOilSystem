package org.example.grainoilsystem.controller;

import org.example.grainoilsystem.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.example.grainoilsystem.entity.Result;

@RestController
@RequestMapping("/api/admin/order")
public class AdminOrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 订单列表接口
     * @param params 请求参数：
     *               page - 页码（int，可选，默认1）
     *               size - 每页数量（int，可选，默认10）
     * @return 返回字段：
     *         list - 订单列表（List<Map<String, Object>>）
     *         total - 订单总数（int）
     */
    @PostMapping("/list")
    public Result<Map<String, Object>> list(@RequestBody Map<String, Object> params) {
        int page = params.get("page") == null ? 1 : (int) params.get("page");
        int size = params.get("size") == null ? 10 : (int) params.get("size");
        int offset = (page - 1) * size;
        params.put("offset", offset);
        params.put("size", size);
        List<Map<String, Object>> list = orderService.getOrderList(params);
        int total = orderService.countOrderList(params);
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        return Result.success(result);
    }

    /**
     * 订单详情接口
     * @param params 请求参数：
     *               id - 订单ID（int，必填）
     * @return 返回：订单详情（Map<String, Object>）
     */
    @PostMapping("/detail")
    public Result<Map<String, Object>> detail(@RequestBody Map<String, Object> params) {
        Integer id = (Integer) params.get("id");
        return Result.success(orderService.getOrderDetailById(id));
    }

    /**
     * 删除订单接口
     * @param params 请求参数：
     *               id - 订单ID（int，必填）
     * @return 返回：删除成功/删除失败（String）
     */
    @PostMapping("/delete")
    public Result<String> delete(@RequestBody Map<String, Object> params) {
        Integer id = (Integer) params.get("id");
        boolean success = orderService.deleteOrderById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }


    /**
     * 添加订单接口
     * @param params 请求参数：
     *               userId - 用户ID（int，必填）
     *               productId - 商品ID（int，必填）
     *               merchantId - 商户ID（int，必填）
     *               quantity - 数量（int，必填）   
     * @return 返回：添加成功/添加失败（String）
     */
    @PostMapping("/add")
    public Result<String> add(@RequestBody Map<String, Object> params) {
        Integer userId = (Integer) params.get("userId");
        Integer productId = (Integer) params.get("productId");
        Integer merchantId = (Integer) params.get("merchantId");
        Integer quantity = (Integer) params.get("quantity");
        
        if (userId == null || productId == null || merchantId == null || quantity == null) {
            return Result.error("缺少必要参数");
        }
        
        boolean success = orderService.buy(userId, productId, merchantId, quantity);
        return success ? Result.success("添加成功") : Result.error("添加失败，商品不存在或未上架");
    }

    /**
     * 批量添加订单接口
     * @param orderList 请求参数列表，每个元素包含：
     *                  userId - 用户ID（int，必填）
     *                  productId - 商品ID（int，必填）
     *                  merchantId - 商户ID（int，必填）
     *                  quantity - 数量（int，必填）
     * @return 返回：成功和失败的数量
     */
    @PostMapping("/batch-add")
    public Result<Map<String, Object>> batchAdd(@RequestBody List<Map<String, Object>> orderList) {
        if (orderList == null || orderList.isEmpty()) {
            return Result.error("订单列表不能为空");
        }

        int successCount = 0;
        int failCount = 0;

        for (Map<String, Object> order : orderList) {
            Integer userId = (Integer) order.get("userId");
            Integer productId = (Integer) order.get("productId");
            Integer merchantId = (Integer) order.get("merchantId");
            Integer quantity = (Integer) order.get("quantity");
            
            if (userId == null || productId == null || merchantId == null || quantity == null) {
                failCount++;
                continue;
            }
            
            boolean success = orderService.buy(userId, productId, merchantId, quantity);
            if (success) {
                successCount++;
            } else {
                failCount++;
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        return Result.success(result);
    }
} 