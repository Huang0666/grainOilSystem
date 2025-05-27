package org.example.grainoilsystem.service;

public interface LikeService {
    boolean addLike(Integer userId, Integer productId, Integer merchantId);
    boolean removeLike(Integer userId, Integer productId, Integer merchantId);
    int countLikeByProductAndMerchant(Integer productId, Integer merchantId);
    boolean isLiked(Integer userId, Integer productId, Integer merchantId);
} 