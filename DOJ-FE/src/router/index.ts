// createRouter：创建router实例对象
// createWebHistory：创建history模式的路由

import { createRouter, createWebHistory } from 'vue-router'

import NProgress from 'nprogress';
import 'nprogress/nprogress.css';

import Layout from '@/views/Layout/index.vue'
import Home from '@/views/Home/index.vue'
import Login from '@/views/User/login/index.vue'
import Register from '@/views/User/register/index.vue'
import Info from '@/views/User/info/index.vue'
import UserHome from '@/views/User/home/index.vue'
import OnlineEditor from '@/views/OnlineEditor/index.vue'
import Problem from '@/views/Problem/index.vue'
import ProblemDetail from '@/views/Problem/detail/index.vue'
import Status from '@/views/Status/index.vue'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    // path和component对应关系的位置
    routes: [
        {
            path: '/',
            component: Layout,
            children: [
                {
                    path: '',
                    component: Home
                },{
                    path: '/login',
                    component: Login
                },{
                    path: '/register',
                    component: Register
                },{
                    path: '/info',
                    component: Info
                },{
                    path: '/home',
                    component: UserHome
                },{
                    path: '/online',
                    component: OnlineEditor
                },{
                    path: '/problem',
                    component: Problem
                },{
                    path: '/problem/:id',
                    component: ProblemDetail
                },{
                    path: '/status',
                    component: Status
                },{
                    path: '/rankings',
                    component: () => import('@/views/Rankings/index.vue')
                }
            ]
        }
    ],
    // 路由滚动行为定制
    scrollBehavior() {
        return {
            top: 0
        }
    }
});

// 在路由导航前，启动进度条
router.beforeEach((to, from, next) => {
    NProgress.start();
    if (to.path === '/login') {
        sessionStorage.setItem('redirect', from.path);
    }
    next();
});

// 在路由导航结束后，关闭进度条
router.afterEach(() => {
    NProgress.done();
});

export default router
