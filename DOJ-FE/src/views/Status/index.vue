<template>
    <div class="status-page">
        <!-- Page Header -->
        <div class="page-header">
            <h1 class="page-title">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/>
                </svg>
                提交状态
            </h1>
            <p class="page-desc">查看所有代码提交记录</p>
        </div>

        <!-- Filters -->
        <div class="filters-section card-glass">
            <div class="filter-group">
                <el-input v-model="searchProblemName" placeholder="题目名称" clearable class="filter-input" :prefix-icon="Search" />
                <el-input v-model="searchUserId" placeholder="用户名称" clearable class="filter-input" />
                <el-select v-model="searchLanguage" placeholder="语言" clearable class="filter-select">
                    <el-option label="C++" value="cpp" />
                    <el-option label="Java" value="java" />
                    <el-option label="Python" value="python" />
                </el-select>
                <el-select v-model="searchStatus" placeholder="评测状态" clearable class="filter-select status-select">
                    <el-option label="Accepted" value="Accepted">
                        <span class="status-option accepted">✓ Accepted</span>
                    </el-option>
                    <el-option label="Wrong Answer" value="Wrong Answer">
                        <span class="status-option wrong">✗ Wrong Answer</span>
                    </el-option>
                    <el-option label="Runtime Error" value="Runtime Error">
                        <span class="status-option error">Runtime Error</span>
                    </el-option>
                    <el-option label="Compile Error" value="Compile Error">
                        <span class="status-option error">Compile Error</span>
                    </el-option>
                    <el-option label="Time Limit Exceeded" value="Time Limit Exceeded">
                        <span class="status-option tle">Time Limit Exceeded</span>
                    </el-option>
                    <el-option label="Memory Limit Exceeded" value="Memory Limit Exceeded">
                        <span class="status-option tle">Memory Limit Exceeded</span>
                    </el-option>
                </el-select>
            </div>
        </div>

        <!-- Status Table -->
        <div class="table-container card-glass">
            <el-table 
                :data="tableData" 
                :default-sort="{ prop: 'id', order: 'descending' }"
                class="status-table"
                v-loading="loading"
            >
                <el-table-column type="expand" width="30">
                    <template v-slot="props">
                        <div class="code-expand">
                            <Editor :config="{
                                tabSize: 4,
                                disabled: true,
                                height: '400px',
                                width: '100%',
                                language: props.row.language,
                                theme: 'default',
                                fontSize: 14,
                                editorType: 'show',
                                code: props.row.code
                            }" />
                        </div>
                    </template>
                </el-table-column>

                <el-table-column prop="submitTime" label="提交时间" sortable align="center" width="160">
                    <template v-slot="scope">
                        <span class="time-cell">{{ scope.row.submitTime }}</span>
                    </template>
                </el-table-column>

                <el-table-column prop="id" label="ID" sortable align="center" width="80">
                    <template v-slot="scope">
                        <span class="id-cell">#{{ scope.row.id }}</span>
                    </template>
                </el-table-column>

                <el-table-column prop="problemName" label="题目" min-width="150">
                    <template v-slot="scope">
                        <span class="problem-link" @click="toProblem(scope.row.problemId)">
                            {{ scope.row.problemName }}
                        </span>
                    </template>
                </el-table-column>

                <el-table-column prop="userName" label="用户" align="center" width="120">
                    <template v-slot="scope">
                        <span class="user-cell">{{ scope.row.userName }}</span>
                    </template>
                </el-table-column>

                <el-table-column prop="language" label="语言" align="center" width="100">
                    <template v-slot="scope">
                        <span class="lang-tag" :class="scope.row.language">
                            {{ formatLanguage(scope.row.language) }}
                        </span>
                    </template>
                </el-table-column>

                <el-table-column prop="status" label="状态" align="center" min-width="140">
                    <template v-slot="scope">
                        <span class="status-tag" :class="getStatusClass(scope.row.status)">
                            {{ scope.row.status }}
                        </span>
                    </template>
                </el-table-column>

                <el-table-column prop="time" label="耗时" align="center" width="100">
                    <template v-slot="scope">
                        <span class="metric-cell">{{ scope.row.time }}ms</span>
                    </template>
                </el-table-column>

                <el-table-column prop="memory" label="内存" align="center" width="100">
                    <template v-slot="scope">
                        <span class="metric-cell">{{ scope.row.memory }}KB</span>
                    </template>
                </el-table-column>
            </el-table>

            <!-- Pagination -->
            <div class="pagination-container">
                <el-pagination 
                    background 
                    layout="prev, pager, next, sizes, total" 
                    :total="total"
                    :page-count="pages" 
                    :page-sizes="pageSizes" 
                    v-model:page-size="pageQueryForm.pageSize"
                    v-model:current-page="pageQueryForm.pageNo" 
                    @change="getStatusList" 
                />
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElTable, ElTableColumn, ElSelect, ElOption, ElInput, ElPagination } from 'element-plus';
import { Search } from '@element-plus/icons-vue';
import { reqSubmissionPageList } from '@/api/submission';
import { reqUserInfo } from '@/api/user';
import { reqProblemDetail } from '@/api/problem';
import type { BasePageQueryForm } from '@/api/base';
import { Submission } from '@/api/submission/type';
import Editor from '@/components/CodeEditor/index.vue';

