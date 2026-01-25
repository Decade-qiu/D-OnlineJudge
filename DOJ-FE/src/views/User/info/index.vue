<template>
    <div class="settings-page">
        <div class="settings-container card-glass">
            <!-- Sidebar -->
            <div class="settings-sidebar">
                <div class="user-preview">
                    <!-- <el-avatar :size="80" :src="form.avatar ? '/api' + form.avatar : ''" class="preview-avatar">
                        {{ form.username?.charAt(0).toUpperCase() }}
                    </el-avatar> -->
                    <el-upload ref="upload" :action="avatarURL" :show-file-list="false" :on-success="handleSuccess"
                        :before-upload="beforeUpload" :headers="uploadHeaders" class="avatar-uploader">
                        <img v-if="imageUrl" :src="imageUrl" class="preview-avatar" />
                        <el-avatar v-else :size="80" class="preview-avatar">
                            {{ form.username?.charAt(0).toUpperCase() }}
                        </el-avatar>
                        <div class="upload-mask">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"></path>
                                <circle cx="12" cy="13" r="4"></circle>
                            </svg>
                        </div>
                    </el-upload>
                    <span class="preview-name">{{ form.username }}</span>
                </div>
                <nav class="nav-menu">
                    <button :class="['nav-item', { active: activeTab === 0 }]" @click="activeTab = 0">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2" />
                            <circle cx="12" cy="7" r="4" />
                        </svg>
                        个人资料
                    </button>
                    <button :class="['nav-item', { active: activeTab === 1 }]" @click="activeTab = 1">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
                            <path d="M7 11V7a5 5 0 0110 0v4" />
                        </svg>
                        修改密码
                    </button>
                </nav>
            </div>

            <!-- Content -->
            <div class="settings-content">
                <!-- Profile Tab -->
                <transition name="fade" mode="out-in">
                    <div v-if="activeTab === 0" key="profile" class="tab-content">
                        <div class="tab-header">
                            <h2 class="tab-title">个人资料</h2>
                            <p class="tab-desc">更新你的个人信息</p>
                        </div>

                        <el-form :model="form" :rules="rules" ref="formRef" class="settings-form" label-position="top">
                            <div class="form-grid">
                                <el-form-item prop="username" label="用户名">
                                    <div class="input-wrapper">
                                        <el-input v-model="form.username" placeholder="输入用户名" />
                                    </div>
                                </el-form-item>
                                <el-form-item prop="gender" label="性别">
                                    <el-select v-model="form.gender" placeholder="选择性别" class="full-width">
                                        <el-option label="男" :value="true" />
                                        <el-option label="女" :value="false" />
                                    </el-select>
                                </el-form-item>
                                <el-form-item prop="school" label="学校">
                                    <el-input v-model="form.school" placeholder="输入学校名称" />
                                </el-form-item>
                                <el-form-item prop="email" label="邮箱">
                                    <el-input v-model="form.email" placeholder="email@example.com" disabled />
                                </el-form-item>
                                <el-form-item prop="url" label="个人主页" class="full-span">
                                    <el-input v-model="form.url" placeholder="https://your-website.com" />
                                </el-form-item>
                                <el-form-item prop="sign" label="个性签名" class="full-span">
                                    <el-input v-model="form.sign" type="textarea" :rows="3" placeholder="写点什么介绍自己..." />
                                </el-form-item>
                            </div>

                            <div class="form-actions">
                                <button type="button" class="submit-btn" @click="handleSubmit">
                                    保存更改
                                </button>
                            </div>
                        </el-form>
                    </div>

                    <!-- Password Tab -->
                    <div v-else key="password" class="tab-content">
                        <div class="tab-header">
                            <h2 class="tab-title">修改密码</h2>
                            <p class="tab-desc">定期修改密码以保护账户安全</p>
                        </div>

                        <el-form :model="pwdForm" :rules="pwdRules" ref="pwdFormRef" class="settings-form narrow"
                            label-position="top">
                            <el-form-item prop="oldPassword" label="当前密码">
                                <el-input v-model="pwdForm.oldPassword" type="password" placeholder="输入当前密码" show-password />
                            </el-form-item>

                            <el-form-item prop="newPassword1" label="新密码">
                                <el-input v-model="pwdForm.newPassword1" type="password" placeholder="输入新密码" show-password />
                            </el-form-item>

                            <el-form-item prop="newPassword2" label="确认新密码">
                                <el-input v-model="pwdForm.newPassword2" type="password" placeholder="再次输入新密码"
                                    show-password />
                            </el-form-item>

                            <div class="form-actions">
                                <button type="button" class="submit-btn" @click="handlePwdSubmit">
                                    更新密码
                                </button>
                            </div>
                        </el-form>
                    </div>
                </transition>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue';
import { ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElUpload, ElAvatar, ElMessage, UploadProps, UploadRawFile } from 'element-plus';
import { reqUserInfo, updUserInfo, updUserPwd } from '@/api/user';
import { useUserStore } from '@/stores/userStore';

const userStore = useUserStore();
const activeTab = ref(0);
const upload = ref<InstanceType<typeof ElUpload>>();
const imageUrl = ref<string>();
const avatarURL = '/api/user/avatar'; // Proxy handles /api

type FormRef = InstanceType<typeof ElForm>;
const formRef = ref<FormRef>();
const pwdFormRef = ref<FormRef>();

const form = ref({
    id: 0,
    username: '',
    avatar: '',
    email: '',
    score: 0,
    ranks: 0,
    school: '',
    gender: true,
    easySolve: 0,
    middleSolve: 0,
    hardSolve: 0,
    url: '',
    sign: '',
    fans: 0,
    subscribe: 0,
    password: '',
    role: '',
    ban: false,
});

const pwdForm = ref({
    oldPassword: '',
    newPassword1: '',
    newPassword2: '',
});

const uploadHeaders = { token: userStore.userInfo?.accessToken };

const beforeUpload = (file: File) => {
    const isValid = file.type === 'image/jpeg' || file.type === 'image/png';
    const isLt2M = file.size / 1024 / 1024 < 2;
    if (!isValid) ElMessage.error('Avatar picture must be JPG format!');
    if (!isLt2M) ElMessage.error('Avatar picture size can not exceed 2MB!');
    return isValid && isLt2M;
};

const handleSuccess: UploadProps['onSuccess'] = (response: any) => {
    if (response.code === 200) {
        imageUrl.value = '/api' + response.data;
        form.value.avatar = response.data;
        // User store update if needed
        if (userStore.userInfo) {
            userStore.userInfo.avatar = response.data;
        }
        ElMessage.success('头像上传成功');
    } else {
        ElMessage.error(response.message);
    }
};

const rules = ref({
    username: [{ required: true, message: '请输入用户名', trigger: 'blur' }]
});

const validatePasswordConfirmation = (rule: any, value: string, callback: Function) => {
    if (value !== pwdForm.value.newPassword1) {
        callback(new Error('两次输入的密码不一致'));
    } else {
        callback();
    }
};

const pwdRules = {
    oldPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
    newPassword1: [
        { required: true, message: '请输入新密码', trigger: 'blur' },
        { min: 6, max: 20, message: '密码长度在 6 到 20 个字符之间', trigger: 'blur' }
    ],
    newPassword2: [
        { required: true, message: '请确认新密码', trigger: 'blur' },
        { validator: validatePasswordConfirmation, trigger: 'blur' }
    ]
};

const handleSubmit = () => {
    formRef.value!.validate(async (valid) => {
        if (valid) {
            await updUserInfo(form.value);
            ElMessage.success('保存成功');
        }
    });
};

const handlePwdSubmit = () => {
    pwdFormRef.value!.validate(async (valid) => {
        if (valid) {
            await updUserPwd({
                id: 0,
                oldPassword: pwdForm.value.oldPassword,
                newPassword: pwdForm.value.newPassword1
            });
            ElMessage.success('密码修改成功');
            pwdForm.value = { oldPassword: '', newPassword1: '', newPassword2: '' };
        }
    });
};

onMounted(async () => {
    const res = await reqUserInfo(userStore.userInfo?.userId || 0);
    form.value = res.data.data;
    if (form.value.avatar) {
        imageUrl.value = '/api' + form.value.avatar;
    }
});
</script>

<style scoped lang="scss">


