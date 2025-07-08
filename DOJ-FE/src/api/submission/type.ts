import { BaseResponse, BasePageQueryForm } from '@/api/base';

export type Submission = {
    id: number;            // 提交记录主键
    userId: number;        // 用户ID（来源于 doj_user.user）
    problemId: number;     // 题目ID（来源于 doj_problem.problem）
    language: string;      // 编程语言
    code: string;          // 提交的代码文本内容
    exitValue?: number;    // 程序退出码
    status?: string;       // 判题状态
    message?: string;      // 判题详细信息
    time?: number;         // 运行时间（单位：秒）
    memory?: number;       // 内存使用（单位：KB）
    submitTime: string;    // 提交时间
};

export type SubmissionPageQueryForm = BasePageQueryForm & {
    userId?: number;
    problemId?: number;
    language?: string;
    status?: string;
};

export type SubmissionsPageResponseData = BaseResponse & {
    data: {
        total: number;
        pages: number;
        list: Submission[];
    },
};

export type SubmissionUserMatch = BaseResponse & {
    data: number;
};