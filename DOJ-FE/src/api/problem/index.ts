// 统一管理项目接口
import request from '@/utils/request';
import type { ProblemsResponseData, ProblemPageQueryForm, ProblemsPageResponseData } from './type';
import type { BaseResponseData } from '@/api/base';
import { AxiosResponse } from 'axios';

enum API {
    PROBLEMS_URL = '/problem/list',
    PROBLEMS_PAGE_URL = '/problem/page',
};

// 暴露请求函数
export const reqProblemList = () => request.get<ProblemsResponseData, AxiosResponse<ProblemsResponseData>>(API.PROBLEMS_URL);
export const reqProblemPageList = (data: ProblemPageQueryForm) => request.get<ProblemsPageResponseData, AxiosResponse<ProblemsPageResponseData>>(API.PROBLEMS_PAGE_URL, {
    params: data
});