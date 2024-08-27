import { BaseResponse } from '@/api/base'

// 登陆接口请求数据类型
export type loginForm = {
    username: string,
    password: string
};

// 登陆接口返回数据类型
type dataType = {
    token: string,
    userId: number,
    username: string
};
export type loginResponseData = BaseResponse & {
    data: dataType
};

// 注册接口请求数据类型
export type registerForm = {
    username: string,
    password: string,
    email: string
    signature: string
};
// 注册接口返回数据类型
export type registerResponseData = BaseResponse & {
    data: String
};

// 用户信息接口返回数据类型
type userType = {
    userId: number,
    username: string,
    avatar: string,
    password: string,
    desc: string,
    roles: string[],
    buttons: string[],
    routers: string[],
    token: string
};
export type userInfoResponseData = BaseResponse & {
    data: userType
};