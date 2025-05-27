package org.example.grainoilsystem.service;

import org.example.grainoilsystem.entity.Merchant;
import java.util.List;

public interface MerchantService {
    boolean addMerchant(Merchant merchant);
    boolean updateMerchant(Merchant merchant);
    boolean deleteMerchantById(Integer id);
    List<Merchant> getMerchantList(int page, int size, String name, String phone);
    int countMerchantList(String name, String phone);
    Merchant getMerchantById(Integer id);
} 