package org.example.grainoilsystem.mapper;

import org.example.grainoilsystem.entity.Favorite;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface FavoriteMapper {
    int insertFavorite(Favorite favorite);
    int deleteFavorite(@Param("userId") Integer userId, @Param("productId") Integer productId, @Param("merchantId") Integer merchantId);
    List<Favorite> selectFavoritesByUserId(@Param("userId") Integer userId);
    Favorite selectFavorite(@Param("userId") Integer userId, @Param("productId") Integer productId, @Param("merchantId") Integer merchantId);
    int countFavoriteByProductAndMerchant(@Param("productId") Integer productId, @Param("merchantId") Integer merchantId);
} 