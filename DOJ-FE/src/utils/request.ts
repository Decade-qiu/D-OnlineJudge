// 二次封装axios：使用请求与响应拦截器
import axios, { type InternalAxiosRequestConfig } from "axios";
import { ElMessage } from "element-plus";
import { useUserStore } from "@/stores/userStore";
import router from "@/router";
// 创建一个专门用于刷新 token 的、不带任何拦截器的 axios 实例
const refreshTokenRequest = axios.create({
    baseURL: import.meta.env.VITE_APP_URL,
    timeout: 50000,
});
// 创建主要的axios实例
const request = axios.create({
    baseURL: import.meta.env.VITE_APP_URL,
    timeout: 50000,
});
// 请求拦截器
request.interceptors.request.use((config) => {
    const userStore = useUserStore();
    const accessToken = userStore.userInfo?.accessToken;
    if (accessToken) {
        config.headers.Authorization = accessToken;
    }
    return config;
});

// --- 静默刷新逻辑 ---
let isRefreshing = false;
// 存储因 token 过期而被挂起的请求
let requests: ((token: string) => void)[] = [];

// 响应拦截器
request.interceptors.response.use(
    (response) => {
        return response;
    },
    async (error) => {
        const { response, config } = error;
        const userStore = useUserStore();

        // 只有当响应是 401 时才执行刷新逻辑
        if (response && response.status === 401) {
            // 如果正在刷新中，则将当前失败的请求挂起
            if (isRefreshing) {
                return new Promise((resolve) => {
                    requests.push((token: string) => {
                        // 当刷新成功后，用新的 token 重新发起请求
                        (config as InternalAxiosRequestConfig).headers.Authorization = token;
                        resolve(request(config));
                    });
                });
            }

            isRefreshing = true;

            try {
                // 使用独立的 axios 实例来刷新 token，避免循环拦截
                const res = await refreshTokenRequest.post('/user/refresh', {}, {
                    headers: {
                        Authorization: userStore.userInfo?.refreshToken || ''
                    }
                });

                const newAccessToken = res.data.data;
                // 1. 更新 Pinia 和当前请求的 accessToken
                userStore.setAccessToken(newAccessToken);
                (config as InternalAxiosRequestConfig).headers.Authorization = newAccessToken;

                // 2. 重试队列中所有挂起的请求
                requests.forEach((cb) => cb(newAccessToken));
                requests = []; // 清空队列

                // 3. 重试本次失败的请求
                return request(config);

            } catch (refreshError) {
                // 如果刷新令牌也失败了（例如 refresh_token 也过期了），则清除所有信息并跳转登录页
                userStore.clearUserInfo();
                router.push('/login');
                ElMessage.error('登录已过期，请重新登录');
                return Promise.reject(refreshError);
            } finally {
                isRefreshing = false;
            }
        }

        // 处理其他网络错误
        const error_info = response?.data || {};
        const msg = error_info.msg || '网络错误，请稍后再试';
        ElMessage({
            type: 'error',
            message: msg,
        });

        return Promise.reject(error);
    }
);

export default request;