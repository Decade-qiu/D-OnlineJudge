<script lang="ts" setup>
import { useUserStore } from '@/stores/userStore'
import { ElDropdown, ElDropdownMenu, ElDropdownItem, ElMessage, ElButton } from 'element-plus';
import { DropdownInstance } from 'element-plus';
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const userStore = useUserStore();
const dropdown = ref<DropdownInstance>();

const closeDropdown = (command: string) => {
    dropdown.value?.handleClose();
    router.push(command);
};

const logout = () => {
    userStore.clearUserInfo();
    window.location.href = '/login';
};

const navItems = [
    { path: '/', icon: 'fas fa-home', text: '主页' },
    { path: '/problem', icon: 'fas fa-book', text: '题库' },
    { path: '/online', icon: 'fas fa-code', text: '编码' },
    { path: '/competitions', icon: 'fas fa-trophy', text: '竞赛' },
    { path: '/status', icon: 'fas fa-tasks', text: '状态' },
    { path: '/rankings', icon: 'fas fa-chart-bar', text: '排名' },
    { path: '/about', icon: 'fas fa-info-circle', text: '关于' }
];

const isActive = (path: string) => router.currentRoute.value.path === path;
</script>

<template>
    <header class="layout-header">
        <nav>
            <div class="fixed">
                <!-- Domain link with site name -->
                <a class="domain" href="/">
                    <span>Duck Online Judge</span>
                </a>
                <!-- Navigation items -->
                <div class="nav-items">
                    <router-link
                        v-for="item in navItems"
                        :key="item.path"
                        class="nav-item"
                        :to="{ path: item.path }"
                        :class="{ active: isActive(item.path) }"
                    >
                        <i :class="item.icon"></i> {{ item.text }}
                    </router-link>
                </div>
            </div>
            <div class="var">
                <div v-if="userStore.userInfo != undefined">
                    <el-dropdown trigger="click" @command="closeDropdown" ref="dropdown" placement="bottom">
                        <div class="self-ada">
                            <el-button class="user-info">
                                <span>{{ userStore.userInfo.username }}</span>
                                <i class="fa fa-caret-down" aria-hidden="true">
                                </i>
                            </el-button>
                        </div>
                        <template #dropdown>
                            <el-dropdown-menu>
                                <el-dropdown-item command="/home">个人主页</el-dropdown-item>
                                <el-dropdown-item command="/info">修改信息</el-dropdown-item>
                                <el-dropdown-item divided @click="logout" class="logout">退出</el-dropdown-item>
                            </el-dropdown-menu>
                        </template>
                    </el-dropdown>
                </div>
                <div v-else class="auth-buttons">
                    <button class="login"><i class="fas fa-sign-in-alt"></i><router-link to="/login">登录</router-link>
                    </button>
                    <button class="register"><i class="fas fa-user-plus"></i><router-link to="/register">注册</router-link>
                    </button>
                </div>
            </div>
        </nav>
    </header>
</template>

<style scoped lang="scss">
/* Header styling */
.layout-header {
    background-color: #f7f7f7;
    /* Light background color */
    padding: 0;
    /* Remove padding */
    width: 100%;
    /* Full width */
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
    /* Subtle shadow */
    height: 60px;
    /* Fixed height for the header */
    z-index: 1000;
    position: fixed;
    top: 0;

    .self-ada {
        display: flex;
        justify-content: end;
        width: 100px;

        .user-info {
            background-color: transparent;
            border: none;
            cursor: pointer;
            font-size: 18px;
            color: #333;
            display: flex;
            align-items: center;
            justify-content: space-between;
            border-radius: 5px;
            /* 添加圆角 */
            // width: 50px;
            height: 40px;

            /* 确保内容超出时显示省略号 */
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;

            span {
                margin-right: 5px;
            }

            &:hover {
                color: #00aaff;
            }

            &:focus {
                outline: none;
                border: 2px solid #D3E6FA;
            }
        }
    }

    .fixed {
        display: flex;
        /* Flexbox layout for header */
        justify-content: space-between;
        /* Space out items */
        align-items: center;
        /* Center items vertically */
        height: 100%;
        /* Full height of the header */
        width: 840px;
    }

    .var {
        display: flex;
        justify-content: end;
        width: 220px;
    }

    /* Navigation styling */
    nav {
        display: flex;
        /* Flexbox layout for navigation */
        flex-direction: row;
        /* Horizontal layout */
        justify-content: space-between;
        /* Space out items */
        align-items: center;
        /* Center items vertically */
        padding: 0 75px;
        /* Horizontal padding */
        height: 100%;
        /* Full height of the header */
    }

    /* Domain link styling */
    .domain {
        font-size: 20px;
        /* Larger font size for site name */
        font-weight: bold;
        /* Bold text */
        color: #333;
        /* Dark text color */
        text-decoration: none;
        /* Remove underline */
    }

    /* Navigation items container styling */
    .nav-items {
        display: flex;
        /* Flexbox layout for items */
        gap: 30px;
        /* Spacing between items */
        // margin-right: 150px;
        /* Margin to align with the auth-buttons */
        margin-top: 18px;
        /* Align items vertically */
    }

    /* Individual navigation item styling */
    .nav-item {
        text-decoration: none;
        /* Remove underline */
        color: #333;
        /* Dark text color */
        font-size: 17px;
        /* Font size for items */
        position: relative;
        /* For positioning active border */
        padding-bottom: 18px;

        i {
            margin-right: 5px;
            /* Space between icon and text */
        }

        /* Active state styling */
        &.active {
            color: #2d8cf0;
            /* Active text color */
            border-bottom: 5px solid #2d8cf0;
            /* Underline effect */
            padding-bottom: 15px;
            /* Padding to position underline */
        }

        /* Hover state styling */
        &:hover {
            color: #007bff;
            /* Hover text color */
        }
    }

    /* Authentication buttons styling */
    .auth-buttons {

        button {
            margin-left: 10px;
            /* Space between buttons */
            padding: 5px 15px;
            /* Button padding */
            border-radius: 50px;
            /* Rounded corners */
            cursor: pointer;
            /* Pointer cursor on hover */

            i {
                margin-right: 5px;
                /* Space between icon and text */
            }

            /* Button hover state styling */
            &:hover {
                background-color: #0056b3;
                /* Darker background on hover */
            }
        }

        /* Login button styling */
        .login {
            border: 1px solid #5f5f60;
            /* Border color */
            background-color: #5f5f60;
            color: #fff;

            a {
                color: #fff;
            }
        }

        /* Register button styling */
        .register {
            border: 1px solid #28a745;
            /* Border color */
            background-color: #28a745;
            color: #fff;

            a {
                color: #fff;
            }
        }
    }
}
</style>