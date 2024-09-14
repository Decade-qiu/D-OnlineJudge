// 统一管理项目接口
import request from '@/utils/request';
import type { submitForm, executeResponseData } from './type';
import type { BaseResponseData } from '@/api/base';
import { AxiosResponse } from 'axios';

enum API {
    SUBMIT_URL = '/sandbox/code',
};

// 暴露请求函数
export const reqSubmit = (data: FormData) => request.post< executeResponseData, AxiosResponse<executeResponseData>>(API.SUBMIT_URL, data);
