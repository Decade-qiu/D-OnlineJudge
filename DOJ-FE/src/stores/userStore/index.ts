// 管理用户数据相关

import { defineStore } from 'pinia'
import { ref } from 'vue'
import { reqLogin } from '@/api/user'
import { loginForm, loginResponseData } from '@/api/user/type'

export const useUserStore = defineStore('user', () => {
    // 1定义管理用户数据的state
    const userInfo = ref<loginResponseData['data'] | undefined>();
    // 定义获取接口数据的action函数
    const getUserInfo = async (form:loginForm) => {
        const res = await reqLogin(form);
        userInfo.value = res.data;
    }
    // 退出时清除用户信息
    const clearUserInfo = () => {
        userInfo.value = undefined;
    }
    // 以对象的格式把state和action return
    return {
        userInfo,
        getUserInfo,
        clearUserInfo
    }
}, {
    persist: true,
})