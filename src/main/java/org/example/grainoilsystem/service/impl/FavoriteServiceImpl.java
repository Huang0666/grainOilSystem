package org.example.grainoilsystem.service.impl;

import org.example.grainoilsystem.entity.Favorite;
import org.example.grainoilsystem.mapper.FavoriteMapper;
import org.example.grainoilsystem.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteServiceImpl implements FavoriteService {
    @Autowired
    private FavoriteMapper favoriteMapper;

    @Override
    public boolean addFavorite(Integer userId, Integer productId, Integer merchantId) {
        if (favoriteMapper.selectFavorite(userId, productId, merchantId) != null) {
            return false; // 已收藏
        }
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        favorite.setMerchantId(merchantId);
        return favoriteMapper.insertFavorite(favorite) > 0;
    }

    @Override
    public boolean removeFavorite(Integer userId, Integer productId, Integer merchantId) {
        return favoriteMapper.deleteFavorite(userId, productId, merchantId) > 0;
    }

    @Override
    public List<Favorite> getFavoritesByUserId(Integer userId) {
        return favoriteMapper.selectFavoritesByUserId(userId);
    }

    @Override
    public boolean isFavorite(Integer userId, Integer productId, Integer merchantId) {
        return favoriteMapper.selectFavorite(userId, productId, merchantId) != null;
    }

    @Override
    public int countFavoriteByProductAndMerchant(Integer productId, Integer merchantId) {
        return favoriteMapper.countFavoriteByProductAndMerchant(productId, merchantId);
    }
} 