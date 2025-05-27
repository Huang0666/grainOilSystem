package org.example.grainoilsystem.service.impl;

import org.example.grainoilsystem.entity.ProductMerchant;
import org.example.grainoilsystem.mapper.ProductMerchantMapper;
import org.example.grainoilsystem.service.ProductMerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class ProductMerchantServiceImpl implements ProductMerchantService {
    
    @Autowired
    private ProductMerchantMapper productMerchantMapper;

    @Override
    public boolean add(ProductMerchant productMerchant) {
        return productMerchantMapper.insert(productMerchant) > 0;
    }

    @Override
    public boolean update(ProductMerchant productMerchant) {
        return productMerchantMapper.update(productMerchant) > 0;
    }

    @Override
    public boolean deleteById(Integer id) {
        return productMerchantMapper.deleteById(id) > 0;
    }

    @Override
    public boolean deleteByProductId(Integer productId) {
        return productMerchantMapper.deleteByProductId(productId) > 0;
    }

    @Override
    public boolean deleteByMerchantId(Integer merchantId) {
        return productMerchantMapper.deleteByMerchantId(merchantId) > 0;
    }

    @Override
    public boolean updateStatus(Integer id, Integer status) {
        return productMerchantMapper.updateStatus(id, status) > 0;
    }

    @Override
    public ProductMerchant getById(Integer id) {
        return productMerchantMapper.selectById(id);
    }

    @Override
    public List<ProductMerchant> getByProductId(Integer productId) {
        return productMerchantMapper.selectByProductId(productId);
    }

    @Override
    public List<ProductMerchant> getByMerchantId(Integer merchantId) {
        return productMerchantMapper.selectByMerchantId(merchantId);
    }

    @Override
    public ProductMerchant getByProductAndMerchant(Integer productId, Integer merchantId) {
        return productMerchantMapper.selectByProductAndMerchant(productId, merchantId);
    }

    @Override
    public List<ProductMerchant> getList(Map<String, Object> params) {
        return productMerchantMapper.selectList(params);
    }

    @Override
    public int countList(Map<String, Object> params) {
        return productMerchantMapper.countList(params);
    }

    @Override
    @Transactional
    public boolean addProductToMerchant(Integer productId, Integer merchantId, Double currentPrice) {
        // 检查是否已存在关联
        ProductMerchant existing = getByProductAndMerchant(productId, merchantId);
        if (existing != null) {
            // 如果已存在且状态为下架，则更新状态为上架和价格
            if (existing.getStatus() == 0) {
                existing.setStatus(1);
                existing.setCurrentPrice(currentPrice);
                return update(existing);
            }
            return false;
        }
        
        // 创建新的关联关系
        ProductMerchant productMerchant = new ProductMerchant();
        productMerchant.setProductId(productId);
        productMerchant.setMerchantId(merchantId);
        productMerchant.setStatus(1); // 默认上架状态
        productMerchant.setCurrentPrice(currentPrice);
        return add(productMerchant);
    }

    @Override
    @Transactional
    public boolean addProductToMerchant(Integer productId, Integer merchantId) {
        return addProductToMerchant(productId, merchantId, null);
    }

    @Override
    @Transactional
    public boolean removeProductFromMerchant(Integer productId, Integer merchantId) {
        ProductMerchant existing = getByProductAndMerchant(productId, merchantId);
        if (existing == null) {
            return false;
        }
        return deleteById(existing.getId());
    }

    @Override
    @Transactional
    public boolean updateProductStatus(Integer productId, Integer merchantId, Integer status) {
        ProductMerchant existing = getByProductAndMerchant(productId, merchantId);
        if (existing == null) {
            return false;
        }
        existing.setStatus(status);
        return update(existing);
    }
} 