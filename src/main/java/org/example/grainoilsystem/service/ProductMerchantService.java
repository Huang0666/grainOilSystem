package org.example.grainoilsystem.service;

import org.example.grainoilsystem.entity.ProductMerchant;
import java.util.List;
import java.util.Map;

public interface ProductMerchantService {
    // 基本的 CRUD 操作
    boolean add(ProductMerchant productMerchant);
    boolean update(ProductMerchant productMerchant);
    boolean deleteById(Integer id);
    boolean deleteByProductId(Integer productId);
    boolean deleteByMerchantId(Integer merchantId);
    
    // 状态更新
    boolean updateStatus(Integer id, Integer status);
    
    // 查询操作
    ProductMerchant getById(Integer id);
    List<ProductMerchant> getByProductId(Integer productId);
    List<ProductMerchant> getByMerchantId(Integer merchantId);
    ProductMerchant getByProductAndMerchant(Integer productId, Integer merchantId);
    List<ProductMerchant> getList(Map<String, Object> params);
    int countList(Map<String, Object> params);
    
    // 业务操作
    boolean addProductToMerchant(Integer productId, Integer merchantId);
    boolean addProductToMerchant(Integer productId, Integer merchantId, Double currentPrice);
    boolean removeProductFromMerchant(Integer productId, Integer merchantId);
    boolean updateProductStatus(Integer productId, Integer merchantId, Integer status);
} 