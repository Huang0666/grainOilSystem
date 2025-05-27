package org.example.grainoilsystem.controller;

import org.example.grainoilsystem.entity.Merchant;
import org.example.grainoilsystem.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.example.grainoilsystem.entity.Result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/merchant")
public class MerchantController {
    
    @Autowired
    private MerchantService merchantService;
    
    /**
     * 获取商家列表
     * @param params 请求参数：
     *               page - 页码（int，可选，默认1）
     *               size - 每页数量（int，可选，默认10）
     *               keyword - 关键字（String，可选）
     *               phone - 手机号（String，可选）
     * @return 返回字段：
     *         list - 商家列表（List<Merchant>）
     */
    @PostMapping("/list")
    public Result<Map<String, Object>> getMerchantList(@RequestBody Map<String, Object> params) {
        int page = params.get("page") == null ? 1 : (int) params.get("page");
        int size = params.get("size") == null ? 10 : (int) params.get("size");
        String name = (String) params.get("keyword");
        String phone = (String) params.get("phone");
        
        List<Merchant> list = merchantService.getMerchantList(page, size, name, phone);
        int total = merchantService.countMerchantList(name, phone);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        
        return Result.success(result);
    }

    /**
     * 获取商家详情
     * @param params 请求参数：
     *               merchantId - 商家ID（int，必填）
     * @return 返回字段：
     *         merchant - 商家详情（Merchant）
     */
    @PostMapping("/detail")
    public Result<Merchant> getMerchantDetail(@RequestBody Map<String, Object> params) {
        Integer merchantId = (Integer) params.get("merchantId");
        if (merchantId == null) {
            return Result.error("商家ID不能为空");
        }
        
        Merchant merchant = merchantService.getMerchantById(merchantId);
        if (merchant == null) {
            return Result.error("商家不存在");
        }
        
        return Result.success(merchant);
    }
} 