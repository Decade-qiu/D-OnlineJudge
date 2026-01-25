package com.decade.doj.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.decade.doj.user.domain.po.Announcement;
import com.decade.doj.user.mapper.AnnouncementMapper;
import com.decade.doj.user.service.IAnnouncementService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 公告表 服务实现类
 * </p>
 *
 * @author Antigravity
 * @since 2026-01-24
 */
@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements IAnnouncementService {

}
