import { BaseResponse } from '@/api/base'

export type submitForm = {
    file: File,
    language: string,
};

type executeMessage = {
    exitValue: number,
    message: string,
    time: number,
    memory: number,
};

export type executeResponseData = BaseResponse & {
    data: executeMessage,
};