<template>
    <div class="status_list">
        <div class="search_setting">
            <el-input v-model="searchProblemName" placeholder="请输入题目名称" clearable />
            <el-input v-model="searchUserId" placeholder="请输入用户名称" clearable />
            <el-select v-model="searchLanguage" placeholder="语言" clearable>
                <el-option label="cpp" value="cpp" />
                <el-option label="Java" value="java" />
                <el-option label="Python" value="python" />
            </el-select>
            <el-select v-model="searchStatus" placeholder="状态" clearable>
                <el-option label="Accepted" value="Accepted" />
                <el-option label="Wrong Answer" value="Wrong Answer" />
                <el-option label="Runtime Error" value="Runtime Error" />
                <el-option label="Compile Error" value="Compile Error" />
                <el-option label="Time Limit Exceeded" value="Time Limit Exceeded" />
                <el-option label="Memory Limit Exceeded" value="Memory Limit Exceeded" />
            </el-select>
        </div>
        <el-table :data="tableData" style="width: 100%" :default-sort="{ prop: 'id', order: 'descending' }">
            <!-- 展开行用于展示代码 -->
            <el-table-column type="expand" width="20">
                <template v-slot="props">
                    <Editor :config="{
                        tabSize: 4,
                        disabled: false,
                        height: '62vh',
                        width: '100%',
                        language: props.row.language,
                        theme: 'oneDark',
                        fontSize: 16,
                        editorType: 'show',
                        code: props.row.code
                    }" />
                </template>
            </el-table-column>
            <el-table-column prop="submitTime" label="提交时间" sortable align="center" width="160" />
            <el-table-column prop="id" label="提交ID" sortable align="center" width="100" />
            <el-table-column prop="problemName" label="题目名称" align="center" min-width="100">
            <template v-slot="scope">
                <span class="problem_name" @click="toProblem(scope.row.problemId)">{{ scope.row.problemName }}</span>
            </template>
            </el-table-column>
            <el-table-column prop="userName" label="用户" align="center" />
            <el-table-column prop="language" label="语言" align="center" />
            <el-table-column prop="status" label="状态" align="center" min-width="100">
            <template v-slot="scope">
                <el-tag
                :type="scope.row.status === 'Accepted' ? 'success'
                    : scope.row.status === 'Wrong Answer' ? 'danger'
                    : scope.row.status === 'Runtime Error' ? 'danger'
                    : scope.row.status === 'Compile Error' ? 'danger'
                    : scope.row.status === 'Time Limit Exceeded' ? 'warning'
                    : scope.row.status === 'Memory Limit Exceeded' ? 'warning'
                    : 'info'"
                >
                {{ scope.row.status }}
                </el-tag>
            </template>
            </el-table-column>
            <el-table-column prop="time" label="执行时间" align="center" />
            <el-table-column prop="memory" label="内存消耗" align="center" />
        </el-table>

        <div class="page_list">
            <el-pagination background layout="prev, pager, next, jumper, total, sizes" :total="total"
                :page-count="pages" :page-sizes="pageSizes" v-model:page-size="pageQueryForm.pageSize"
                v-model:current-page="pageQueryForm.pageNo" @change="getStatusList" />
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElTable, ElTableColumn, ElSelect, ElOption, ElInput, ElPagination } from 'element-plus';
// 假设后端接口已经实现，接口方法可根据实际情况调整
import { reqSubmissionPageList } from '@/api/submission';
import { reqUserInfo } from '@/api/user'; // 如果需要用户相关接口
import { reqProblemDetail } from '@/api/problem'; // 如果需要题目相关接口
import type { BasePageQueryForm } from '@/api/base';
import { Submission } from '@/api/submission/type';
import languages from '@/components/CodeEditor/languages';
import { configType } from '@/components/CodeEditor/index.vue';
import Editor from '@/components/CodeEditor/index.vue';

interface StatusItem {
    id: number;             // 提交ID
    submitTime: string;     // 提交时间
    problemId: number;      // 题目的ID，用于跳转详情
    problemName: string;    // 题目名称
    userId: number;         // 用户ID
    userName: string;      // 用户名，可选
    language: string;       // 使用的语言
    code: string;           // 源代码
    status?: string;         // 提交状态
    time?: number;           // 执行时间
    memory?: number;         // 内存消耗
}

const router = useRouter();
const route = useRoute();

// 搜索条件
const searchProblemName = ref();
const searchUserId = ref();
const searchLanguage = ref();
const searchStatus = ref();

watch([searchProblemName, searchUserId, searchLanguage, searchStatus], () => {
    getStatusList();
});

const tableData = ref<StatusItem[]>([]);
const pageQueryForm = ref<BasePageQueryForm>({
    pageNo: 1,
    pageSize: 10,
    isAsc: false,
    sortBy: ''
});
const total = ref(0);
const pages = ref(0);
const pageSizes = ref([5, 10, 20, 50]);

const toProblem = (id: number) => {
    router.push("/problem/" + id);
};

const getStatusList = async () => {
    const params = {
        ...pageQueryForm.value,
        problemId: searchProblemName.value,
        userId: searchUserId.value,
        language: searchLanguage.value,
        status: searchStatus.value
    };
    const res = await reqSubmissionPageList(params);
    const { total: totalV, pages: pagesV, list } = res.data.data;
    total.value = totalV;
    pages.value = pagesV;
    tableData.value = await Promise.all(list.map(async (item: Submission) => {
        const problemRes = await reqProblemDetail(String(item.problemId));
        const problemName = problemRes.data?.data?.name || String(item.problemId);
        const userInfo = await reqUserInfo(item.userId);
        const userName = userInfo.data?.data?.username || '未知用户';
        const timedate = new Date(item.submitTime).toLocaleString();
        return {
            id: item.id,
            submitTime: timedate,
            problemName: problemName,
            problemId: item.problemId,
            userId: item.userId,
            userName: userName,
            language: item.language,
            code: item.code,
            status: item.status,
            time: item.time,
            memory: item.memory,
        };
    }));
};

onMounted(() => {
    // 从 URL 查询参数获取
    const { problemName, userName } = route.query;
    if (problemName) searchProblemName.value = problemName as string;
    if (userName) searchUserId.value = userName as string;
    getStatusList();
});
</script>

<style scoped lang="scss">
.status_list {
    font-weight: 700;

    .search_setting {
        display: flex;
        align-items: center;
        gap: 10px;
        margin-bottom: 20px;
        padding-top: 10px;
    }

    .el-table {
        background-color: #f9f9f9;
        border-radius: 8px;
        box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);

        .problem_name {
            color: #1E70BF;
            cursor: pointer;
        }
    }

    .code_container {
        background-color: #f5f5f5;
        padding: 10px;
        border-radius: 4px;
        font-family: Menlo, Monaco, Consolas, "Courier New", monospace;
        white-space: pre-wrap;
        word-break: break-all;
    }

    .page_list {
        display: flex;
        justify-content: center;
        margin-top: 20px;
    }
}
</style>