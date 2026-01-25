<script lang="ts" setup>
import { useUserStore } from '@/stores/userStore'
import { ElDropdown, ElDropdownMenu, ElDropdownItem, ElAvatar, ElIcon } from 'element-plus';
import { Moon, Sunny } from '@element-plus/icons-vue';
import { DropdownInstance } from 'element-plus';
import { ref, computed } from 'vue';
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
    { path: '/', icon: 'home', text: '首页' },
    { path: '/problem', icon: 'book', text: '题库' },
    { path: '/online', icon: 'code', text: '编码' },
    { path: '/status', icon: 'activity', text: '状态' },
    { path: '/rankings', icon: 'trophy', text: '排名' },
];

const isActive = (path: string) => {
    if (path === '/') {
        return router.currentRoute.value.path === '/';
    }
    return router.currentRoute.value.path.startsWith(path);
};

const userInitials = computed(() => {
    if (userStore.userInfo?.username) {
        return userStore.userInfo.username.charAt(0).toUpperCase();
    }
    return 'U';
});

// Theme Management
const isDark = ref(localStorage.getItem('theme') !== 'light');

const toggleTheme = () => {
    isDark.value = !isDark.value;
    const theme = isDark.value ? 'dark' : 'light';
    document.documentElement.setAttribute('data-theme', theme);
    localStorage.setItem('theme', theme);
};

// Initialize theme
if (!isDark.value) {
    document.documentElement.setAttribute('data-theme', 'light');
}

</script>

