package com.decade.doj.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.decade.doj.common.annotation.AdminRequired;
import com.decade.doj.common.domain.R;
import com.decade.doj.user.domain.po.Announcement;
import com.decade.doj.user.service.IAnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/user/announcement")
@Tag(name = "公告接口")
@RequiredArgsConstructor
public class AnnouncementController {

    private final IAnnouncementService announcementService;

    @GetMapping("/list")
    @Operation(summary = "获取公告列表")
    public R<List<Announcement>> list() {
        return R.ok(announcementService.list(new LambdaQueryWrapper<Announcement>()
                .eq(Announcement::getDeleted, false)
                .orderByDesc(Announcement::getCreateTime)));
    }

    // Admin endpoints (simplified, assuming standard auth/admin check or internal use if gateway handles it)
    // For now, I'll allow creation to support the requirement
    
    @PostMapping
    @AdminRequired
    @Operation(summary = "新增公告")
    public R<Void> create(@RequestBody Announcement announcement) {
        announcement.setCreateTime(LocalDateTime.now());
        announcement.setUpdateTime(LocalDateTime.now());
        announcementService.save(announcement);
        return R.ok();
    }
}