.settings-page {
    padding: $space-lg 0;
    max-width: 1000px;
    margin: 0 auto;
}

.settings-container {
    display: flex;
    min-height: 500px;
    overflow: hidden;
    border-radius: $radius-lg;
    
    @include mobile {
        flex-direction: column;
    }
}

// Sidebar
.settings-sidebar {
    width: 240px;
    padding: $space-xl;
    border-right: 1px solid var(--border-color);
    background: var(--bg-elevated);
    flex-shrink: 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    
    @include mobile {
        width: 100%;
        padding: $space-md;
        border-right: none;
        border-bottom: 1px solid var(--border-color);
        flex-direction: row;
        justify-content: space-between;
    }
}

.user-preview {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: $space-xl;
    position: relative;
    
    @include mobile {
        flex-direction: row;
        gap: $space-md;
        margin-bottom: 0;
    }
    
    .avatar-uploader {
        position: relative;
        cursor: pointer;
        
        &:hover .upload-mask {
            opacity: 1;
        }
    }
    
    .preview-avatar {
        width: 80px;
        height: 80px;
        border-radius: 50%;
        background: var(--primary-gradient);
        color: white;
        font-weight: $font-weight-bold;
        font-size: 32px;
        display: flex;
        align-items: center;
        justify-content: center;
        object-fit: cover;
        border: 3px solid var(--bg-elevated);
        box-shadow: var(--shadow-md);
    }
    
    .upload-mask {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        border-radius: 50%;
        background: rgba(0, 0, 0, 0.5);
        display: flex;
        align-items: center;
        justify-content: center;
        opacity: 0;
        transition: opacity $transition-fast;
        color: white;
        
        svg {
            width: 24px;
            height: 24px;
        }
    }
    
    .preview-name {
        margin-top: $space-md;
        font-weight: $font-weight-semibold;
        color: var(--text-primary);
        font-size: $font-size-lg;
        
        @include mobile {
            margin-top: 0;
        }
    }
}

.nav-menu {
    display: flex;
    flex-direction: column;
    gap: $space-xs;
    width: 100%;
    
    @include mobile {
        flex-direction: row;
        width: auto;
    }
}

.nav-item {
    display: flex;
    align-items: center;
    gap: $space-sm;
    padding: $space-sm $space-md;
    background: transparent;
    border: none;
    border-radius: $radius-md;
    color: var(--text-secondary);
    font-size: $font-size-sm;
    cursor: pointer;
    transition: all $transition-fast;
    text-align: left;
    width: 100%;
    
    svg {
        width: 18px;
        height: 18px;
    }
    
    &:hover {
        background: var(--glass-bg);
        color: var(--text-primary);
    }
    
    &.active {
        background: rgba(102, 126, 234, 0.1);
        color: var(--primary-start);
        font-weight: $font-weight-medium;
    }
}

// Content
.settings-content {
    flex: 1;
    padding: $space-xl $space-2xl;
    position: relative;
    
    @include mobile {
        padding: $space-lg;
    }
}

.tab-header {
    margin-bottom: $space-xl;
    
    .tab-title {
        font-size: $font-size-xl;
        font-weight: $font-weight-bold;
        margin-bottom: $space-xs;
    }
    
    .tab-desc {
        color: var(--text-secondary);
        font-size: $font-size-sm;
    }
}

// Form
.form-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: $space-lg;
    
    @include mobile {
        grid-template-columns: 1fr;
    }
}

.settings-form {
    &.narrow {
        max-width: 400px;
    }
    
    .full-span {
        grid-column: 1 / -1;
    }
    
    .full-width {
        width: 100%;
    }
}

.form-actions {
    margin-top: $space-xl;
    display: flex;
    justify-content: flex-end;
}

.submit-btn {
    padding: $space-sm $space-xl;
    background: var(--primary-gradient);
    color: white;
    border: none;
    border-radius: $radius-md;
    font-size: $font-size-sm;
    font-weight: $font-weight-semibold;
    cursor: pointer;
    transition: all $transition-normal;
    box-shadow: var(--shadow-glow);
    
    &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 20px rgba(102, 126, 234, 0.5);
    }
}

// Transition
.fade-enter-active,
.fade-leave-active {
    transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
    opacity: 0;
}
</style>