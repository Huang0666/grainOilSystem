package org.example.grainoilsystem.entity;

import lombok.Data;
import java.util.Date;

@Data
public class ProductMerchant {
    private Integer id;
    private Integer productId;
    private Integer merchantId;
    private Integer status;
    private Double currentPrice; // 当前价格
    
    // 关联字段
    private String productName;
    private String productType;
    private String merchantName;
    private Date recordTime;  // 记录时间
    private String imageUrl; // 图片地址
} 