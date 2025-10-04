// 管理用户数据相关

import { defineStore } from 'pinia';
import { ref } from 'vue';
import { reqLogin } from '@/api/user';
import { loginForm, loginResponseData } from '@/api/user/type';

// 定义用户信息的接口类型
interface UserInfo {
    userId: number;
    username: string;
    accessToken: string;
    refreshToken: string;
}

export const useUserStore = defineStore('user', () => {
    const userInfo = ref<UserInfo | undefined>();

    const getUserInfo = async (form: loginForm) => {
        const res = await reqLogin(form);
        userInfo.value = res.data.data;
    }; 

    const clearUserInfo = () => {
        userInfo.value = undefined;
    };

    // 新增一个 action，只用于更新 accessToken
    const setAccessToken = (token: string) => {
        if (userInfo.value) {
            userInfo.value.accessToken = token;
        }
    };

    // 以对象的格式把state和action return
    return {
        userInfo,
        getUserInfo,
        clearUserInfo,
        setAccessToken, // 暴露新的 action
    };
}, {
    persist: true,
});