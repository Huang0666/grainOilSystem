package org.example.grainoilsystem.controller;

import org.example.grainoilsystem.entity.Product;
import org.example.grainoilsystem.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.example.grainoilsystem.entity.Result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品相关接口
 * 提供商品的分页查询和详情查询功能。
 */
@RestController
@RequestMapping("/api/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    /**
     * 商品分页列表接口
     * @param params 请求参数：
     *               page - 页码（int，必填）
     *               size - 每页数量（int，必填）
     *               type - 商品类型（String，可选）
     * @return 返回字段：
     *         list - 商品列表（List<Map<String, Object>>）
     *         total - 商品总数（int）
     */
    @PostMapping("/list")
    public Result<Map<String, Object>> list(@RequestBody Map<String, Object> params) {
        int page = params.get("page") == null ? 1 : (int) params.get("page");
        int size = params.get("size") == null ? 10 : (int) params.get("size");
        String type = params.get("type") == null ? null : (String) params.get("type");
        int offset = (page - 1) * size;
        
        Map<String, Object> query = new HashMap<>();
        query.put("offset", offset);
        query.put("size", size);
        query.put("type", type);
        query.put("status", 1); // 只查上架商品
        
        List<Map<String, Object>> list = productService.getProductListWithMerchant(query);
        int total = productService.countProductListWithMerchant(query);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        return Result.success(result);
    }

    /**
     * 商品详情接口
     * @param params 请求参数：
     *               id - 商品ID（int，必填）
     * @return 返回字段：
     *         Product对象，包含商品的详细信息
     */
    @PostMapping("/detail")
    public Result<Product> detail(@RequestBody Map<String, Object> params) {
        Integer id = (Integer) params.get("id");
        if (id == null) {
            return Result.error("商品ID不能为空");
        }
        
        Product product = productService.getProductById(id);
        if (product == null) {
            return Result.error("商品不存在");
        }
        
        return Result.success(product);
    }

    /**
     * 获取所有商品类型
     * @return 返回字段：
     *         types - 商品类型列表（List<String>）
     */
    @PostMapping("/types")
    public Result<List<String>> getAllTypes() {
        List<String> types = productService.getAllTypes();
        return Result.success(types);
    }

    /**
     * 根据商品类型获取商品ID列表
     * @param params 请求参数：
     *               type - 商品类型（String，必填）
     * @return 返回字段：
     *         productIds - 商品ID列表（List<Integer>）
     */
    @PostMapping("/ids-by-type")
    public Result<List<Integer>> getProductIdsByType(@RequestBody Map<String, Object> params) {
        String type = (String) params.get("type");
        if (type == null || type.trim().isEmpty()) {
            return Result.error("商品类型不能为空");
        }
        
        List<Integer> productIds = productService.getProductIdsByType(type);
        return Result.success(productIds);
    }
} 