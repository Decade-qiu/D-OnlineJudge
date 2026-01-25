<template>
    <div class="problem-manager">
        <div class="actions-bar">
            <el-button type="primary" @click="handleAdd">新增题目</el-button>
            <el-button type="warning" @click="handleSync" :loading="syncLoading">同步到ES</el-button>
        </div>

        <el-table :data="tableData" v-loading="loading" style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="name" label="题目名称" />
            <el-table-column prop="difficulty" label="难度" width="100">
                <template #default="{ row }">
                    <el-tag :type="getDifficultyType(row.difficulty)">{{ row.difficulty }}</el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="tags" label="标签">
                <template #default="{ row }">
                    <el-tag v-for="tag in parseTags(row.tags)" :key="tag" size="small" class="mr-1">{{ tag }}</el-tag>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="180">
                <template #default="{ row }">
                    <el-button size="small" @click="handleEdit(row)">编辑</el-button>
                    <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

        <div class="pagination-container">
            <el-pagination
                v-model:current-page="pageParams.pageNo"
                v-model:page-size="pageParams.pageSize"
                :total="total"
                layout="prev, pager, next"
                @current-change="getData"
            />
        </div>

        <ProblemDialog 
            ref="dialogRef" 
            @refresh="getData"
        />
    </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, reactive } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { reqProblemPageList, reqDeleteProblem, reqSyncProblemToEs } from '@/api/problem';
import type { ProblemType } from '@/api/problem/type';
import ProblemDialog from './components/ProblemDialog.vue';

const tableData = ref<ProblemType[]>([]);
const loading = ref(false);
const syncLoading = ref(false);
const total = ref(0);
const pageParams = reactive({
    pageNo: 1,
    pageSize: 10,
    keyword: '',
    isAsc: false,
    sortBy: 'id',
    name: '',
    difficulty: '',
    tags: [] as string[],
    status: ''
});

const dialogRef = ref();

const getDifficultyType = (diff: string) => {
    if (diff === '简单') return 'success';
    if (diff === '中等') return 'warning';
    if (diff === '困难') return 'danger';
    return 'info';
};

const parseTags = (tags: any) => {
    if (!tags) return [];
    if (Array.isArray(tags)) return tags;
    try {
        if (typeof tags === 'string' && tags.startsWith('[')) {
            return JSON.parse(tags.replace(/'/g, '"'));
        }
        return String(tags).split(',');
    } catch {
        return [tags];
    }
};

const getData = async () => {
    loading.value = true;
    try {
        const res = await reqProblemPageList(pageParams as any);
        if (res.data.code === 200) {
            tableData.value = res.data.data.list;
            total.value = res.data.data.total;
        }
    } finally {
        loading.value = false;
    }
};

const handleAdd = () => {
    dialogRef.value?.open();
};

const handleEdit = (row: any) => {
    dialogRef.value?.open(row);
};

const handleDelete = (row: any) => {
    ElMessageBox.confirm('确定要删除该题目吗？', '提示', {
        type: 'warning'
    }).then(async () => {
        const res = await reqDeleteProblem(row.id);
        if (res.data.code === 200) {
            ElMessage.success('删除成功');
            getData();
        } else {
            ElMessage.error(res.data.message || '删除失败');
        }
    });
};

const handleSync = async () => {
    syncLoading.value = true;
    try {
        const res = await reqSyncProblemToEs();
        if (res.data.code === 200) {
            ElMessage.success(`同步成功，共同步 ${res.data.data} 条数据`);
        } else {
            ElMessage.error(res.data.message || '同步失败');
        }
    } catch (e) {
        ElMessage.error('同步请求失败');
    } finally {
        syncLoading.value = false;
    }
};

onMounted(() => {
    getData();
});
</script>

<style scoped lang="scss">


.problem-manager {
    .actions-bar {
        margin-bottom: $space-lg;
        display: flex;
        gap: $space-md;
    }
    
    .mr-1 {
        margin-right: 4px;
    }
    
    .pagination-container {
        margin-top: $space-lg;
        display: flex;
        justify-content: center;
    }
}
</style>
