package org.example.grainoilsystem.mapper;

import org.example.grainoilsystem.entity.Product;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

public interface ProductMapper {
    int insertProduct(Product product);
    int updateProduct(Product product);
    int deleteProductById(@Param("id") Integer id);
    List<Product> selectProductList(Map<String, Object> params);
    int countProductList(Map<String, Object> params);
    Product selectProductById(@Param("id") Integer id);
    Product selectByNameType(@Param("name") String name, @Param("type") String type);
    
    // 新增查询方法
    List<Map<String, Object>> selectProductListWithMerchant(Map<String, Object> params);
    int countProductListWithMerchant(Map<String, Object> params);
    
    // 新增获取所有商品类型的方法
    List<String> selectAllTypes();
    
    // 新增根据类型获取商品ID的方法
    List<Integer> selectProductIdsByType(@Param("type") String type);
} 