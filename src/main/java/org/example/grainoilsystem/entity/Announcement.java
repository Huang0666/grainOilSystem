package org.example.grainoilsystem.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Announcement {
    private Integer id;
    private String title;
    private String content;
    private Integer isTop; // 0=不置顶，1=置顶
    private Date createTime;
} 