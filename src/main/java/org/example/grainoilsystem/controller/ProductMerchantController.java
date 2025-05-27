package org.example.grainoilsystem.controller;

import org.example.grainoilsystem.entity.ProductMerchant;
import org.example.grainoilsystem.service.ProductMerchantService;
import org.example.grainoilsystem.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.example.grainoilsystem.entity.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product-merchant")
public class ProductMerchantController {
    
    @Autowired
    private ProductMerchantService productMerchantService;
    
    @Autowired
    private ProductService productService;
    

    /**
     * 获取商品列表
     * @param params 请求参数：
     *               page - 页码（int，可选，默认1）
     *               size - 每页数量（int，可选，默认10）
     * @return 返回字段：
     *         list - 商品列表（List<ProductMerchant>）
     *         total - 商品总数（int）
     */
    @PostMapping("/list")
    public Result<Map<String, Object>> getProductList(@RequestBody Map<String, Object> params) {
        int page = params.get("page") == null ? 1 : (int) params.get("page");
        int size = params.get("size") == null ? 10 : (int) params.get("size");
        
        params.put("offset", (page - 1) * size);
        params.put("size", size);
        
        List<ProductMerchant> list = productMerchantService.getList(params);
        int total = productMerchantService.countList(params);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        
        return Result.success(result);
    }

    /**
     * 获取商品详情
     * @param params 请求参数：
     *               productId - 商品ID（int，必填）
     *               merchantId - 商家ID（int，必填）
     * @return 返回字段：
     *         productMerchant - 商品详情（ProductMerchant）
     */
    @PostMapping("/detail")
    public Result<ProductMerchant> getProductDetail(@RequestBody Map<String, Object> params) {
        Integer productId = (Integer) params.get("productId");
        Integer merchantId = (Integer) params.get("merchantId");
        
        if (productId == null || merchantId == null) {
            return Result.error("商品ID和商家ID不能为空");
        }
        
        ProductMerchant detail = productMerchantService.getByProductAndMerchant(productId, merchantId);
        if (detail == null) {
            return Result.error("商品不存在");
        }
        
        return Result.success(detail);
    }

    /**
     * 获取商品列表（支持按商品类型和商家名称筛选）
     * @param params 请求参数：
     *               type - 商品类型（String，可选）
     *               merchantName - 商家名称（String，可选）
     *               page - 页码（int，可选，默认1）
     *               size - 每页数量（int，可选，默认10）
     * @return 返回字段：
     *         list - 商品列表（List<ProductMerchant>）
     *         total - 商品总数（int）
     */
    @PostMapping("/filtered-list")
    public Result<Map<String, Object>> getFilteredProductList(@RequestBody Map<String, Object> params) {
        String type = (String) params.get("type");
        String merchantName = (String) params.get("merchantName");
        int page = params.get("page") == null ? 1 : (int) params.get("page");
        int size = params.get("size") == null ? 10 : (int) params.get("size");
        
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("offset", (page - 1) * size);
        queryParams.put("size", size);
        
        // 如果指定了商品类型，添加到查询参数中
        if (type != null && !type.trim().isEmpty()) {
            queryParams.put("productType", type.trim());
        }
        
        // 如果指定了商家名称，添加到查询参数中
        if (merchantName != null && !merchantName.trim().isEmpty()) {
            queryParams.put("merchantName", merchantName.trim());
        }
        
        // 获取分页后的数据
        List<ProductMerchant> list = productMerchantService.getList(queryParams);
        int total = productMerchantService.countList(queryParams);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        
        return Result.success(result);
    }
} 