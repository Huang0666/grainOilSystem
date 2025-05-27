package org.example.grainoilsystem.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

public interface StatisticsMapper {
    Map<String, Object> selectTotalVolumeAndAmount();
    Map<String, Object> selectTodayVolumeAndAmount();
    List<Map<String, Object>> selectTrendVolumeAndAmount(int days);
    List<Map<String, Object>> selectProductRank(int limit);
    List<Map<String, Object>> selectTypeRank(int limit);
    List<Map<String, Object>> selectTypeMerchantRank(@Param("type") String type, @Param("limit") int limit);
} 