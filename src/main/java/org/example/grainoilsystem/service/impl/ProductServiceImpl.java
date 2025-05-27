package org.example.grainoilsystem.service.impl;

import org.example.grainoilsystem.entity.Product;
import org.example.grainoilsystem.mapper.ProductMapper;
import org.example.grainoilsystem.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductMapper productMapper;

    @Override
    public boolean addProduct(Product product) {
        return productMapper.insertProduct(product) > 0;
    }

    @Override
    public boolean updateProduct(Product product) {
        return productMapper.updateProduct(product) > 0;
    }

    @Override
    public boolean deleteProductById(Integer id) {
        return productMapper.deleteProductById(id) > 0;
    }

    @Override
    public List<Product> getProductList(Map<String, Object> params) {
        return productMapper.selectProductList(params);
    }

    @Override
    public int countProductList(Map<String, Object> params) {
        return productMapper.countProductList(params);
    }

    @Override
    public Product getProductById(Integer id) {
        return productMapper.selectProductById(id);
    }

    @Override
    public boolean existsByNameType(String name, String type) {
        return productMapper.selectByNameType(name, type) != null;
    }

    @Override
    public List<Map<String, Object>> getProductListWithMerchant(Map<String, Object> params) {
        return productMapper.selectProductListWithMerchant(params);
    }

    @Override
    public int countProductListWithMerchant(Map<String, Object> params) {
        return productMapper.countProductListWithMerchant(params);
    }

    @Override
    public List<String> getAllTypes() {
        return productMapper.selectAllTypes();
    }

    @Override
    public List<Integer> getProductIdsByType(String type) {
        return productMapper.selectProductIdsByType(type);
    }
} 