package org.example.grainoilsystem.mapper;

import org.example.grainoilsystem.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

@Mapper
public interface AnnouncementMapper {
    int insertAnnouncement(Announcement announcement);
    int deleteAnnouncement(Integer id);
    int updateAnnouncement(Announcement announcement);
    int updateAnnouncementTop(@Param("id") Integer id, @Param("isTop") Integer isTop);
    
    // 查询方法
    List<Announcement> selectList(Map<String, Object> params);
    int countList(Map<String, Object> params);
    Announcement selectById(Integer id);
} 