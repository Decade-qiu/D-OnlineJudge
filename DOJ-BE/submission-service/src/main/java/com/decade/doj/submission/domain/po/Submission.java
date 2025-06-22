package com.decade.doj.submission.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 代码提交记录表
 * @TableName submission
 */
@TableName(value ="submission")
@Data
public class Submission {
    /**
     * 提交记录主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID（来源于 doj_user.user）
     */
    private Long userId;

    /**
     * 题目ID（来源于 doj_problem.problem）
     */
    private Long problemId;

    private String userName;

    private String problemName;

    /**
     * 编程语言
     */
    private String language;

    /**
     * 提交的代码文本内容
     */
    private String code;

    /**
     * 程序退出码
     */
    private Integer exitValue;

    /**
     * 判题状态
     */
    private String status;

    /**
     * 判题详细信息
     */
    private String message;

    /**
     * 运行时间（单位：秒）
     */
    private Double time;

    /**
     * 内存使用（单位：KB）
     */
    private Long memory;

    /**
     * 提交时间
     */
    private Date submitTime;
}