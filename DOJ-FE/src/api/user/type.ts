import { BaseResponse } from '@/api/base'

// 登陆接口请求数据类型
export type loginForm = {
    username: string,
    password: string
};

// 登陆接口返回数据类型
type dataType = {
    accessToken: string,
    refreshToken: string,
    userId: number,
    username: string,
    avatar?: string,
    role: boolean,
};
export type loginResponseData = BaseResponse & {
    data: dataType
};

// 注册接口请求数据类型
export type registerForm = {
    username: string,
    password: string,
    email: string
    sign: string
};
// 注册接口返回数据类型
export type registerResponseData = BaseResponse & {
    data: string
};

// 头像上传接口返回数据类型
export type uploadAvatarResponseData = BaseResponse & {
    data: string
};

// 用户信息接口返回数据类型
export type userType = {
    id: number,
    username: string,
    avatar: string,
    email: string,
    password: string,
    score: number,
    ranks: number,
    school: string,
    gender: boolean,
    easySolve: number,
    middleSolve: number,
    hardSolve: number,
    role: string,
    url: string,
    sign: string,
    fans: number,
    subscribe: number,
    ban: boolean,
};

export type userInfoResponseData = BaseResponse & {
    data: userType
};

// 修改密码接口请求数据类型
export type updPwdForm = {
    id: number,
    oldPassword: string,
    newPassword: string
};