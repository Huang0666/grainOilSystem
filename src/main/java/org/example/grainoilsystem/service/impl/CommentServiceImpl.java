package org.example.grainoilsystem.service.impl;

import org.example.grainoilsystem.entity.Comment;
import org.example.grainoilsystem.mapper.CommentMapper;
import org.example.grainoilsystem.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;

    @Override
    public boolean addComment(Integer userId, Integer productId, Integer merchantId, String content) {
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setProductId(productId);
        comment.setMerchantId(merchantId);
        comment.setContent(content);
        comment.setCreateTime(new Date());
        return commentMapper.insertComment(comment) > 0;
    }

    @Override
    public List<Comment> getCommentsByProductAndMerchant(Integer productId, Integer merchantId) {
        return commentMapper.selectCommentsByProductAndMerchant(productId, merchantId);
    }

    @Override
    public int countCommentByProductAndMerchant(Integer productId, Integer merchantId) {
        return commentMapper.countCommentByProductAndMerchant(productId, merchantId);
    }

    @Override
    public boolean isCommented(Integer userId, Integer productId, Integer merchantId) {
        return commentMapper.selectComment(userId, productId, merchantId) != null;
    }

    @Override
    public boolean removeComment(Integer userId, Integer productId, Integer merchantId) {
        return commentMapper.deleteComment(userId, productId, merchantId) > 0;
    }
} 