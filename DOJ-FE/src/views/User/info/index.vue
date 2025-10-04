<template>
    <div class="card">
        <div class="left">
            <img ref="avatarImage" src="@/assets/default.png" alt="camera" />
            <ul>
                <li :class="{ active: activeIndex === 0 }" @click="activeIndex = 0">
                    简介
                </li>
                <li :class="{ active: activeIndex === 1 }" @click="activeIndex = 1">
                    账号
                </li>
            </ul>
        </div>
        <div class="right" v-show="activeIndex === 0">
            <div class="title">
                简介设置
            </div>
            <el-upload class="upload-demo" drag ref="upload" :before-upload="beforeUpload" :on-success="handleSuccess"
                :action="avatarURL" :limit="1" :on-exceed="handleExceed" :on-remove="handleRemove" :headers="uploadHeaders">
                <el-icon class="el-icon--upload"><upload-filled /></el-icon>
                <div class="el-upload__text">
                    在这里拖放文件或单击<em>上传</em>头像
                </div>
                <template #tip>
                    <div class="el-upload__tip">
                        jpg/png files with a size less than 500kb
                    </div>
                </template>
            </el-upload>
            <div v-if="imageUrl" class="uploaded-image">
                <img :src="imageUrl" alt="Uploaded Image" />
            </div>
            <el-form :model="form" :rules="rules" ref="formRef" label-width="0px" class="login-form">
                <div class="multi-item-row">
                    <el-form-item prop="username" class="username">
                        <el-input v-model="form.username" placeholder="用户名" :prefix-icon="Avatar">
                        </el-input>
                    </el-form-item>
                    <el-form-item prop="gender" class="gender">
                        <el-select v-model="form.gender" placeholder="性别">
                            <template #prefix>
                                <el-icon>
                                    <Male />
                                </el-icon>
                            </template>
                            <el-option label="男" :value="true" />
                            <el-option label="女" :value="false" />
                        </el-select>
                    </el-form-item>
                </div>
                <el-form-item prop="school">
                    <el-input v-model="form.school" placeholder="学校" :prefix-icon="School">
                    </el-input>
                </el-form-item>
                <el-form-item prop="url">
                    <el-input v-model="form.url" placeholder="个人主页" :prefix-icon="Link">
                    </el-input>
                </el-form-item>
                <el-form-item prop="sign">
                    <el-input v-model="form.sign" placeholder="个人签名" :prefix-icon="Comment">
                    </el-input>
                </el-form-item>

                <el-form-item>
                    <el-button type="primary" @click="handleSubmit">保存</el-button>
                </el-form-item>
            </el-form>
        </div>
        <div class="right" v-show="activeIndex === 1">
            <div class="title">
                账号设置
            </div>
            <el-form :model="pwdForm" :rules="pwdRules" ref="pwdFormRef" label-width="0px" class="login-form">
                <el-form-item prop="oldPassword">
                    <el-input v-model="pwdForm.oldPassword" placeholder="旧密码" :prefix-icon="Lock" type="password">
                    </el-input>
                </el-form-item>
                <el-form-item prop="newPassword1">
                    <el-input v-model="pwdForm.newPassword1" placeholder="新密码" :prefix-icon="Lock" type="password">
                    </el-input>
                </el-form-item>
                <el-form-item prop="newPassword2">
                    <el-input v-model="pwdForm.newPassword2" placeholder="确认新密码" :prefix-icon="Lock" type="password">
                    </el-input>
                </el-form-item>

                <el-form-item>
                    <el-button type="primary" @click="handlePwdSubmit">保存</el-button>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { Ref, ref, onMounted, inject } from 'vue';
import { ElForm, ElFormItem, ElInput, ElButton, ElCard, ElMessage, ElRow, ElCol, ElUpload, UploadProps, UploadRawFile } from 'element-plus';
import { UploadFilled, School, Avatar, Male, Link, Comment, Lock } from '@element-plus/icons-vue';
import { uploadAvatarResponseData, userInfoResponseData } from '@/api/user/type';
import { reqUserInfo, updUserInfo, updUserPwd } from '@/api/user';
import { useUserStore } from '@/stores/userStore';

const triggerRefresh = inject('triggerRefresh') as (() => void);
const activeIndex = ref<number>(0);
const userStore = useUserStore();
const upload = ref<InstanceType<typeof ElUpload>>();
const imageUrl = ref<string>();
const avatarURL = import.meta.env.VITE_APP_URL + '/user/avatar';
const avatarImage = ref<HTMLImageElement>();
type FormRef = InstanceType<typeof ElForm>;
const formRef = ref<FormRef>();
let form: Ref<userInfoResponseData['data']> = ref({
    id: 0,
    username: '',
    avatar: '',
    email: '',
    password: '',
    score: 0,
    ranks: 0,
    school: '',
    gender: false,
    easySolve: 0,
    middleSolve: 0,
    hardSolve: 0,
    role: '',
    url: '',
    sign: '',
    fans: 0,
    subscribe: 0,
    ban: false,
});

