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
@RequestMapping("/api/admin/announcement")
public class AdminAnnouncementController {
    @Autowired
    private AnnouncementService announcementService;

    /**
     * 新增公告接口
     * @param params 请求参数：
     *               title - 公告标题（String，必填）
     *               content - 公告内容（String，必填）
     * @return 返回：发布成功/发布失败（String）
     */
    @PostMapping("/add")
    public Result<String> add(@RequestBody Map<String, Object> params) {
        String title = (String) params.get("title");
        String content = (String) params.get("content");
        boolean success = announcementService.addAnnouncement(title, content);
        return success ? Result.success("发布成功") : Result.error("发布失败");
    }

    /**
     * 删除公告接口
     * @param params 请求参数：
     *               id - 公告ID（int，必填）
     * @return 返回：删除成功/删除失败（String）
     */
    @PostMapping("/delete")
    public Result<String> delete(@RequestBody Map<String, Object> params) {
        Integer id = (Integer) params.get("id");
        boolean success = announcementService.deleteAnnouncement(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 修改公告接口
     * @param announcement 请求体参数：公告对象，包含id、title、content等
     * @return 返回：修改成功/修改失败（String）
     */
    @PostMapping("/update")
    public Result<String> update(@RequestBody Announcement announcement) {
        boolean success = announcementService.updateAnnouncement(announcement);
        return success ? Result.success("修改成功") : Result.error("修改失败");
    }

    /**
     * 置顶/取消置顶公告接口
     * @param params 请求参数：
     *               id - 公告ID（int，必填）
     *               isTop - 是否置顶（int，1为置顶，0为取消置顶，必填）
     * @return 返回：操作成功/操作失败（String）
     */
    @PostMapping("/top")
    public Result<String> top(@RequestBody Map<String, Object> params) {
        Integer id = (Integer) params.get("id");
        Integer isTop = (Integer) params.get("isTop");
        boolean success = announcementService.updateAnnouncementTop(id, isTop);
        return success ? Result.success("操作成功") : Result.error("操作失败");
    }

    /**
     * 分页查询公告列表
     * @param params 请求参数：
     *              pageNum - 页码（int，选填，默认1）
     *              pageSize - 每页条数（int，选填，默认10）
     *              title - 标题关键词（String，选填）
     *              isTop - 是否置顶（Integer，选填）
     * @return 返回：分页数据，包含总数和列表
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
        result.put("total", total);
        result.put("list", list);
        
        return Result.success(result);
    }

    /**
     * 查询公告详情
     * @param params 请求参数：
     *              id - 公告ID（int，必填）
     * @return 返回：公告详情对象
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

    /**
     * 获取公告统计数据
     * @return 返回：
     *         total - 公告总数
     *         topCount - 置顶公告数量
     *         normalCount - 未置顶公告数量
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = announcementService.getAnnouncementStats();
        return Result.success(stats);
    }
} 