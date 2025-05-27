package org.example.grainoilsystem.entity;

import lombok.Data;

@Data
public class Product {
    private Integer id;
    private String name;
    private String type;
    private String imageUrl;  // 商品图片URL
}
