package org.example.grainoilsystem.controller;

import org.example.grainoilsystem.entity.ProductMerchant;
import org.example.grainoilsystem.service.ProductMerchantService;
import org.example.grainoilsystem.entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/product-merchant")
public class AdminProductMerchantController {
    
    @Autowired
    private ProductMerchantService productMerchantService;

    /**
     * 添加商品到商家
     * @param params 请求参数：
     *               productId - 商品ID（int，必填）
     *               merchantId - 商家ID（int，必填）
     *               current_price - 商品价格（double，可选）
     * @return 返回：添加成功/添加失败（String）
     */
    @PostMapping("/add")
    public Result<String> addProductToMerchant(@RequestBody Map<String, Object> params) {
        Integer productId = (Integer) params.get("productId");
        Integer merchantId = (Integer) params.get("merchantId");
        Double currentPrice = null;
        
        // 处理价格参数
        Object priceObj = params.get("currentPrice");
        if (priceObj instanceof Integer) {
            currentPrice = ((Integer) priceObj).doubleValue();
        } else if (priceObj instanceof Double) {
            currentPrice = (Double) priceObj;
        } else if (priceObj instanceof String) {
            try {
                currentPrice = Double.parseDouble((String) priceObj);
            } catch (NumberFormatException e) {
                return Result.error("价格格式错误");
            }
        }
        
        if (productId == null || merchantId == null) {
            return Result.error("商品ID和商家ID不能为空");
        }
        
        if (currentPrice == null) {
            return Result.error("商品价格不能为空");
        }
        
        boolean success = productMerchantService.addProductToMerchant(productId, merchantId, currentPrice);
        return success ? Result.success("添加成功") : Result.error("添加失败，该商品可能已经添加");
    }

    /**
     * 从商家移除商品
     * @param params 请求参数：
     *               productId - 商品ID（int，必填）
     *               merchantId - 商家ID（int，必填）
     * @return 返回：移除成功/移除失败（String）
     */
    @PostMapping("/remove")
    public Result<String> removeProductFromMerchant(@RequestBody Map<String, Object> params) {
        Integer productId = (Integer) params.get("productId");
        Integer merchantId = (Integer) params.get("merchantId");
        
        if (productId == null || merchantId == null) {
            return Result.error("商品ID和商家ID不能为空");
        }
        
        boolean success = productMerchantService.removeProductFromMerchant(productId, merchantId);
        return success ? Result.success("移除成功") : Result.error("移除失败，未找到相关记录");
    }

    /**
     * 更新商品状态
     * @param params 请求参数：
     *               productId - 商品ID（int，必填）
     *               merchantId - 商家ID（int，必填）
     *               status - 状态（int，必填，1-上架，0-下架）
     * @return 返回：更新成功/更新失败（String）
     */
    @PostMapping("/status")
    public Result<String> updateStatus(@RequestBody Map<String, Object> params) {
        Integer productId = (Integer) params.get("productId");
        Integer merchantId = (Integer) params.get("merchantId");
        Integer status = (Integer) params.get("status");
        
        if (productId == null || merchantId == null || status == null) {
            return Result.error("商品ID、商家ID和状态不能为空");
        }
        
        if (status != 0 && status != 1) {
            return Result.error("状态值无效");
        }
        
        boolean success = productMerchantService.updateProductStatus(productId, merchantId, status);
        return success ? Result.success("更新成功") : Result.error("更新失败，未找到相关记录");
    }

    /**
     * 获取商品-商家关联列表
     * @param params 请求参数：
     *               productId - 商品ID（int，可选）
     *               merchantId - 商家ID（int，可选）
     *               productName - 商品名称（String，可选）
     *               merchantName - 商家名称（String，可选）
     *               productType - 商品类型（String，可选）
     *               status - 上架状态（Integer，可选，1-上架，0-下架）
     *               orderBy - 排序字段（String，可选，默认按id降序）
     *               page - 页码（int，可选，默认1）
     *               size - 每页数量（int，可选，默认10）
     * @return 返回：
     *         list - 商品-商家关联列表
     *         total - 总数
     */
    @PostMapping("/list")
    public Result<Map<String, Object>> getList(@RequestBody Map<String, Object> params) {
        // 处理分页参数
        int page = params.get("page") == null ? 1 : (int) params.get("page");
        int size = params.get("size") == null ? 10 : (int) params.get("size");
        int offset = (page - 1) * size;
        
        // 处理查询参数
        Integer productId = (Integer) params.get("productId");
        Integer merchantId = (Integer) params.get("merchantId");
        String productName = (String) params.get("productName");
        String merchantName = (String) params.get("merchantName");
        String productType = (String) params.get("productType");
        Integer status = (Integer) params.get("status");
        String orderBy = (String) params.get("orderBy");
        
        // 构建查询参数
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("offset", offset);
        queryParams.put("size", size);
        
        if (productId != null) queryParams.put("productId", productId);
        if (merchantId != null) queryParams.put("merchantId", merchantId);
        if (productName != null && !productName.trim().isEmpty()) queryParams.put("productName", productName);
        if (merchantName != null && !merchantName.trim().isEmpty()) queryParams.put("merchantName", merchantName);
        if (productType != null && !productType.trim().isEmpty()) queryParams.put("productType", productType);
        if (status != null) queryParams.put("status", status);
        if (orderBy != null && !orderBy.trim().isEmpty()) queryParams.put("orderBy", orderBy);
        
        // 获取列表和总数
        List<ProductMerchant> list = productMerchantService.getList(queryParams);
        int total = productMerchantService.countList(queryParams);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        
        return Result.success(result);
    }

    /**
     * @deprecated 请使用 {@link #getList(Map)} 代替
     */
    @Deprecated
    @PostMapping("/merchant-list")
    public Result<Map<String, Object>> getMerchantList(@RequestBody Map<String, Object> params) {
        Integer productId = (Integer) params.get("productId");
        if (productId == null) {
            return Result.error("商品ID不能为空");
        }
        
        int page = params.get("page") == null ? 1 : (int) params.get("page");
        int size = params.get("size") == null ? 10 : (int) params.get("size");
        int offset = (page - 1) * size;
        
        params.put("offset", offset);
        params.put("size", size);
        
        List<ProductMerchant> list = productMerchantService.getList(params);
        int total = productMerchantService.countList(params);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        
        return Result.success(result);
    }

    /**
     * 获取商家的商品列表
     * @param params 请求参数：
     *               merchantId - 商家ID（int，必填）
     *               page - 页码（int，可选，默认1）
     *               size - 每页数量（int，可选，默认10）
     * @return 返回：商品列表和总数
     */
    @Deprecated
    @PostMapping("/product-list")
    public Result<Map<String, Object>> getProductList(@RequestBody Map<String, Object> params) {
        Integer merchantId = (Integer) params.get("merchantId");
        if (merchantId == null) {
            return Result.error("商家ID不能为空");
        }
        
        int page = params.get("page") == null ? 1 : (int) params.get("page");
        int size = params.get("size") == null ? 10 : (int) params.get("size");
        int offset = (page - 1) * size;
        
        params.put("offset", offset);
        params.put("size", size);
        
        List<ProductMerchant> list = productMerchantService.getList(params);
        int total = productMerchantService.countList(params);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        
        return Result.success(result);
    }

    /**
     * 获取商品-商家关联统计数据
     * @return 返回：
     *         total - 商品-商家关联总数
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        int total = productMerchantService.countList(new HashMap<>());
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        return Result.success(stats);
    }
} 