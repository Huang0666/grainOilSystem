package org.example.grainoilsystem.entity;

import lombok.Data;

@Data
public class Like {
    private Integer id;
    private Integer userId;
    private Integer productId;
    private Integer merchantId;
} 