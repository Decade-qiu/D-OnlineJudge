// 统一管理项目接口
import request from '@/utils/request';
import type { ProblemsResponseData, ProblemPageQueryForm, ProblemsPageResponseData, ProblemsDetailResponseData } from './type';
import type { BaseResponseData } from '@/api/base';
import { AxiosResponse } from 'axios';

enum API {
    PROBLEMS_URL = '/problem/list',
    PROBLEMS_PAGE_URL = '/problem/page',
    PROBLEMS_detail = '/problem/',
};

// 暴露请求函数
export const reqProblemList = () => request.get<ProblemsResponseData, AxiosResponse<ProblemsResponseData>>(API.PROBLEMS_URL);
export const reqProblemPageList = (data: ProblemPageQueryForm) => request.get<ProblemsPageResponseData, AxiosResponse<ProblemsPageResponseData>>(API.PROBLEMS_PAGE_URL, {
    params: data
});
export const reqProblemDetail = (data: string) => request.get<ProblemsDetailResponseData, AxiosResponse<ProblemsDetailResponseData>>(API.PROBLEMS_detail + data);