import { BaseResponse, BasePageQueryForm } from '@/api/base';

export type ProblemPageQueryForm = BasePageQueryForm & {
    name?: string,
    description?: string,
    difficulty?: string,
    tags?: string[],
    status?: string,
};

export type ProblemType = {
    id: number;
    name: string;
    description: string;
    inputStyle: string;
    outputStyle: string;
    hint: string;
    inputSample: string[];
    outputSample: string[];
    difficulty: string;
    timeLimit: number;
    memoryLimit: number;
    totalPass: number;
    totalAttempt: number;
    tags: string[];
    status?: string;
    testData?: string;
    testAns?: string;
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
