package org.example.grainoilsystem.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Comment {
    private Integer id;
    private Integer userId;
    private Integer productId;
    private Integer merchantId;
    private String content;
    private Date createTime;
} 