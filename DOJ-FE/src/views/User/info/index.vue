<template>
    <div class="card">
        <div class="left">
            <img src="@/assets/default.png" alt="camera" />
            <ul>
                <li>Profile</li>
                <li>Account</li>
            </ul>
        </div>
        <div class="right">
            <div class="title">
                Profile Setting
            </div>
            <el-upload class="upload-demo" drag action="https://run.mocky.io/v3/9d059bf9-4660-45f2-925d-ce80ad6c4d15"
                multiple>
                <el-icon class="el-icon--upload"><upload-filled /></el-icon>
                <div class="el-upload__text">
                    Drop file here or <em>click to upload</em>
                </div>
                <template #tip>
                    <div class="el-upload__tip">
                        jpg/png files with a size less than 500kb
                    </div>
                </template>
            </el-upload>
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
        </div>
    </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import { UploadFilled } from '@element-plus/icons-vue'
import { ElForm, ElFormItem, ElInput, ElButton, ElCard, ElMessage, ElRow, ElCol, ElUpload } from 'element-plus';
import { Lock, Avatar } from '@element-plus/icons-vue';
import { useUserStore } from '@/stores/userStore';
import { useRouter } from 'vue-router';

const userStore = useUserStore();

const router = useRouter();

// 表单数据
const form = ref({
    username: '',
    password: ''
});

type FormRef = InstanceType<typeof ElForm>;
const formRef = ref<FormRef>();

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
    try {
        formRef.value!.validate(async valid => {
            if (!valid) {
                return;
            }
            await userStore.getUserInfo(
                {
                    username: form.value.username,
                    password: form.value.password
                }
            );
            ElMessage.success("登录成功!");
            router.replace({ path: '/' });
        });
    } catch (error) {
        ElMessage.error("请重新填写表单!");
    }
};
</script>
  
<style scoped lang="scss">
.card {
    margin: 20px;
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);

    display: flex;
    justify-content: start;

    .left {

        padding: 30px 60px;
        border-right: 2px solid #f0f0f0;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: start;

        gap: 20px;

        img {
            width: 160px;
            /* 根据需要设置图片宽度 */
            height: 160px;
            /* 根据需要设置图片高度 */
            border-radius: 50%;
            /* 将图片变成圆形 */
            object-fit: cover;
            /* 确保图片内容适应容器 */
        }

        ul {
            margin: 40px 0;
            display: flex;
            flex-direction: column;
            align-items: center;
            gap: 40px;

            li {
                font-size: 20px;
                color: #333;
                cursor: pointer;
                transition: color 0.3s;

                &:hover {
                    color: #00aaff;
                }
            }
        }

    }

    .right {

        flex-grow: 1;

        padding: 30px 60px;
        display: flex;
        flex-direction: column;
        align-items: left;
        justify-content: start;
        gap: 20px;

        .title {
            font-size: 24px;
            color: #333;
        }

        .login-form {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;

            .el-form-item {
                width: 100%;
                /* 根据需要设置表单项宽度 */
            }

            .el-input {
                width: 100%;
                /* 根据需要设置输入框宽度 */
            }

            .el-button {
                width: 100%;
                /* 根据需要设置按钮宽度 */
            }
        }
    }
}
</style>  