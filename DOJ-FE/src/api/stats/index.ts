import request from '@/utils/request';
import type { BaseResponseData } from '@/api/base';
import { AxiosResponse } from 'axios';

// Types
export interface Stats {
    totalSubmissions: number;
    todaySubmissions: number;
    totalProblems: number;
    activeUsers: number;
}

export type StatsResponse = BaseResponseData & {
    data: Stats;
};

enum API {
    STATS = '/user/stats',
}

export const reqStats = () => request.get<StatsResponse, AxiosResponse<StatsResponse>>(API.STATS);
