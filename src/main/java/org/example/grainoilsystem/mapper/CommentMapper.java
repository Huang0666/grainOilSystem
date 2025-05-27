package org.example.grainoilsystem.mapper;

import org.example.grainoilsystem.entity.Comment;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface CommentMapper {
    int insertComment(Comment comment);
    List<Comment> selectCommentsByProductAndMerchant(@Param("productId") Integer productId, @Param("merchantId") Integer merchantId);
    int countCommentByProductAndMerchant(@Param("productId") Integer productId, @Param("merchantId") Integer merchantId);
    Comment selectComment(@Param("userId") Integer userId, @Param("productId") Integer productId, @Param("merchantId") Integer merchantId);
    int deleteComment(@Param("userId") Integer userId, @Param("productId") Integer productId, @Param("merchantId") Integer merchantId);
} 