import request from '@/utils/request';
import type { BaseResponseData } from '@/api/base';
import { AxiosResponse } from 'axios';

// Types
export interface Announcement {
    id: number;
    title: string;
    content: string;
    createTime: string;
    updateTime: string;
}

export type AnnouncementListResponse = BaseResponseData & {
    data: Announcement[];
};

enum API {
    ANNOUNCEMENT_LIST = '/user/announcement/list',
}

export const reqAnnouncementList = () => request.get<AnnouncementListResponse, AxiosResponse<AnnouncementListResponse>>(API.ANNOUNCEMENT_LIST);
