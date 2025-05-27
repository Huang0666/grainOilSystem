package org.example.grainoilsystem.mapper;

import org.example.grainoilsystem.entity.Like;
import org.apache.ibatis.annotations.Param;

public interface LikeMapper {
    int insertLike(Like like);
    int deleteLike(@Param("userId") Integer userId, @Param("productId") Integer productId, @Param("merchantId") Integer merchantId);
    int countLikeByProductAndMerchant(@Param("productId") Integer productId, @Param("merchantId") Integer merchantId);
    Like selectLike(@Param("userId") Integer userId, @Param("productId") Integer productId, @Param("merchantId") Integer merchantId);
} 