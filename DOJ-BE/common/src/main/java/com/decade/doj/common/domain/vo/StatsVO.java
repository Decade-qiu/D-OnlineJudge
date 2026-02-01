package com.decade.doj.common.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 首页统计数据 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatsVO {
    /**
     * 累计提交数
     */
    private Long totalSubmissions;

    /**
     * 今日提交数
     */
    private Long todaySubmissions;

    /**
     * 题目总数
     */
    private Long totalProblems;

    /**
     * 活跃用户数（有提交记录的用户）
     */
    private Long activeUsers;
}
