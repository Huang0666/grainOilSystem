package org.example.grainoilsystem.service.impl;

import org.example.grainoilsystem.mapper.StatisticsMapper;
import org.example.grainoilsystem.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class StatisticsServiceImpl implements StatisticsService {
    @Autowired
    private StatisticsMapper statisticsMapper;

    @Override
    public Map<String, Object> getTotalVolumeAndAmount() {
        return statisticsMapper.selectTotalVolumeAndAmount();
    }

    @Override
    public Map<String, Object> getTodayVolumeAndAmount() {
        return statisticsMapper.selectTodayVolumeAndAmount();
    }

    @Override
    public List<Map<String, Object>> getTrendVolumeAndAmount(int days) {
        return statisticsMapper.selectTrendVolumeAndAmount(days);
    }

    @Override
    public List<Map<String, Object>> getTypeRank(int limit) {
        return statisticsMapper.selectTypeRank(limit);
    }

    @Override
    public List<Map<String, Object>> getTypeMerchantRank(String type, int limit) {
        return statisticsMapper.selectTypeMerchantRank(type, limit);
    }
} 