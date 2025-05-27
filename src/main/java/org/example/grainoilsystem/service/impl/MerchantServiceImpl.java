package org.example.grainoilsystem.service.impl;

import org.example.grainoilsystem.entity.Merchant;
import org.example.grainoilsystem.mapper.MerchantMapper;
import org.example.grainoilsystem.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MerchantServiceImpl implements MerchantService {
    @Autowired
    private MerchantMapper merchantMapper;

    @Override
    public boolean addMerchant(Merchant merchant) {
        return merchantMapper.insertMerchant(merchant) > 0;
    }

    @Override
    public boolean updateMerchant(Merchant merchant) {
        return merchantMapper.updateMerchant(merchant) > 0;
    }

    @Override
    public boolean deleteMerchantById(Integer id) {
        return merchantMapper.deleteMerchantById(id) > 0;
    }

    @Override
    public List<Merchant> getMerchantList(int page, int size, String name, String phone) {
        int offset = (page - 1) * size;
        Map<String, Object> params = new HashMap<>();
        params.put("offset", offset);
        params.put("size", size);
        params.put("name", name);
        params.put("phone", phone);
        return merchantMapper.selectMerchantList(params);
    }

    @Override
    public int countMerchantList(String name, String phone) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("phone", phone);
        return merchantMapper.countMerchantList(params);
    }

    @Override
    public Merchant getMerchantById(Integer id) {
        return merchantMapper.selectMerchantById(id);
    }
} 