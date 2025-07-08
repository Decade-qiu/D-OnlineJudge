// 统一管理项目接口
import request from '@/utils/request';
import type { Submission, SubmissionPageQueryForm, SubmissionsPageResponseData, SubmissionUserMatch } from './type';
import type { BaseResponseData } from '@/api/base';
import { AxiosResponse } from 'axios';

enum API {
    SUBMISSIONS_LIST = '/submission/list',
    SUBMISSIONS_PAGE = '/submission/page',
    SUBMISSION_DETAIL = '/submission/',
    SUBMISSION_USER_PROBLEM = '/submission/match/',
};

// 请求提交列表（非分页）
export const reqSubmissionList = () =>
    request.get<SubmissionsPageResponseData, AxiosResponse<SubmissionsPageResponseData>>(API.SUBMISSIONS_LIST);

// 请求提交分页列表
export const reqSubmissionPageList = (data: SubmissionPageQueryForm) =>
    request.get<SubmissionsPageResponseData, AxiosResponse<SubmissionsPageResponseData>>(API.SUBMISSIONS_PAGE, {
        params: data
    });

// 请求用户提交的题目
export const reqSubmissionUserProblem = (id: number) =>
    request.get<SubmissionUserMatch, AxiosResponse<SubmissionUserMatch>>(API.SUBMISSION_USER_PROBLEM + id);