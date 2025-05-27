package org.example.grainoilsystem.service;

import org.example.grainoilsystem.entity.PriceHistory;
import java.util.List;
import java.util.Map;

public interface PriceService {
    boolean addPrice(PriceHistory priceHistory);
    List<PriceHistory> getPriceTrend(Integer productId, Integer days);
    List<PriceHistory> getPriceList(Map<String, Object> params);
    int countPriceList(Map<String, Object> params);
    Map<String, Object> predictPrice(Integer productId, Integer days);
    List<Integer> getDistinctProductIdsFromHistory();
} 