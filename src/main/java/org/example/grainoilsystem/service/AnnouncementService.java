package org.example.grainoilsystem.service;

import org.example.grainoilsystem.entity.Announcement;
import java.util.List;
import java.util.Map;

public interface AnnouncementService {
    boolean addAnnouncement(String title, String content);
    boolean deleteAnnouncement(Integer id);
    boolean updateAnnouncement(Announcement announcement);
    boolean updateAnnouncementTop(Integer id, Integer isTop);
    
    // 查询方法
    List<Announcement> getAnnouncementList(int pageNum, int pageSize, String title, Integer isTop);
    int getAnnouncementCount(String title, Integer isTop);
    Announcement getAnnouncementById(Integer id);
    
    // 统计方法
    Map<String, Object> getAnnouncementStats();
} 