// 上传头像
const uploadHeaders = {
    Authorization: userStore.userInfo?.accessToken
};
const beforeUpload = (file: File) => {
    const isJPG = file.type === 'image/jpeg';
    const isPNG = file.type === 'image/png';
    if (!isJPG && !isPNG) {
        ElMessage.error('上传文件格式必须是 JPG 或 PNG!');
        return false;
    }
    if (file.size / 1024 / 1024 > 0.5) {
        ElMessage.error('上传文件大小不能超过 500KB!');
        return false;
    }
    return true;
};
const handleSuccess: UploadProps['onSuccess'] = (response: uploadAvatarResponseData) => {
    if (response.code === 200) {
        imageUrl.value = import.meta.env.VITE_APP_URL + response.data;
        form.value.avatar = response.data;
    } else {
        ElMessage.error(response.message);
    }
};
const handleRemove: UploadProps['onRemove'] = (file) => {
    imageUrl.value = '';
    form.value.avatar = '';
};
const handleExceed: UploadProps['onExceed'] = (files) => {
    upload.value!.clearFiles();
    const file = files[0] as UploadRawFile;
    upload.value!.handleStart(file);
    upload.value!.submit();
};

// 表单
const rules = ref({
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' }
    ],
});
const handleSubmit = () => {
    formRef.value!.validate(async valid => {
        if (!valid) {
            return;
        }
        await updUserInfo(form.value);
        ElMessage.success('保存成功');
        // update user info
        imageUrl.value = '';
        upload.value?.clearFiles();
        if (form.value.avatar) {
            avatarImage.value!.src = import.meta.env.VITE_APP_URL + form.value.avatar;
        }
        // triggerRefresh();
    });
};

//密码
const pwdFormRef = ref<FormRef>();
const pwdForm = ref({
    oldPassword: '',
    newPassword1: '',
    newPassword2: '',
});
const validatePasswordConfirmation = (rule: any, value: string, callback: Function) => {
    if (value !== pwdForm.value.newPassword1) {
        callback(new Error('两次输入的新密码不匹配'));
    } else {
        callback();
    }
};
const pwdRules = {
    oldPassword: [
        { required: true, message: '请输入旧密码', trigger: 'blur' },
        { min: 1, max: 20, message: '密码长度在 1 到 20 个字符之间', trigger: 'blur' },
    ],
    newPassword1: [
        { required: true, message: '请输入新密码', trigger: 'blur' },
        { min: 1, max: 20, message: '密码长度在 1 到 20 个字符之间', trigger: 'blur' },
    ],
    newPassword2: [
        { required: true, message: '请确认新密码', trigger: 'blur' },
        { min: 1, max: 20, message: '密码长度在 1 到 20 个字符之间', trigger: 'blur' },
        { validator: validatePasswordConfirmation, trigger: 'blur' },
    ],
};
const handlePwdSubmit = () => {
    pwdFormRef.value!.validate(async valid => {
        if (valid) {
            // 提交表单数据
            await updUserPwd({
                id: 0,
                oldPassword: pwdForm.value.oldPassword,
                newPassword: pwdForm.value.newPassword1,
            });
            ElMessage.success('密码已保存成功');
        } else {
            ElMessage.error('请检查表单填写是否正确');
        }
    });
};


onMounted(async () => {
    const userInfoResponseData = await reqUserInfo(userStore.userInfo?.userId ? userStore.userInfo!.userId : 0);
    form.value = userInfoResponseData.data.data;
    if (form.value.avatar) {
        avatarImage.value!.src = import.meta.env.VITE_APP_URL + form.value.avatar;
    }
});
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
        flex-shrink: 0; // 禁止缩小
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

                &.active {
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

        .multi-item-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
        }

        .uploaded-image {
            margin-top: 20px;

            img {
                max-width: 100%;
                height: auto;
                border-radius: 8px;
                box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
            }
        }

        // .login-form {
        //     display: flex;
        //     flex-direction: column;
        //     align-items: center;
        //     justify-content: center;

        //     .el-form-item {
        //         width: 100%;
        //         /* 根据需要设置表单项宽度 */
        //     }

        //     .el-input {
        //         width: 100%;
        //         /* 根据需要设置输入框宽度 */
        //     }

        //     .el-button {
        //         width: 100%;
        //         /* 根据需要设置按钮宽度 */
        //     }
        // }
    }
}
</style>  