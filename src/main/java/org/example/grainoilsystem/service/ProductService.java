package org.example.grainoilsystem.service;

import org.example.grainoilsystem.entity.Product;
import java.util.List;
import java.util.Map;

public interface ProductService {
    boolean addProduct(Product product);
    boolean updateProduct(Product product);
    boolean deleteProductById(Integer id);
    List<Product> getProductList(Map<String, Object> params);
    int countProductList(Map<String, Object> params);
    Product getProductById(Integer id);
    boolean existsByNameType(String name, String type);
    
    // 新增获取带商家信息的商品列表方法
    List<Map<String, Object>> getProductListWithMerchant(Map<String, Object> params);
    int countProductListWithMerchant(Map<String, Object> params);
    
    // 新增获取所有商品类型的方法
    List<String> getAllTypes();
    
    // 新增根据类型获取商品ID的方法
    List<Integer> getProductIdsByType(String type);
} 