package org.example.grainoilsystem.controller;

import org.example.grainoilsystem.entity.Comment;
import org.example.grainoilsystem.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.example.grainoilsystem.entity.Result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 评论相关接口
 * 提供添加评论和获取评论列表功能。
 */
@RestController
@RequestMapping("/api/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    /**
     * 添加评论接口
     * @param params 请求参数：
     *               userId - 用户ID（int，必填）
     *               productId - 商品ID（int，必填）
     *               merchantId - 商家ID（int，必填）
     *               content - 评论内容（String，必填）
     * @return 返回字段：
     *         success - 是否成功（boolean）
     *         msg - 提示信息（String）
     */
    @PostMapping("/add")
    public Result<Map<String, Object>> add(@RequestBody Map<String, Object> params) {
        Integer userId = (Integer) params.get("userId");
        Integer productId = (Integer) params.get("productId");
        Integer merchantId = (Integer) params.get("merchantId");
        String content = (String) params.get("content");
        
        if (userId == null || productId == null || merchantId == null || content == null) {
            return Result.error("缺少必要参数");
        }
        
        boolean success = commentService.addComment(userId, productId, merchantId, content);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("msg", success ? "评论成功" : "评论失败");
        return Result.success(result);
    }

    /**
     * 获取商品评论列表接口
     * @param params 请求参数：
     *               productId - 商品ID（int，必填）
     *               merchantId - 商家ID（int，必填）
     * @return 返回字段：
     *         List<Comment> - 评论列表
     */
    @PostMapping("/list")
    public Result<List<Comment>> list(@RequestBody Map<String, Object> params) {
        Integer productId = (Integer) params.get("productId");
        Integer merchantId = (Integer) params.get("merchantId");
        
        if (productId == null || merchantId == null) {
            return Result.error("缺少必要参数");
        }
        
        return Result.success(commentService.getCommentsByProductAndMerchant(productId, merchantId));
    }

    /**
     * 获取商品评论数量接口
     * @param params 请求参数：
     *               productId - 商品ID（int，必填）
     *               merchantId - 商家ID（int，必填）
     * @return 返回字段：
     *         count - 评论数量（int）
     */
    @PostMapping("/count")
    public Result<Map<String, Object>> count(@RequestBody Map<String, Object> params) {
        Integer productId = (Integer) params.get("productId");
        Integer merchantId = (Integer) params.get("merchantId");
        
        if (productId == null || merchantId == null) {
            return Result.error("缺少必要参数");
        }
        
        int count = commentService.countCommentByProductAndMerchant(productId, merchantId);
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        return Result.success(result);
    }

    /**
     * 判断用户是否已评论接口
     * @param params 请求参数：
     *               userId - 用户ID（int，必填）
     *               productId - 商品ID（int，必填）
     *               merchantId - 商家ID（int，必填）
     * @return 返回字段：
     *         commented - 是否已评论（boolean）
     */
    @PostMapping("/isComment")
    public Result<Map<String, Object>> isComment(@RequestBody Map<String, Object> params) {
        Integer userId = (Integer) params.get("userId");
        Integer productId = (Integer) params.get("productId");
        Integer merchantId = (Integer) params.get("merchantId");
        
        if (userId == null || productId == null || merchantId == null) {
            return Result.error("缺少必要参数");
        }
        
        boolean commented = commentService.isCommented(userId, productId, merchantId);
        Map<String, Object> result = new HashMap<>();
        result.put("commented", commented);
        return Result.success(result);
    }

    /**
     * 删除评论接口
     * @param params 请求参数：
     *               userId - 用户ID（int，必填）
     *               productId - 商品ID（int，必填）
     *               merchantId - 商家ID（int，必填）
     * @return 返回字段：
     *         success - 是否删除成功（boolean）
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
        
        boolean success = commentService.removeComment(userId, productId, merchantId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("msg", success ? "删除成功" : "删除失败");
        return Result.success(result);
    }
} 