<template>
    <header class="navbar">
        <nav class="navbar-inner">
            <!-- Logo -->
            <router-link to="/" class="logo">
                <div class="logo-icon">
                    <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="url(#gradient)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                        <path d="M2 17L12 22L22 17" stroke="url(#gradient)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                        <path d="M2 12L12 17L22 12" stroke="url(#gradient)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                        <defs>
                            <linearGradient id="gradient" x1="2" y1="2" x2="22" y2="22">
                                <stop stop-color="#667eea"/>
                                <stop offset="1" stop-color="#764ba2"/>
                            </linearGradient>
                        </defs>
                    </svg>
                </div>
                <span class="logo-text">DOJ</span>
            </router-link>

            <!-- Navigation Items -->
            <div class="nav-items">
                <router-link
                    v-for="item in navItems"
                    :key="item.path"
                    class="nav-item"
                    :to="item.path"
                    :class="{ active: isActive(item.path) }"
                >
                    <span class="nav-icon">
                        <!-- Home icon -->
                        <svg v-if="item.icon === 'home'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"/>
                            <polyline points="9 22 9 12 15 12 15 22"/>
                        </svg>
                        <!-- Book icon -->
                        <svg v-if="item.icon === 'book'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M4 19.5A2.5 2.5 0 016.5 17H20"/>
                            <path d="M6.5 2H20v20H6.5A2.5 2.5 0 014 19.5v-15A2.5 2.5 0 016.5 2z"/>
                        </svg>
                        <!-- Code icon -->
                        <svg v-if="item.icon === 'code'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <polyline points="16 18 22 12 16 6"/>
                            <polyline points="8 6 2 12 8 18"/>
                        </svg>
                        <!-- Activity icon -->
                        <svg v-if="item.icon === 'activity'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/>
                        </svg>
                        <!-- Trophy icon -->
                        <svg v-if="item.icon === 'trophy'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M6 9H4.5a2.5 2.5 0 010-5H6"/>
                            <path d="M18 9h1.5a2.5 2.5 0 000-5H18"/>
                            <path d="M4 22h16"/>
                            <path d="M10 14.66V17c0 .55-.47.98-.97 1.21C7.85 18.75 7 20.24 7 22"/>
                            <path d="M14 14.66V17c0 .55.47.98.97 1.21C16.15 18.75 17 20.24 17 22"/>
                            <path d="M18 2H6v7a6 6 0 0012 0V2z"/>
                        </svg>
                    </span>
                    <span class="nav-text">{{ item.text }}</span>
                </router-link>
            </div>

            <!-- User Area -->
            <div class="user-area">
                <template v-if="userStore.userInfo">
                    <el-dropdown trigger="click" @command="closeDropdown" ref="dropdown" placement="bottom-end" popper-class="user-dropdown-popper">
                        <div class="user-button">
                            <el-avatar 
                                :size="36" 
                                :src="userStore.userInfo.avatar ? '/api' + userStore.userInfo.avatar : ''"
                                class="user-avatar"
                            >
                                {{ userInitials }}
                            </el-avatar>
                            <span class="user-name">{{ userStore.userInfo.username }}</span>
                            <svg class="dropdown-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <polyline points="6 9 12 15 18 9"/>
                            </svg>
                        </div>
                        <template #dropdown>
                            <el-dropdown-menu class="user-dropdown">
                                <!-- Admin Entry -->
                                <el-dropdown-item command="/admin" v-if="!userStore.userInfo.role">
                                    <div class="dropdown-item-content">
                                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5"/>
                                        </svg>
                                        <span>后台管理</span>
                                    </div>
                                </el-dropdown-item>
                                <el-dropdown-item divided command="/home">
                                    <div class="dropdown-item-content">
                                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/>
                                            <circle cx="12" cy="7" r="4"/>
                                        </svg>
                                        <span>个人主页</span>
                                    </div>
                                </el-dropdown-item>
                                <el-dropdown-item command="/info">
                                    <div class="dropdown-item-content">
                                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <circle cx="12" cy="12" r="3"/>
                                            <path d="M19.4 15a1.65 1.65 0 00.33 1.82l.06.06a2 2 0 010 2.83 2 2 0 01-2.83 0l-.06-.06a1.65 1.65 0 00-1.82-.33 1.65 1.65 0 00-1 1.51V21a2 2 0 01-2 2 2 2 0 01-2-2v-.09A1.65 1.65 0 009 19.4a1.65 1.65 0 00-1.82.33l-.06.06a2 2 0 01-2.83 0 2 2 0 010-2.83l.06-.06a1.65 1.65 0 00.33-1.82 1.65 1.65 0 00-1.51-1H3a2 2 0 01-2-2 2 2 0 012-2h.09A1.65 1.65 0 004.6 9a1.65 1.65 0 00-.33-1.82l-.06-.06a2 2 0 010-2.83 2 2 0 012.83 0l.06.06a1.65 1.65 0 001.82.33H9a1.65 1.65 0 001-1.51V3a2 2 0 012-2 2 2 0 012 2v.09a1.65 1.65 0 001 1.51 1.65 1.65 0 001.82-.33l.06-.06a2 2 0 012.83 0 2 2 0 010 2.83l-.06.06a1.65 1.65 0 00-.33 1.82V9a1.65 1.65 0 001.51 1H21a2 2 0 012 2 2 2 0 01-2 2h-.09a1.65 1.65 0 00-1.51 1z"/>
                                        </svg>
                                        <span>设置</span>
                                    </div>
                                </el-dropdown-item>
                                <el-dropdown-item>
                                    <div class="dropdown-item-content theme-switch" @click.stop="toggleTheme">
                                        <svg v-if="isDark" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"></path>
                                        </svg>
                                        <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <circle cx="12" cy="12" r="5"></circle>
                                            <line x1="12" y1="1" x2="12" y2="3"></line>
                                            <line x1="12" y1="21" x2="12" y2="23"></line>
                                            <line x1="4.22" y1="4.22" x2="5.64" y2="5.64"></line>
                                            <line x1="18.36" y1="18.36" x2="19.78" y2="19.78"></line>
                                            <line x1="1" y1="12" x2="3" y2="12"></line>
                                            <line x1="21" y1="12" x2="23" y2="12"></line>
                                            <line x1="4.22" y1="19.78" x2="5.64" y2="18.36"></line>
                                            <line x1="18.36" y1="5.64" x2="19.78" y2="4.22"></line>
                                        </svg>
                                        <span>{{ isDark ? '暗色模式' : '亮色模式' }}</span>
                                    </div>
                                </el-dropdown-item>
                                <el-dropdown-item divided @click="logout" class="logout-item">
                                    <div class="dropdown-item-content">
                                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <path d="M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4"/>
                                            <polyline points="16 17 21 12 16 7"/>
                                            <line x1="21" y1="12" x2="9" y2="12"/>
                                        </svg>
                                        <span>退出登录</span>
                                    </div>
                                </el-dropdown-item>
                            </el-dropdown-menu>
                        </template>
                    </el-dropdown>
                </template>
                <template v-else>
                    <router-link to="/login" class="auth-btn login-btn">
                        登录
                    </router-link>
                    <router-link to="/register" class="auth-btn register-btn">
                        注册
                    </router-link>
                </template>
            </div>
        </nav>
    </header>
</template>

<style scoped lang="scss">


.navbar {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    height: $navbar-height;
    z-index: $z-fixed;
    @include glass;
    border-bottom: 1px solid var(--border-color);
}

.navbar-inner {
    max-width: $container-max;
    height: 100%;
    margin: 0 auto;
    padding: 0 $container-padding;
    display: flex;
    align-items: center;
    justify-content: space-between;
}

// Logo
.logo {
    display: flex;
    align-items: center;
    gap: $space-sm;
    text-decoration: none;
    
    .logo-icon {
        width: 32px;
        height: 32px;
        
        svg {
            width: 100%;
            height: 100%;
        }
    }
    
    .logo-text {
        font-size: $font-size-xl;
        font-weight: $font-weight-bold;
        @include gradient-text;
    }
}

// Navigation
.nav-items {
    display: flex;
    align-items: center;
    gap: $space-xs;
}

.nav-item {
    display: flex;
    align-items: center;
    gap: $space-sm;
    padding: $space-sm $space-md;
    color: var(--text-secondary);
    text-decoration: none;
    border-radius: $radius-md;
    transition: all $transition-fast;
    position: relative;
    
    .nav-icon {
        width: 18px;
        height: 18px;
        display: flex;
        align-items: center;
        justify-content: center;
        
        svg {
            width: 100%;
            height: 100%;
        }
    }
    
    .nav-text {
        font-size: $font-size-sm;
        font-weight: $font-weight-medium;
    }
    
    &:hover {
        color: var(--text-primary);
        background: var(--glass-bg);
    }
    
    &.active {
        color: var(--primary-start);
        background: rgba(102, 126, 234, 0.1);
        
        &::after {
            content: '';
            position: absolute;
            bottom: -1px;
            left: 50%;
            transform: translateX(-50%);
            width: 24px;
            height: 2px;
            background: var(--primary-gradient);
            border-radius: $radius-full;
        }
    }
}

// User Area
.user-area {
    display: flex;
    align-items: center;
    gap: $space-md;
}

.user-button {
    display: flex;
    align-items: center;
    gap: $space-sm;
    padding: $space-xs $space-sm;
    border-radius: $radius-full;
    cursor: pointer;
    transition: all $transition-fast;
    
    &:hover {
        background: var(--glass-bg);
    }
    
    .user-avatar {
        border: 2px solid var(--border-light);
        background: var(--primary-gradient);
        color: white;
        font-weight: $font-weight-semibold;
    }
    
    .user-name {
        font-size: $font-size-sm;
        font-weight: $font-weight-medium;
        color: var(--text-primary);
        max-width: 100px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
    }
    
    .dropdown-arrow {
        width: 16px;
        height: 16px;
        color: var(--text-muted);
        transition: transform $transition-fast;
    }
}

// Auth Buttons
.auth-btn {
    padding: $space-sm $space-lg;
    font-size: $font-size-sm;
    font-weight: $font-weight-medium;
    border-radius: $radius-full;
    text-decoration: none;
    transition: all $transition-fast;
}

.login-btn {
    color: var(--text-primary);
    
    &:hover {
        background: var(--glass-bg);
    }
}

.register-btn {
    background: var(--primary-gradient);
    color: white;
    
    &:hover {
        box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
        transform: translateY(-1px);
    }
}



// Dropdown Menu Styles
:deep(.user-dropdown) {
    min-width: 180px;
    padding: $space-xs;
    background: var(--bg-elevated);
    border: 1px solid var(--border-color);
    box-shadow: var(--shadow-lg);
    border-radius: $radius-md;
    
    .el-dropdown-menu__item {
        padding: $space-sm;
        border-radius: $radius-sm;
        color: var(--text-primary);
        
        &:hover, &:focus {
            background-color: var(--bg-primary);
            color: var(--primary-start);
        }
        
        &.logout-item {
            .dropdown-item-content {
                color: $danger;
            }
            
            &:hover {
                background: $danger-light !important;
            }
        }
        
        &.is-divided {
            border-top: 1px solid var(--border-color);
            margin-top: $space-xs;
        }
    }
}

.dropdown-item-content {
    display: flex;
    align-items: center;
    gap: $space-sm;
    width: 100%;
    
    svg {
        width: 16px;
        height: 16px;
    }
    
    &.theme-switch {
        cursor: pointer;
        justify-content: flex-start;
    }
}

// Mobile Responsive
@include mobile {
    .nav-text {
        display: none;
    }
    
    .nav-item {
        padding: $space-sm;
    }
    
    .user-name {
        display: none;
    }
    
    .logo-text {
        display: none;
    }
}
</style>

<style lang="scss">
// Global styles for dropdown popper
.user-dropdown-popper {
    &.el-popper {
        border: 1px solid var(--border-color) !important;
        background: var(--bg-elevated) !important;
    }

    .el-popper__arrow::before {
        background: var(--bg-elevated) !important;
        border: 1px solid var(--border-color) !important;
    }
}
</style>