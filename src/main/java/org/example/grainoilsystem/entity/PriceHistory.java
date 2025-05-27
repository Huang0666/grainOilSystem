package org.example.grainoilsystem.entity;

import lombok.Data;
import java.util.Date;

@Data
public class PriceHistory {
    private Integer id;
    private Integer productId;
    private Double price;
    private Date recordTime;
} 