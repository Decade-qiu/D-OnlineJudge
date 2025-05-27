import { BaseResponse, BasePageQueryForm } from '@/api/base';

export type ProblemPageQueryForm = BasePageQueryForm & {
    name: string,
    difficulty: string,
    tags: string,
    status: number,
};

export type ProblemType = {
    id: number;
    name: string;
    inputStyle: string;
    outputStyle: string;
    dataRange: string;
    inputSample: string;
    outputSample: string;
    difficulty: string;
    timeLimit: number;
    memoryLimit: number;
    description: string;
    totalPass: number;
    totalAttempt: number;
    tag: string;
};

export type ProblemsPageResponseData = BaseResponse & {
    data: {
        total: number,
        pages: number,
        list: ProblemType[],
    },
};

export type ProblemsResponseData = BaseResponse & {
    data: ProblemType[],
};

export type ProblemsDetailResponseData = BaseResponse & {
    data: ProblemType,
};