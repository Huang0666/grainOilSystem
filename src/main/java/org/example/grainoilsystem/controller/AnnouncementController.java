package org.example.grainoilsystem.controller;

import org.example.grainoilsystem.entity.Announcement;
import org.example.grainoilsystem.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.example.grainoilsystem.entity.Result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/announcement")
public class AnnouncementController {
    @Autowired
    private AnnouncementService announcementService;

    /**
     * 公告列表接口
     * @param params 请求参数：
     *               pageNum - 页码（int，可选，默认1）
     *               pageSize - 每页数量（int，可选，默认10）
     *               title - 标题关键词（String，可选）
     *               isTop - 是否置顶（Integer，可选）
     * @return 返回字段：
     *         list - 公告列表（List<Announcement>）
     *         total - 公告总数（int）
     */
    @PostMapping("/list")
    public Result<Map<String, Object>> list(@RequestBody(required = false) Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        
        int pageNum = params.get("pageNum") == null ? 1 : Integer.parseInt(params.get("pageNum").toString());
        int pageSize = params.get("pageSize") == null ? 10 : Integer.parseInt(params.get("pageSize").toString());
        String title = (String) params.get("title");
        Integer isTop = params.get("isTop") == null ? null : Integer.parseInt(params.get("isTop").toString());

        List<Announcement> list = announcementService.getAnnouncementList(pageNum, pageSize, title, isTop);
        int total = announcementService.getAnnouncementCount(title, isTop);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        return Result.success(result);
    }

    /**
     * 公告详情接口
     * @param params 请求参数：
     *               id - 公告ID（int，必填）
     * @return 返回字段：
     *         Announcement对象，包含公告详细信息
     */
    @PostMapping("/detail")
    public Result<Announcement> detail(@RequestBody Map<String, Object> params) {
        Integer id = (Integer) params.get("id");
        if (id == null) {
            return Result.error("公告ID不能为空");
        }
        Announcement announcement = announcementService.getAnnouncementById(id);
        return announcement != null ? Result.success(announcement) : Result.error("公告不存在");
    }
} 