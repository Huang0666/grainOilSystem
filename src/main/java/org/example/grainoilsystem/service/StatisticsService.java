package org.example.grainoilsystem.service;

import java.util.List;
import java.util.Map;

public interface StatisticsService {
    Map<String, Object> getTotalVolumeAndAmount();
    Map<String, Object> getTodayVolumeAndAmount();
    List<Map<String, Object>> getTrendVolumeAndAmount(int days);
    List<Map<String, Object>> getTypeRank(int limit);
    List<Map<String, Object>> getTypeMerchantRank(String type, int limit);
} 