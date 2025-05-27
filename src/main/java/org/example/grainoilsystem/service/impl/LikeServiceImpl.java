package org.example.grainoilsystem.service.impl;

import org.example.grainoilsystem.entity.Like;
import org.example.grainoilsystem.mapper.LikeMapper;
import org.example.grainoilsystem.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl implements LikeService {
    @Autowired
    private LikeMapper likeMapper;

    @Override
    public boolean addLike(Integer userId, Integer productId, Integer merchantId) {
        if (likeMapper.selectLike(userId, productId, merchantId) != null) {
            return false; // 已点赞
        }
        Like like = new Like();
        like.setUserId(userId);
        like.setProductId(productId);
        like.setMerchantId(merchantId);
        return likeMapper.insertLike(like) > 0;
    }

    @Override
    public boolean removeLike(Integer userId, Integer productId, Integer merchantId) {
        return likeMapper.deleteLike(userId, productId, merchantId) > 0;
    }

    @Override
    public int countLikeByProductAndMerchant(Integer productId, Integer merchantId) {
        return likeMapper.countLikeByProductAndMerchant(productId, merchantId);
    }

    @Override
    public boolean isLiked(Integer userId, Integer productId, Integer merchantId) {
        return likeMapper.selectLike(userId, productId, merchantId) != null;
    }
} 