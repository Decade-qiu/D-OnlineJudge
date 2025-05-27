// 统一管理项目接口
import request from '@/utils/request';
import type { loginForm, loginResponseData, registerForm, userInfoResponseData, userType, updPwdForm } from './type';
import type { BaseResponseData } from '@/api/base';

enum API {
    LOGIN_URL = '/user/login',
    REGISTER_URL = '/user/register',
    USERINFO_URL = '/user/',
    USERPWD_URL = '/user/pwd',
    AVATAR_URL = '/user/avatar',
};

// 暴露请求函数
export const reqLogin = (data: loginForm) => request.post< loginResponseData>(API.LOGIN_URL, data);

export const reqRegister = (data: registerForm) => request.post<userInfoResponseData>(API.REGISTER_URL, data);

export const reqUserInfo = (id: number) => request.get<userInfoResponseData>(API.USERINFO_URL + id);

export const updUserInfo = (data: userType) => request.put<BaseResponseData>(API.USERINFO_URL, data);

export const updUserPwd = (data: updPwdForm) => request.put<BaseResponseData>(API.USERPWD_URL, data);

export const updUserAvatar = (data: FormData) => request.post<BaseResponseData>(API.AVATAR_URL, data);