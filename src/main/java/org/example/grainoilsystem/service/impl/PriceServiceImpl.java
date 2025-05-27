package org.example.grainoilsystem.service.impl;

import org.example.grainoilsystem.entity.PriceHistory;
import org.example.grainoilsystem.mapper.PriceHistoryMapper;
import org.example.grainoilsystem.service.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PriceServiceImpl implements PriceService {
    @Autowired
    private PriceHistoryMapper priceHistoryMapper;

    @Override
    public boolean addPrice(PriceHistory priceHistory) {
        return priceHistoryMapper.insert(priceHistory) > 0;
    }

    @Override
    public List<PriceHistory> getPriceTrend(Integer productId, Integer days) {
        // 只根据productId查询，不再区分merchantId
        return priceHistoryMapper.selectPriceTrendByProduct(productId, days);
    }

    @Override
    public List<PriceHistory> getPriceList(Map<String, Object> params) {
        return priceHistoryMapper.selectList(params);
    }

    @Override
    public int countPriceList(Map<String, Object> params) {
        return priceHistoryMapper.countList(params);
    }

    @Override
    public Map<String, Object> predictPrice(Integer productId, Integer days) {
        // 取最近30天的历史价格做线性回归
        List<PriceHistory> history = priceHistoryMapper.selectPriceTrendByProduct(productId, 30);
        int n = history.size();
        if (n < 2) return null;
        // x为天数（0,1,2...），y为价格
        double sumX = 0, sumY = 0, sumXY = 0, sumXX = 0;
        for (int i = 0; i < n; i++) {
            sumX += i;
            sumY += history.get(i).getPrice();
            sumXY += i * history.get(i).getPrice();
            sumXX += i * i;
        }
        double avgX = sumX / n;
        double avgY = sumY / n;
        double denominator = sumXX - n * avgX * avgX;
        double a = denominator == 0 ? 0 : (sumXY - n * avgX * avgY) / denominator; // 斜率
        double b = avgY - a * avgX; // 截距
        // 预测未来days天
        List<Map<String, Object>> predictions = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        if (n > 0) cal.setTime(history.get(n - 1).getRecordTime());
        for (int i = 1; i <= days; i++) {
            Map<String, Object> prediction = new HashMap<>();
            cal.add(Calendar.DAY_OF_MONTH, 1);
            double price = a * (n + i - 1) + b;
            prediction.put("date", String.format("%tF", cal.getTime()));
            prediction.put("price", Math.round(price * 100.0) / 100.0);
            predictions.add(prediction);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("predictions", predictions);
        result.put("trend", a > 0 ? "上涨" : a < 0 ? "下跌" : "稳定");
        result.put("slope", Math.round(a * 100.0) / 100.0);
        return result;
    }

    @Override
    public List<Integer> getDistinctProductIdsFromHistory() {
        return priceHistoryMapper.selectDistinctProductIds();
    }
} 