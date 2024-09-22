// 统一管理项目接口
import request from '@/utils/request';
import type { ProblemsResponseData } from './type';
import type { BaseResponseData } from '@/api/base';
import { AxiosResponse } from 'axios';

enum API {
    PROBLEMS_URL = '/problem/list',
};

// 暴露请求函数
export const reqProblemList = () => request.get< ProblemsResponseData, AxiosResponse<ProblemsResponseData>>(API.PROBLEMS_URL);