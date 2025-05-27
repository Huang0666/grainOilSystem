package org.example.grainoilsystem.controller;

import org.example.grainoilsystem.entity.PriceHistory;
import org.example.grainoilsystem.service.PriceService;
import org.example.grainoilsystem.entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/price")
public class AdminPriceController {
    
    @Autowired
    private PriceService priceService;

    /**
     * 添加价格记录
     * @param params 请求参数：
     *               productId - 商品ID（int，必填）
     *               price - 价格（double，必填）
     *               recordTime - 记录时间（date，可选，默认当前时间）
     * @return 返回：添加成功/添加失败（String）
     */
    @PostMapping("/add")
    public Result<String> addPrice(@RequestBody Map<String, Object> params) {
        Integer productId = (Integer) params.get("productId");
        Double price = params.get("price") instanceof Integer ?
            ((Integer) params.get("price")).doubleValue() :
            (Double) params.get("price");
        Date recordTime = params.get("recordTime") != null ?
            new Date((Long) params.get("recordTime")) :
            new Date();

        if (productId == null || price == null) {
            return Result.error("商品ID和价格不能为空");
        }

        PriceHistory priceHistory = new PriceHistory();
        priceHistory.setProductId(productId);
        priceHistory.setPrice(price);
        priceHistory.setRecordTime(recordTime);

        boolean success = priceService.addPrice(priceHistory);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }

    /**
     * 获取价格历史列表
     * @param params 请求参数：
     *               productId - 商品ID（int，可选）
     *               startTime - 开始时间（long，可选）
     *               endTime - 结束时间（long，可选）
     *               page - 页码（int，可选，默认1）
     *               size - 每页数量（int，可选，默认10）
     * @return 返回：价格历史列表和总数
     */
    @PostMapping("/list")
    public Result<Map<String, Object>> getPriceList(@RequestBody Map<String, Object> params) {
        int page = params.get("page") == null ? 1 : (int) params.get("page");
        int size = params.get("size") == null ? 10 : (int) params.get("size");
        int offset = (page - 1) * size;
        
        // 处理时间参数
        if (params.get("startTime") != null) {
            params.put("startTime", new Date((Long) params.get("startTime")));
        }
        if (params.get("endTime") != null) {
            params.put("endTime", new Date((Long) params.get("endTime")));
        }
        
        params.put("offset", offset);
        params.put("size", size);
        
        List<PriceHistory> list = priceService.getPriceList(params);
        int total = priceService.countPriceList(params);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        
        return Result.success(result);
    }

    /**
     * 获取价格趋势
     * @param params 请求参数：
     *               productId - 商品ID（int，必填）
     *               days - 天数（int，必填）
     * @return 返回：价格趋势数据
     */
    @PostMapping("/trend")
    public Result<List<PriceHistory>> getPriceTrend(@RequestBody Map<String, Object> params) {
        Integer productId = (Integer) params.get("productId");
        Integer days = (Integer) params.get("days");
        if (productId == null || days == null) {
            return Result.error("商品ID和天数不能为空");
        }
        List<PriceHistory> trend = priceService.getPriceTrend(productId, days);
        return Result.success(trend);
    }

    /**
     * 预测价格
     * @param params 请求参数：
     *               productId - 商品ID（int，必填）
     *               days - 天数（int，必填）
     * @return 返回：价格预测数据
     */
    @PostMapping("/predict")
    public Result<Map<String, Object>> predictPrice(@RequestBody Map<String, Object> params) {
        Integer productId = (Integer) params.get("productId");
        Integer days = (Integer) params.get("days");
        if (productId == null || days == null) {
            return Result.error("商品ID和天数不能为空");
        }
        Map<String, Object> prediction = priceService.predictPrice(productId, days);
        if (prediction == null) {
            return Result.error("没有足够的历史数据进行预测");
        }
        return Result.success(prediction);
    }
} 