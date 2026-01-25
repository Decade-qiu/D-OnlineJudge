<template>
    <div class="login-page">
        <div class="login-container">
            <!-- Left Side - Branding -->
            <div class="branding-section">
                <div class="brand-content">
                    <div class="logo">
                        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                            <path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                            <path d="M2 12L12 17L22 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                        </svg>
                    </div>
                    <h1>Duck Online Judge</h1>
                    <p>现代化的在线编程判题平台</p>
                    <div class="features">
                        <div class="feature">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
                            </svg>
                            安全沙箱执行
                        </div>
                        <div class="feature">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/>
                            </svg>
                            实时评测反馈
                        </div>
                        <div class="feature">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <polyline points="16 18 22 12 16 6"/>
                                <polyline points="8 6 2 12 8 18"/>
                            </svg>
                            多语言支持
                        </div>
                    </div>
                </div>
            </div>

            <!-- Right Side - Form -->
            <div class="form-section">
                <div class="form-card">
                    <h2>欢迎回来</h2>
                    <p class="subtitle">登录你的账户继续编程之旅</p>

                    <el-form :model="form" :rules="rules" ref="formRef" class="login-form">
                        <el-form-item prop="username">
                            <el-input 
                                v-model="form.username" 
                                placeholder="用户名"
                                size="large"
                            >
                                <template #prefix>
                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="input-icon">
                                        <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/>
                                        <circle cx="12" cy="7" r="4"/>
                                    </svg>
                                </template>
                            </el-input>
                        </el-form-item>

                        <el-form-item prop="password">
                            <el-input 
                                v-model="form.password" 
                                type="password" 
                                placeholder="密码"
                                size="large"
                                show-password
                            >
                                <template #prefix>
                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="input-icon">
                                        <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                                        <path d="M7 11V7a5 5 0 0110 0v4"/>
                                    </svg>
                                </template>
                            </el-input>
                        </el-form-item>

                        <div class="form-options">
                            <label class="remember-me">
                                <input type="checkbox" v-model="rememberMe" />
                                <span>记住我</span>
                            </label>
                            <a href="#" class="forgot-link">忘记密码？</a>
                        </div>

                        <el-form-item>
                            <button type="button" class="submit-btn" @click="handleSubmit" :disabled="loading">
                                <span v-if="loading" class="loading-spinner"></span>
                                <span v-else>登录</span>
                            </button>
                        </el-form-item>
                    </el-form>

                    <div class="form-footer">
                        <span>还没有账户？</span>
                        <router-link to="/register" class="register-link">立即注册</router-link>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import { ElForm, ElFormItem, ElInput, ElMessage } from 'element-plus';
import { useUserStore } from '@/stores/userStore';
import { useRouter } from 'vue-router';

const userStore = useUserStore();
const router = useRouter();

const form = ref({ username: '', password: '' });
const rememberMe = ref(false);
const loading = ref(false);

type FormRef = InstanceType<typeof ElForm>;
const formRef = ref<FormRef>();

const rules = ref({
    username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
    password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
});

const handleSubmit = async () => {
    try {
        await formRef.value!.validate();
        loading.value = true;
        await userStore.getUserInfo({
            username: form.value.username,
            password: form.value.password
        });
        ElMessage.success("登录成功!");
        const redirect = sessionStorage.getItem("redirect");
        router.push(redirect || "/");
        sessionStorage.removeItem("redirect");
    } catch (error) {
        ElMessage.error("登录失败，请检查用户名或密码");
    } finally {
        loading.value = false;
    }
};
</script>

<style scoped lang="scss">


.login-page {
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: $space-lg;
}

.login-container {
    display: flex;
    max-width: 900px;
    width: 100%;
    background: var(--bg-card);
    border: 1px solid var(--border-color);
    border-radius: $radius-xl;
    overflow: hidden;
    box-shadow: var(--shadow-lg);
    
    @include mobile {
        flex-direction: column;
    }
}

// Branding Section
.branding-section {
    flex: 1;
    background: var(--primary-gradient);
    padding: $space-2xl;
    display: flex;
    align-items: center;
    justify-content: center;
    
    @include mobile {
        padding: $space-xl;
    }
}

.brand-content {
    color: white;
    text-align: center;
    
    .logo {
        width: 60px;
        height: 60px;
        margin: 0 auto $space-lg;
        
        svg {
            width: 100%;
            height: 100%;
        }
    }
    
    h1 {
        font-size: $font-size-2xl;
        font-weight: $font-weight-bold;
        margin-bottom: $space-sm;
    }
    
    p {
        opacity: 0.9;
        margin-bottom: $space-xl;
    }
}

.features {
    display: flex;
    flex-direction: column;
    gap: $space-md;
    text-align: left;
    
    .feature {
        display: flex;
        align-items: center;
        gap: $space-sm;
        font-size: $font-size-sm;
        opacity: 0.9;
        
        svg {
            width: 18px;
            height: 18px;
        }
    }
}

// Form Section
.form-section {
    flex: 1;
    padding: $space-2xl;
    display: flex;
    align-items: center;
    justify-content: center;
}

.form-card {
    width: 100%;
    max-width: 360px;
    
    h2 {
        font-size: $font-size-2xl;
        font-weight: $font-weight-bold;
        margin-bottom: $space-xs;
    }
    
    .subtitle {
        color: var(--text-secondary);
        margin-bottom: $space-xl;
    }
}

.login-form {
    .el-form-item {
        margin-bottom: $space-lg;
    }
    
    .input-icon {
        width: 18px;
        height: 18px;
        color: var(--text-muted);
    }
}

.form-options {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: $space-lg;
    
    .remember-me {
        display: flex;
        align-items: center;
        gap: $space-xs;
        font-size: $font-size-sm;
        color: var(--text-secondary);
        cursor: pointer;
        
        input {
            accent-color: var(--primary-start);
        }
    }
    
    .forgot-link {
        font-size: $font-size-sm;
        color: var(--primary-start);
        
        &:hover {
            text-decoration: underline;
        }
    }
}

.submit-btn {
    width: 100%;
    padding: $space-md;
    background: var(--primary-gradient);
    color: white;
    border: none;
    border-radius: $radius-md;
    font-size: $font-size-base;
    font-weight: $font-weight-semibold;
    cursor: pointer;
    transition: all $transition-normal;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: $space-sm;
    
    &:hover:not(:disabled) {
        box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
        transform: translateY(-2px);
    }
    
    &:disabled {
        opacity: 0.7;
        cursor: not-allowed;
    }
    
    .loading-spinner {
        width: 20px;
        height: 20px;
        border: 2px solid rgba(255,255,255,0.3);
        border-top-color: white;
        border-radius: 50%;
        animation: spin 0.8s linear infinite;
    }
}

@keyframes spin {
    to { transform: rotate(360deg); }
}

.form-footer {
    text-align: center;
    margin-top: $space-xl;
    color: var(--text-secondary);
    font-size: $font-size-sm;
    
    .register-link {
        color: var(--primary-start);
        font-weight: $font-weight-medium;
        margin-left: $space-xs;
        
        &:hover {
            text-decoration: underline;
        }
    }
}
</style>