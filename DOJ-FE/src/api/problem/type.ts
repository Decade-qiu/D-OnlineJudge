import { BaseResponse } from '@/api/base'

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

export type ProblemsResponseData = BaseResponse & {
    data: ProblemType[],
};