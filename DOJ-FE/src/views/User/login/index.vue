<template>
    <el-card class="card">
        <template #header>
            <div class="card-header">
                <span>欢迎登陆 DOJ</span>
            </div>
        </template>
        <el-form :model="form" :rules="rules" ref="formRef" label-width="0px" class="login-form">
            <el-form-item prop="username">
                <el-input v-model="form.username" placeholder="用户名" :prefix-icon="Avatar">
                </el-input>
            </el-form-item>
            <el-form-item prop="password">
                <el-input v-model="form.password" type="password" placeholder="密码" :prefix-icon="Lock">
                </el-input>
            </el-form-item>

            <el-form-item>
                <el-button type="primary" @click="handleSubmit">登录</el-button>
            </el-form-item>
        </el-form>
        <div class="card-footer">
            <span><a href="\register">注册账号</a></span>
            <span>忘记密码？</span>
        </div>
    </el-card>
</template>

<script setup>
import { ref } from 'vue';
import { ElForm, ElFormItem, ElInput, ElButton, ElCard } from 'element-plus';
import { Lock, Avatar } from '@element-plus/icons-vue';

// 表单数据
const form = ref({
    username: '',
    password: ''
});

// 表单验证规则
const rules = ref({
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' }
    ]
});

// 表单提交处理
const handleSubmit = async () => {
    const formRef = ref(null);
    try {
        await formRef.value.validate();
        // 表单验证成功后，执行登录操作
        console.log('表单数据:', form.value);
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
        justify-content: space-between;
        margin-top: 20px;
        font-size: 14px;
        color: #409EFF;
        cursor: pointer;
        a {
            color: #409EFF;
        }
    }

    .login-form {
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