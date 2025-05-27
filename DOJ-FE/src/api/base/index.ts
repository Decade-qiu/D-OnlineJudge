// types.ts
export type BaseResponse = {
    code: number;
    message: string;
};

export type BaseResponseData = BaseResponse & {
    data: string
};

export type BasePageQueryForm = {
    pageNo: number,
    pageSize: number,
    isAsc: boolean,
    sortBy: string,
};