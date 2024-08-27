// 二次封装axios：使用请求与响应拦截器
import axios from "axios";
import { ElMessage } from "element-plus";
// 创建axios实例 配置一些基础项
const request = axios.create({
    baseURL: 'http://localhost:8080/',
    timeout: 5000,
});
// 请求拦截器
request.interceptors.request.use((config) => {
    // config配置对象
    // headers属性请求头对象
    return config;
});
// 响应拦截器
request.interceptors.response.use((response) => {
    // 成功获取到数据 可以对数据进行处理
    return response.data;
}, (error) => {
    // 响应失败 处理网络错误的
    let error_info = error.response.data;
    let msg = error_info.msg;
    ElMessage({
        type: 'error',
        message: msg
    });
    return Promise.reject(error);
});

export default request;