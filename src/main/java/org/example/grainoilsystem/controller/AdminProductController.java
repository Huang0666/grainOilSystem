package org.example.grainoilsystem.controller;

import org.example.grainoilsystem.entity.Product;
import org.example.grainoilsystem.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.example.grainoilsystem.entity.Result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/product")
public class AdminProductController {
    @Autowired
    private ProductService productService;

    /**
     * 商品列表接口
     * @param params 请求参数：
     *               page - 页码（int，可选，默认1）
     *               size - 每页数量（int，可选，默认10）
     *               type - 商品类型（String，可选）
     * @return 返回字段：
     *         list - 商品列表（List<Product>）
     *         total - 商品总数（int）
     */
    @PostMapping("/list")
    public Result<Map<String, Object>> list(@RequestBody Map<String, Object> params) {
        int page = params.get("page") == null ? 1 : (int) params.get("page");
        int size = params.get("size") == null ? 10 : (int) params.get("size");
        int offset = (page - 1) * size;
        params.put("offset", offset);
        params.put("size", size);
        List<Product> list = productService.getProductList(params);
        int total = productService.countProductList(params);
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        return Result.success(result);
    }

    /**
     * 新增商品接口
     * @param product 请求体参数：商品对象，包含name、type
     * @return 返回：添加成功/添加失败（String）
     */
    @PostMapping("/add")
    public Result<String> add(@RequestBody Product product) {
        if (productService.existsByNameType(product.getName(), product.getType())) {
            return Result.error("该商品已存在");
        }
        boolean success = productService.addProduct(product);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }

    /**
     * 修改商品接口
     * @param product 请求体参数：商品对象，包含id、name、type
     * @return 返回：修改成功/修改失败（String）
     */
    @PostMapping("/update")
    public Result<String> update(@RequestBody Product product) {
        if (product.getId() == null) {
            return Result.error("商品ID不能为空");
        }
        Product existingProduct = productService.getProductById(product.getId());
        if (existingProduct == null) {
            return Result.error("商品不存在");
        }
        // 检查修改后的名称和类型是否与其他商品重复
        if (!existingProduct.getName().equals(product.getName()) || 
            !existingProduct.getType().equals(product.getType())) {
            if (productService.existsByNameType(product.getName(), product.getType())) {
                return Result.error("该商品已存在");
            }
        }
        boolean success = productService.updateProduct(product);
        return success ? Result.success("修改成功") : Result.error("修改失败");
    }

    /**
     * 商品详情接口
     * @param params 请求参数：
     *               id - 商品ID（int，必填）
     * @return 返回：商品对象（Product）
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
     * 获取所有商品类型列表
     * @return 返回：商品类型列表（List<String>）
     */
    @GetMapping("/types")
    public Result<List<String>> types() {
        List<String> types = productService.getAllTypes();
        return Result.success(types);
    }

    /**
     * 删除商品接口
     * @param params 请求参数：
     *               id - 商品ID（int，必填）
     * @return 返回：删除成功/删除失败（String）
     */
    @PostMapping("/delete")
    public Result<String> delete(@RequestBody Map<String, Object> params) {
        Integer id = (Integer) params.get("id");
        if (id == null) {
            return Result.error("商品ID不能为空");
        }
        
        Product product = productService.getProductById(id);
        if (product == null) {
            return Result.error("商品不存在");
        }
        
        boolean success = productService.deleteProductById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 获取商品统计数据
     * @return 返回：
     *         total - 商品总数
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        int total = productService.countProductList(new HashMap<>());
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        return Result.success(stats);
    }

    /**
     * 获取商品类型统计
     * @return 返回：
     *         total - 商品类型总数
     */
    @GetMapping("/type-stats")
    public Result<Map<String, Object>> getTypeStats() {
        int typeCount = productService.getAllTypes().size();
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", typeCount);
        return Result.success(stats);
    }
} 