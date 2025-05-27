package org.example.grainoilsystem.service.impl;

import org.example.grainoilsystem.entity.Announcement;
import org.example.grainoilsystem.mapper.AnnouncementMapper;
import org.example.grainoilsystem.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {
    @Autowired
    private AnnouncementMapper announcementMapper;

    @Override
    public boolean addAnnouncement(String title, String content) {
        Announcement announcement = new Announcement();
        announcement.setTitle(title);
        announcement.setContent(content);
        announcement.setCreateTime(new Date());
        return announcementMapper.insertAnnouncement(announcement) > 0;
    }

    @Override
    public boolean deleteAnnouncement(Integer id) {
        return announcementMapper.deleteAnnouncement(id) > 0;
    }

    @Override
    public List<Announcement> getAnnouncementList(int pageNum, int pageSize, String title, Integer isTop) {
        Map<String, Object> params = new HashMap<>();
        params.put("offset", (pageNum - 1) * pageSize);
        params.put("size", pageSize);
        params.put("title", title);
        params.put("isTop", isTop);
        return announcementMapper.selectList(params);
    }

    @Override
    public int getAnnouncementCount(String title, Integer isTop) {
        Map<String, Object> params = new HashMap<>();
        params.put("title", title);
        params.put("isTop", isTop);
        return announcementMapper.countList(params);
    }

    @Override
    public Announcement getAnnouncementById(Integer id) {
        return announcementMapper.selectById(id);
    }

    @Override
    public boolean updateAnnouncement(Announcement announcement) {
        announcement.setCreateTime(new Date());
        return announcementMapper.updateAnnouncement(announcement) > 0;
    }

    @Override
    public boolean updateAnnouncementTop(Integer id, Integer isTop) {
        return announcementMapper.updateAnnouncementTop(id, isTop) > 0;
    }

    @Override
    public Map<String, Object> getAnnouncementStats() {
        Map<String, Object> stats = new HashMap<>();
        // 获取总数
        int total = getAnnouncementCount(null, null);
        // 获取置顶公告数量
        int topCount = getAnnouncementCount(null, 1);
        // 获取未置顶公告数量
        int normalCount = getAnnouncementCount(null, 0);
        
        stats.put("total", total);
        stats.put("topCount", topCount);
        stats.put("normalCount", normalCount);
        
        return stats;
    }
} 