package org.example.grainoilsystem.service;

import org.example.grainoilsystem.entity.Comment;
import java.util.List;

public interface CommentService {
    boolean addComment(Integer userId, Integer productId, Integer merchantId, String content);
    List<Comment> getCommentsByProductAndMerchant(Integer productId, Integer merchantId);
    int countCommentByProductAndMerchant(Integer productId, Integer merchantId);
    boolean isCommented(Integer userId, Integer productId, Integer merchantId);
    boolean removeComment(Integer userId, Integer productId, Integer merchantId);
} 