package org.example.grainoilsystem.controller;

import org.example.grainoilsystem.entity.Favorite;
import org.example.grainoilsystem.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.example.grainoilsystem.entity.Result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 收藏相关接口
 * 提供添加、移除、查询收藏功能。
 */
@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {
    @Autowired
    private FavoriteService favoriteService;

    /**
     * 添加收藏接口
     * @param params 请求参数：
     *               userId - 用户ID（int，必填）
     *               productId - 商品ID（int，必填）
     *               merchantId - 商家ID（int，必填）
     * @return 返回字段：
     *         success - 是否收藏成功（boolean）
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
        
        boolean success = favoriteService.addFavorite(userId, productId, merchantId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("msg", success ? "收藏成功" : "已收藏");
        return Result.success(result);
    }

    /**
     * 取消收藏接口
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
        
        boolean success = favoriteService.removeFavorite(userId, productId, merchantId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("msg", success ? "取消收藏成功" : "未收藏");
        return Result.success(result);
    }

    /**
     * 获取用户收藏列表接口
     * @param params 请求参数：
     *               userId - 用户ID（int，必填）
     * @return 返回字段：
     *         List<Favorite> - 收藏列表
     */
    @PostMapping("/list")
    public Result<List<Favorite>> list(@RequestBody Map<String, Object> params) {
        Integer userId = (Integer) params.get("userId");
        
        if (userId == null) {
            return Result.error("缺少必要参数");
        }
        
        return Result.success(favoriteService.getFavoritesByUserId(userId));
    }

    /**
     * 判断商品是否已收藏接口
     * @param params 请求参数：
     *               userId - 用户ID（int，必填）
     *               productId - 商品ID（int，必填）
     *               merchantId - 商家ID（int，必填）
     * @return 返回字段：
     *         favorite - 是否已收藏（boolean）
     */
    @PostMapping("/isFavorite")
    public Result<Map<String, Object>> isFavorite(@RequestBody Map<String, Object> params) {
        Integer userId = (Integer) params.get("userId");
        Integer productId = (Integer) params.get("productId");
        Integer merchantId = (Integer) params.get("merchantId");
        
        if (userId == null || productId == null || merchantId == null) {
            return Result.error("缺少必要参数");
        }
        
        boolean favorite = favoriteService.isFavorite(userId, productId, merchantId);
        Map<String, Object> result = new HashMap<>();
        result.put("favorite", favorite);
        return Result.success(result);
    }

    /**
     * 获取商品收藏数量接口
     * @param params 请求参数：
     *               productId - 商品ID（int，必填）
     *               merchantId - 商家ID（int，必填）
     * @return 返回字段：
     *         count - 收藏数量（int）
     */
    @PostMapping("/count")
    public Result<Map<String, Object>> count(@RequestBody Map<String, Object> params) {
        Integer productId = (Integer) params.get("productId");
        Integer merchantId = (Integer) params.get("merchantId");
        
        if (productId == null || merchantId == null) {
            return Result.error("缺少必要参数");
        }
        
        int count = favoriteService.countFavoriteByProductAndMerchant(productId, merchantId);
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        return Result.success(result);
    }
} 