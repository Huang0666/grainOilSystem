package org.example.grainoilsystem.controller;

import org.example.grainoilsystem.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.example.grainoilsystem.entity.Result;

import java.util.HashMap;
import java.util.Map;

/**
 * 点赞相关接口
 * 提供点赞、取消点赞、统计点赞数、判断是否点赞功能。
 */
@RestController
@RequestMapping("/api/like")
public class LikeController {
    @Autowired
    private LikeService likeService;

    /**
     * 点赞接口
     * @param params 请求参数：
     *               userId - 用户ID（int，必填）
     *               productId - 商品ID（int，必填）
     *               merchantId - 商家ID（int，必填）
     * @return 返回字段：
     *         success - 是否点赞成功（boolean）
     *         msg - 提示信息（String）
     */
    @PostMapping("/add")
    public Result<Map<String, Object>> add(@RequestBody Map<String, Object> params) {
        Integer userId = (Integer) params.get("userId");
        Integer productId = (Integer) params.get("productId");
        Integer merchantId = (Integer) params.get("merchantId");
        
        if (userId == null || productId == null || merchantId == null) {
            return Result.error("缺少必要参数");
        }
        
        boolean success = likeService.addLike(userId, productId, merchantId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("msg", success ? "点赞成功" : "已点赞");
        return Result.success(result);
    }

    /**
     * 取消点赞接口
     * @param params 请求参数：
     *               userId - 用户ID（int，必填）
     *               productId - 商品ID（int，必填）
     *               merchantId - 商家ID（int，必填）
     * @return 返回字段：
     *         success - 是否取消成功（boolean）
     *         msg - 提示信息（String）
     */
    @PostMapping("/remove")
    public Result<Map<String, Object>> remove(@RequestBody Map<String, Object> params) {
        Integer userId = (Integer) params.get("userId");
        Integer productId = (Integer) params.get("productId");
        Integer merchantId = (Integer) params.get("merchantId");
        
        if (userId == null || productId == null || merchantId == null) {
            return Result.error("缺少必要参数");
        }
        
        boolean success = likeService.removeLike(userId, productId, merchantId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("msg", success ? "取消点赞成功" : "未点赞");
        return Result.success(result);
    }

    /**
     * 获取商品点赞数接口
     * @param params 请求参数：
     *               productId - 商品ID（int，必填）
     *               merchantId - 商家ID（int，必填）
     * @return 返回字段：
     *         count - 点赞数（int）
     */
    @PostMapping("/count")
    public Result<Map<String, Object>> count(@RequestBody Map<String, Object> params) {
        Integer productId = (Integer) params.get("productId");
        Integer merchantId = (Integer) params.get("merchantId");
        
        if (productId == null || merchantId == null) {
            return Result.error("缺少必要参数");
        }
        
        int count = likeService.countLikeByProductAndMerchant(productId, merchantId);
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        return Result.success(result);
    }

    /**
     * 判断商品是否已点赞接口
     * @param params 请求参数：
     *               userId - 用户ID（int，必填）
     *               productId - 商品ID（int，必填）
     *               merchantId - 商家ID（int，必填）
     * @return 返回字段：
     *         liked - 是否已点赞（boolean）
     */
    @PostMapping("/isLiked")
    public Result<Map<String, Object>> isLiked(@RequestBody Map<String, Object> params) {
        Integer userId = (Integer) params.get("userId");
        Integer productId = (Integer) params.get("productId");
        Integer merchantId = (Integer) params.get("merchantId");
        
        if (userId == null || productId == null || merchantId == null) {
            return Result.error("缺少必要参数");
        }
        
        boolean liked = likeService.isLiked(userId, productId, merchantId);
        Map<String, Object> result = new HashMap<>();
        result.put("liked", liked);
        return Result.success(result);
    }
} 