package org.example.grainoilsystem.service;

import org.example.grainoilsystem.entity.Favorite;
import java.util.List;

public interface FavoriteService {
    boolean addFavorite(Integer userId, Integer productId, Integer merchantId);
    boolean removeFavorite(Integer userId, Integer productId, Integer merchantId);
    List<Favorite> getFavoritesByUserId(Integer userId);
    boolean isFavorite(Integer userId, Integer productId, Integer merchantId);
    int countFavoriteByProductAndMerchant(Integer productId, Integer merchantId);
} 