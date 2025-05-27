package org.example.grainoilsystem.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Order {
    private Integer id;
    private Integer userId;
    private Integer productId;
    private Integer merchantId;
    private Integer quantity;
    private Double price;
    private Double totalPrice;
    private Date createTime;
} 