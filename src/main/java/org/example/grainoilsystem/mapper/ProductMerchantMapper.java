package org.example.grainoilsystem.mapper;

import org.example.grainoilsystem.entity.ProductMerchant;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

public interface ProductMerchantMapper {
    // 基本的 CRUD 操作
    int insert(ProductMerchant productMerchant);
    int update(ProductMerchant productMerchant);
    int deleteById(@Param("id") Integer id);
    int deleteByProductId(@Param("productId") Integer productId);
    int deleteByMerchantId(@Param("merchantId") Integer merchantId);
    
    // 状态更新
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);
    
    // 查询操作
    ProductMerchant selectById(@Param("id") Integer id);
    List<ProductMerchant> selectByProductId(@Param("productId") Integer productId);
    List<ProductMerchant> selectByMerchantId(@Param("merchantId") Integer merchantId);
    ProductMerchant selectByProductAndMerchant(@Param("productId") Integer productId, @Param("merchantId") Integer merchantId);
    List<ProductMerchant> selectList(Map<String, Object> params);
    int countList(Map<String, Object> params);
} 