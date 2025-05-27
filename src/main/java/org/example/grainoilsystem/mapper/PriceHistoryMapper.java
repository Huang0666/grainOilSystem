package org.example.grainoilsystem.mapper;

import org.example.grainoilsystem.entity.PriceHistory;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

public interface PriceHistoryMapper {
    int insert(PriceHistory priceHistory);
    List<PriceHistory> selectPriceTrend(@Param("productId") Integer productId, @Param("merchantId") Integer merchantId, @Param("days") Integer days);
    List<PriceHistory> selectList(Map<String, Object> params);
    int countList(Map<String, Object> params);
    List<PriceHistory> selectPriceTrendByProduct(@Param("productId") Integer productId, @Param("days") Integer days);
    List<Integer> selectDistinctProductIds();
} 