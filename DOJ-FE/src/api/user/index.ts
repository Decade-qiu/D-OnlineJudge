// 统一管理项目接口
import request from '@/utils/request';
// import type只会导入类型，不会导入具体的值
import type { loginForm, loginResponseData, registerForm, userInfoResponseData } from './type';
enum API {
    LOGIN_URL = '/user/login',
    REGISTER_URL = '/user/register',
    USERINFO_URL = '/user/info',
};
// 暴露请求函数
export const reqLogin = (data: loginForm) => request.post<any, loginResponseData, loginForm>(API.LOGIN_URL, data);
export const reqRegister = (data: registerForm) => request.post<userInfoResponseData>(API.REGISTER_URL, data);
