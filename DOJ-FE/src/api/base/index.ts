// types.ts
export type BaseResponse = {
    code: number;
    message: string;
};

export type BaseResponseData = BaseResponse & {
    data: string
};

