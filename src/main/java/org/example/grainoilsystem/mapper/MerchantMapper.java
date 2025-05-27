package org.example.grainoilsystem.mapper;

import org.example.grainoilsystem.entity.Merchant;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

public interface MerchantMapper {
    int insertMerchant(Merchant merchant);
    int updateMerchant(Merchant merchant);
    int deleteMerchantById(@Param("id") Integer id);
    List<Merchant> selectMerchantList(Map<String, Object> params);
    int countMerchantList(Map<String, Object> params);
    Merchant selectMerchantById(@Param("id") Integer id);
} 