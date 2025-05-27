package org.example.grainoilsystem.controller;

import org.example.grainoilsystem.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.example.grainoilsystem.entity.Result;

import java.util.HashMap;
import java.util.Map;

/**
 * 统计相关接口
 * 提供平台成交量、成交额统计及趋势查询功能。
 */
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {
    @Autowired
    private StatisticsService statisticsService;

    /**
     * 平台总成交量、总成交额和今日成交量、成交额统计接口
     * 无需参数
     * @return 返回字段：
     *         totalVolume - 总成交量（int）
     *         totalAmount - 总成交额（double）
     *         todayVolume - 今日成交量（int）
     *         todayAmount - 今日成交额（double）
     */
    @GetMapping("/overview")
    public Result<Map<String, Object>> overview() {
        Map<String, Object> total = statisticsService.getTotalVolumeAndAmount();
        Map<String, Object> today = statisticsService.getTodayVolumeAndAmount();
        Map<String, Object> result = new HashMap<>();
        result.putAll(total);
        result.putAll(today);
        return Result.success(result);
    }

    /**
     * 平台每日成交量、成交额趋势接口
     * @param params 请求参数：
     *               days - 查询天数（int，可选，默认7）
     * @return 返回字段：
     *         trend - 每日成交量和成交额列表（List<Map<String,Object>>）
     *                 每个元素包含：day-日期，volume-成交量，amount-成交额
     */
    @PostMapping("/trend")
    public Result<Map<String, Object>> trend(@RequestBody Map<String, Object> params) {
        int days = params.get("days") == null ? 7 : (int) params.get("days");
        Map<String, Object> result = new HashMap<>();
        result.put("trend", statisticsService.getTrendVolumeAndAmount(days));
        return Result.success(result);
    }

    /**
     * 品类销量排行（按type）
     * @param params 请求参数：limit（可选，默认10）
     * @return 排行榜列表
     */
    @PostMapping("/type/rank")
    public Result<Map<String, Object>> typeRank(@RequestBody Map<String, Object> params) {
        int limit = params.get("limit") == null ? 10 : (int) params.get("limit");
        Map<String, Object> result = new HashMap<>();
        result.put("rank", statisticsService.getTypeRank(limit));
        return Result.success(result);
    }

    /**
     * 某品类在各商户的销量排行
     * @param params 请求参数：type（必填），limit（可选，默认10）
     * @return 排行榜列表
     */
    @PostMapping("/type/merchant-rank")
    public Result<Map<String, Object>> typeMerchantRank(@RequestBody Map<String, Object> params) {
        String type = (String) params.get("type");
        int limit = params.get("limit") == null ? 10 : (int) params.get("limit");
        Map<String, Object> result = new HashMap<>();
        result.put("rank", statisticsService.getTypeMerchantRank(type, limit));
        return Result.success(result);
    }
} 