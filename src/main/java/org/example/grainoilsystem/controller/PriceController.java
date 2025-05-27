package org.example.grainoilsystem.controller;

import org.example.grainoilsystem.entity.PriceHistory;
import org.example.grainoilsystem.entity.Result;
import org.example.grainoilsystem.service.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/price")
public class PriceController {
    @Autowired
    private PriceService priceService;

    /**
     * 商品价格趋势接口
     * @param params 请求参数：
     *               productId - 商品ID（int，必填）
     *               days - 查询天数（int，可选，默认30）
     * @return 返回字段：
     *         trend - 价格趋势列表（List<PriceHistory>）
     */
    @PostMapping("/trend")
    public Result<Map<String, Object>> trend(@RequestBody Map<String, Object> params) {
        Integer productId = (Integer) params.get("productId");
        int days = params.get("days") == null ? 30 : (int) params.get("days");
        
        if (productId == null) {
            return Result.error("商品ID不能为空");
        }
        
        List<PriceHistory> trend = priceService.getPriceTrend(productId, days);
        Map<String, Object> result = new HashMap<>();
        result.put("trend", trend);
        return Result.success(result);
    }

    /**
     * 商品价格预测接口
     * @param params 请求参数：
     *               productId - 商品ID（int，必填）
     *               days - 预测天数（int，可选，默认7）
     * @return 返回字段：
     *         prediction - 价格预测数据
     */
    @PostMapping("/predict")
    public Result<Map<String, Object>> predict(@RequestBody Map<String, Object> params) {
        Integer productId = (Integer) params.get("productId");
        int days = params.get("days") == null ? 7 : (int) params.get("days");
        
        if (productId == null) {
            return Result.error("商品ID不能为空");
        }
        
        Map<String, Object> prediction = priceService.predictPrice(productId, days);
        if (prediction == null) {
            return Result.error("没有足够的历史数据进行预测");
        }
        
        return Result.success(prediction);
    }

    /**
     * 查询价格历史表中所有去重后的商品ID列表
     * @return 返回字段：productIds - 商品ID列表（List<Integer>）
     */
    @GetMapping("/product-ids")
    public Result<List<Integer>> getDistinctProductIds() {
        List<Integer> productIds = priceService.getDistinctProductIdsFromHistory();
        return Result.success(productIds);
    }
} 