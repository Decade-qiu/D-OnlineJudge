<template>
    <el-card class="card">
        <template #header>
            <div class="card-header">
                <span>欢迎注册 DOJ</span>
            </div>
        </template>
        <el-form :model="form" :rules="rules" ref="formRef" label-width="0px" class="register-form">
            <el-form-item prop="nickname">
                <el-input v-model="form.username" placeholder="用户名" :prefix-icon="Avatar">
                </el-input>
            </el-form-item>

            <el-form-item prop="password">
                <el-input v-model="form.password" type="password" placeholder="密码"  :prefix-icon="Lock" />
            </el-form-item>

            <el-form-item prop="email">
                <el-input v-model="form.email" placeholder="邮箱" :prefix-icon="Message" />
            </el-form-item>

            <el-form-item prop="signature">
                <el-input v-model="form.signature" placeholder="签名（可不填）" :prefix-icon="Opportunity"/>
            </el-form-item>

            <el-form-item>
                <el-button type="primary" @click="handleSubmit">注册</el-button>
            </el-form-item>
        </el-form>
        <div class="card-footer">
            <span>已有账号？ <a href="/login">登录</a></span>
        </div>
    </el-card>
</template>
  
<script setup>
import { ref } from 'vue';
import { ElForm, ElFormItem, ElInput, ElButton, ElCard } from 'element-plus';
import { Lock, Avatar, Message, Opportunity } from '@element-plus/icons-vue';

// 表单数据
const form = ref({
    nickname: '',
    password: '',
    email: '',
    signature: ''
});

// 表单验证规则
const rules = ref({
    nickname: [
        { required: true, message: '请输入昵称', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' }
    ],
    email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
    ]
});

// 表单提交处理
const handleSubmit = async () => {
    const formRef = ref(null);
    try {
        await formRef.value.validate();
        // 表单验证成功后的处理逻辑
        console.log('注册数据:', form.value);
    } catch (error) {
        console.log('表单验证失败:', error);
    }
};
</script>
  
<style scoped lang="scss">
.card {
    max-width: 400px;
    margin: 0 auto;
    margin-top: 100px;
    border-radius: 8px;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);

    .card-header {
        font-size: 24px;
        font-weight: bold;
    }

    .card-footer {
        display: flex;
        justify-content: center;
        margin-top: 20px;
        font-size: 14px;
        color: #409EFF;

        a {
            color: #409EFF;
            text-decoration: none;
            margin-left: 5px;

            &:hover {
                text-decoration: underline;
            }
        }
    }

    .register-form {
        margin: 5px 0;
        padding: 0;

        .el-form-item {
            margin-bottom: 20px;
        }

        .el-button {
            width: 100%;
        }
    }
}
</style>
