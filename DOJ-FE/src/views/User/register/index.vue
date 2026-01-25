<template>
    <div class="register-page">
        <div class="register-container">
            <!-- Left Side - Branding -->
            <div class="branding-section">
                <div class="brand-content">
                    <div class="logo">
                        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2"/>
                            <path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="2"/>
                            <path d="M2 12L12 17L22 12" stroke="currentColor" stroke-width="2"/>
                        </svg>
                    </div>
                    <h1>Duck Online Judge</h1>
                    <p>加入我们，开启你的编程之旅</p>
                    <div class="stats">
                        <div class="stat">
                            <span class="value">1000+</span>
                            <span class="label">题目</span>
                        </div>
                        <div class="stat">
                            <span class="value">5000+</span>
                            <span class="label">用户</span>
                        </div>
                        <div class="stat">
                            <span class="value">50万+</span>
                            <span class="label">提交</span>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Right Side - Form -->
            <div class="form-section">
                <div class="form-card">
                    <h2>创建账户</h2>
                    <p class="subtitle">填写以下信息完成注册</p>

                    <el-form :model="form" :rules="rules" ref="formRef" class="register-form">
                        <el-form-item prop="username">
                            <el-input v-model="form.username" placeholder="用户名" size="large">
                                <template #prefix>
                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="input-icon">
                                        <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/>
                                        <circle cx="12" cy="7" r="4"/>
                                    </svg>
                                </template>
                            </el-input>
                        </el-form-item>

                        <el-form-item prop="email">
                            <el-input v-model="form.email" placeholder="邮箱" size="large">
                                <template #prefix>
                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="input-icon">
                                        <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
                                        <polyline points="22,6 12,13 2,6"/>
                                    </svg>
                                </template>
                            </el-input>
                        </el-form-item>

                        <el-form-item prop="password">
                            <el-input v-model="form.password" type="password" placeholder="密码" size="large" show-password>
                                <template #prefix>
                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="input-icon">
                                        <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                                        <path d="M7 11V7a5 5 0 0110 0v4"/>
                                    </svg>
                                </template>
                            </el-input>
                        </el-form-item>

                        <el-form-item prop="sign">
                            <el-input v-model="form.sign" placeholder="个性签名（可选）" size="large">
                                <template #prefix>
                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="input-icon">
                                        <path d="M12 20h9"/>
                                        <path d="M16.5 3.5a2.121 2.121 0 013 3L7 19l-4 1 1-4L16.5 3.5z"/>
                                    </svg>
                                </template>
                            </el-input>
                        </el-form-item>

                        <el-form-item>
                            <button type="button" class="submit-btn" @click="handleSubmit" :disabled="loading">
                                <span v-if="loading" class="loading-spinner"></span>
                                <span v-else>注册</span>
                            </button>
                        </el-form-item>
                    </el-form>

                    <div class="form-footer">
                        <span>已有账户？</span>
                        <router-link to="/login" class="login-link">立即登录</router-link>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import { ElForm, ElFormItem, ElInput, ElMessage, FormRules } from 'element-plus';
import { reqRegister } from '@/api/user';
import { useRouter } from 'vue-router';

const router = useRouter();
const loading = ref(false);

type ElFormInstance = InstanceType<typeof ElForm>;
const formRef = ref<ElFormInstance>();

interface FormType {
    username: string;
    password: string;
    email: string;
    sign: string;
}

const form = ref<FormType>({
    username: '',
    password: '',
    email: '',
    sign: ''
});

const rules = ref<FormRules<FormType>>({
    username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 20, message: '密码长度在 6 到 20 个字符之间', trigger: 'blur' }
    ],
    email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
    ]
});

const handleSubmit = async () => {
    try {
        await formRef.value!.validate();
        loading.value = true;
        await reqRegister(form.value);
        ElMessage.success("注册成功!");
        router.push('/login');
    } catch (error) {
        ElMessage.error("注册失败，请检查输入");
    } finally {
        loading.value = false;
    }
};
</script>

<style scoped lang="scss">


.register-page {
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: $space-lg;
}

.register-container {
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

.branding-section {
    flex: 1;
    background: var(--primary-gradient);
    padding: $space-2xl;
    display: flex;
    align-items: center;
    justify-content: center;
}

.brand-content {
    color: white;
    text-align: center;
    
    .logo {
        width: 60px;
        height: 60px;
        margin: 0 auto $space-lg;
        svg { width: 100%; height: 100%; }
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

.stats {
    display: flex;
    justify-content: center;
    gap: $space-xl;
    
    .stat {
        display: flex;
        flex-direction: column;
        
        .value {
            font-size: $font-size-xl;
            font-weight: $font-weight-bold;
        }
        
        .label {
            font-size: $font-size-xs;
            opacity: 0.8;
        }
    }
}

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

.register-form {
    .el-form-item { margin-bottom: $space-md; }
    .input-icon { width: 18px; height: 18px; color: var(--text-muted); }
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
    
    &:hover:not(:disabled) {
        box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
        transform: translateY(-2px);
    }
    
    &:disabled { opacity: 0.7; cursor: not-allowed; }
    
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
    
    .login-link {
        color: var(--primary-start);
        font-weight: $font-weight-medium;
        margin-left: $space-xs;
        &:hover { text-decoration: underline; }
    }
}
</style>
