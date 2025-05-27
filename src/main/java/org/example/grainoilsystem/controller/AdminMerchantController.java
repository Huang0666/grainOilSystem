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
@RequestMapping("/api/admin/merchant")
public class AdminMerchantController {
    @Autowired
    private MerchantService merchantService;


    /**
     * 商户列表接口
     * @param params 请求参数：
     *               page - 页码（int，可选，默认1）
     *               size - 每页数量（int，可选，默认10）
     *               name - 商户名称（String，可选）
     *               phone - 商户电话（String，可选）
     * {
     *     "page":
     * }
     * @return 返回字段：
     *         list - 商户列表（List<Merchant>）
     *         total - 商户总数（int）
     */
    @PostMapping("/list")
    public Result<Map<String, Object>> list(@RequestBody Map<String, Object> params) {
        int page = params.get("page") == null ? 1 : (int) params.get("page");
        int size = params.get("size") == null ? 10 : (int) params.get("size");
        String name = params.get("name") == null ? null : (String) params.get("name");
        String phone = params.get("phone") == null ? null : (String) params.get("phone");
        List<Merchant> list = merchantService.getMerchantList(page, size, name, phone);
        int total = merchantService.countMerchantList(name, phone);
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        return Result.success(result);
    }

    /**
     * 新增商户接口
     * @param merchant 请求体参数：商户对象，包含name、phone等
     * @return 返回：添加成功/添加失败（String）
     */
    @PostMapping("/add")
    public Result<String> add(@RequestBody Merchant merchant) {
        boolean success = merchantService.addMerchant(merchant);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }

    /**
     * 修改商户接口
     * @param merchant 请求体参数：商户对象，包含id、name、phone等
     * @return 返回：修改成功/修改失败（String）
     */
    @PostMapping("/update")
    public Result<String> update(@RequestBody Merchant merchant) {
        boolean success = merchantService.updateMerchant(merchant);
        return success ? Result.success("修改成功") : Result.error("修改失败");
    }

    /**
     * 删除商户接口
     * @param params 请求参数：
     *               id - 商户ID（int，必填）
     * @return 返回：删除成功/删除失败（String）
     */
    @PostMapping("/delete")
    public Result<String> delete(@RequestBody Map<String, Object> params) {
        Integer id = (Integer) params.get("id");
        boolean success = merchantService.deleteMerchantById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 商户详情接口
     * @param params 请求参数：
     *               id - 商户ID（int，必填）
     * @return 返回：商户对象（Merchant）
     */
    @PostMapping("/detail")
    public Result<Merchant> detail(@RequestBody Map<String, Object> params) {
        Integer id = (Integer) params.get("id");
        return Result.success(merchantService.getMerchantById(id));
    }

    /**
     * 获取商家统计数据
     * @return 返回：
     *         total - 商家总数
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        int total = merchantService.countMerchantList(null, null);
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        return Result.success(stats);
    }
}