interface StatusItem {
    id: number;
    submitTime: string;
    problemId: number;
    problemName: string;
    userId: number;
    userName: string;
    language: string;
    code: string;
    status?: string;
    time?: number;
    memory?: number;
}

const router = useRouter();
const route = useRoute();

const searchProblemName = ref();
const searchUserId = ref();
const searchLanguage = ref();
const searchStatus = ref();
const loading = ref(false);

watch([searchProblemName, searchUserId, searchLanguage, searchStatus], () => {
    getStatusList();
});

const tableData = ref<StatusItem[]>([]);
const pageQueryForm = ref<BasePageQueryForm>({
    pageNo: 1,
    pageSize: 20,
    isAsc: false,
    sortBy: ''
});
const total = ref(0);
const pages = ref(0);
const pageSizes = ref([10, 20, 50, 100]);

const toProblem = (id: number) => {
    router.push("/problem/" + id);
};

const formatLanguage = (lang: string) => {
    const map: Record<string, string> = {
        'cpp': 'C++',
        'java': 'Java',
        'python': 'Python'
    };
    return map[lang] || lang;
};

const getStatusClass = (status: string) => {
    if (status === 'Accepted') return 'accepted';
    if (status === 'Wrong Answer') return 'wrong';
    if (status?.includes('Error')) return 'error';
    if (status?.includes('Exceeded')) return 'tle';
    return 'pending';
};

const getStatusList = async () => {
    loading.value = true;
    try {
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
                problemName,
                problemId: item.problemId,
                userId: item.userId,
                userName,
                language: item.language,
                code: item.code,
                status: item.status,
                time: item.time,
                memory: item.memory,
            };
        }));
    } finally {
        loading.value = false;
    }
};

onMounted(() => {
    const { problemName, userName } = route.query;
    if (problemName) searchProblemName.value = problemName as string;
    if (userName) searchUserId.value = userName as string;
    getStatusList();
});
</script>

<style scoped lang="scss">


.status-page {
    display: flex;
    flex-direction: column;
    gap: $space-lg;
}

.page-header {
    .page-title {
        display: flex;
        align-items: center;
        gap: $space-sm;
        font-size: $font-size-2xl;
        font-weight: $font-weight-bold;
        margin-bottom: $space-xs;
        
        svg {
            width: 28px;
            height: 28px;
            color: var(--primary-start);
        }
    }
    
    .page-desc {
        color: var(--text-secondary);
        font-size: $font-size-sm;
    }
}

.filters-section {
    padding: $space-md $space-lg;
}

.filter-group {
    display: flex;
    gap: $space-md;
    flex-wrap: wrap;
    
    .filter-input { width: 180px; }
    .filter-select { width: 140px; }
    .status-select { width: 180px; }
}

.status-option {
    &.accepted { color: $success; }
    &.wrong, &.error { color: $danger; }
    &.tle { color: $warning; }
}

.table-container {
    padding: 0;
    overflow: hidden;
}

.code-expand {
    padding: $space-md;
    background: var(--bg-primary);
    border-radius: $radius-md;
    margin: $space-sm;
    border: 1px solid var(--border-color);
}

.time-cell {
    font-size: $font-size-xs;
    color: var(--text-muted);
}

.id-cell {
    font-size: $font-size-sm;
    color: var(--text-secondary);
    font-family: $font-mono;
}

.problem-link {
    color: var(--text-primary);
    font-weight: $font-weight-medium;
    cursor: pointer;
    transition: color $transition-fast;
    
    &:hover {
        color: var(--primary-start);
    }
}

.user-cell {
    color: var(--text-secondary);
    font-size: $font-size-sm;
}

.lang-tag {
    padding: $space-xs $space-sm;
    border-radius: $radius-sm;
    font-size: $font-size-xs;
    font-weight: $font-weight-medium;
    
    &.cpp {
        background: rgba(#00599c, 0.15);
        color: #00599c;
    }
    &.java {
        background: rgba(#f8981d, 0.15);
        color: #f8981d;
    }
    &.python {
        background: rgba(#3776ab, 0.15);
        color: #3776ab;
    }
}

.status-tag {
    padding: $space-xs $space-sm;
    border-radius: $radius-sm;
    font-size: $font-size-xs;
    font-weight: $font-weight-semibold;
    
    &.accepted {
        background: $success-light;
        color: $success;
    }
    &.wrong, &.error {
        background: $danger-light;
        color: $danger;
    }
    &.tle {
        background: $warning-light;
        color: $warning;
    }
    &.pending {
        background: var(--glass-bg);
        color: var(--text-muted);
    }
}

.metric-cell {
    font-size: $font-size-xs;
    color: var(--text-muted);
    font-family: $font-mono;
}

.pagination-container {
    padding: $space-lg;
    display: flex;
    justify-content: center;
    border-top: 1px solid var(--border-color);
}
</style>