package com.decade.doj.common.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 提交统计 VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionStatsVO {
    /**
     * 累计提交数
     */
    private Long totalSubmissions;

    /**
     * 今日提交数
     */
    private Long todaySubmissions;
}
