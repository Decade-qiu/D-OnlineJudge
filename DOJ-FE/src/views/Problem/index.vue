<template>
    <div class="problem-page">
        <!-- Page Header -->
        <div class="page-header">
            <h1 class="page-title">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M4 19.5A2.5 2.5 0 016.5 17H20"/>
                    <path d="M6.5 2H20v20H6.5A2.5 2.5 0 014 19.5v-15A2.5 2.5 0 016.5 2z"/>
                </svg>
                题目列表
            </h1>
            <p class="page-desc">共 {{ total }} 道题目，开始你的编程之旅</p>
        </div>

        <!-- Filters -->
        <div class="filters-section card-glass">
            <div class="filter-group">
                <el-select v-model="searchDifficulty" placeholder="难度" @clear="searchDifficulty = null" clearable class="filter-select">
                    <el-option label="简单" value="简单">
                        <span class="difficulty-option easy">简单</span>
                    </el-option>
                    <el-option label="中等" value="中等">
                        <span class="difficulty-option medium">中等</span>
                    </el-option>
                    <el-option label="困难" value="困难">
                        <span class="difficulty-option hard">困难</span>
                    </el-option>
                </el-select>
                
                <el-select v-model="searchStatus" placeholder="状态" clearable @clear="searchStatus = null" class="filter-select">
                    <el-option label="已解决" value="已解决">
                        <span class="status-option passed">✓ 已解决</span>
                    </el-option>
                    <el-option label="未开始" value="未开始">
                        <span class="status-option">未开始</span>
                    </el-option>
                    <el-option label="尝试中" value="尝试中">
                        <span class="status-option trying">尝试中</span>
                    </el-option>
                </el-select>
                
                <el-input 
                    v-model="searchTag" 
                    placeholder="标签 (用逗号分隔)" 
                    clearable 
                    class="filter-input"
                    :prefix-icon="Tag"
                />
                
                <el-input 
                    v-model="searchName" 
                    placeholder="搜索题目名称或内容..." 
                    clearable 
                    class="filter-input search-input"
                    :prefix-icon="Search"
                />
            </div>
        </div>

        <!-- Problem Table -->
        <div class="table-container card-glass">
            <el-table 
                :data="tableData" 
                :default-sort="{ prop: 'id', order: 'ascending' }"
                class="problem-table"
                v-loading="loading"
            >
                <el-table-column prop="status" label="状态" align="center" width="80">
                    <template v-slot="scope">
                        <div class="status-cell">
                            <span v-if="!scope.row.status || scope.row.status === '未开始'" class="status-icon empty"></span>
                            <span v-else-if="scope.row.status === '已解决'" class="status-icon passed">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
                                    <polyline points="20 6 9 17 4 12"/>
                                </svg>
                            </span>
                            <span v-else-if="scope.row.status === '尝试中'" class="status-icon trying">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <circle cx="12" cy="12" r="10"/>
                                    <line x1="12" y1="8" x2="12" y2="12"/>
                                    <line x1="12" y1="16" x2="12.01" y2="16"/>
                                </svg>
                            </span>
                        </div>
                    </template>
                </el-table-column>
                
                <el-table-column prop="id" label="#" sortable align="center" width="80" />
                
                <el-table-column prop="name" label="题目" min-width="200">
                    <template v-slot="scope">
                        <span class="problem-name" @click="toProblem(scope.row.id)">
                            {{ scope.row.name }}
                        </span>
                    </template>
                </el-table-column>
                
                <el-table-column prop="difficulty" label="难度" align="center" width="100">
                    <template v-slot="scope">
                        <span 
                            class="difficulty-tag"
                            :class="{
                                'easy': scope.row.difficulty === '简单',
                                'medium': scope.row.difficulty === '中等',
                                'hard': scope.row.difficulty === '困难'
                            }"
                        >
                            {{ scope.row.difficulty }}
                        </span>
                    </template>
                </el-table-column>
                
                <el-table-column prop="totalAttempt" label="提交" align="center" width="100">
                    <template v-slot="scope">
                        <span class="attempt-count">{{ scope.row.totalAttempt }}</span>
                    </template>
                </el-table-column>
                
                <el-table-column prop="passRate" label="通过率" sortable width="120" align="center">
                    <template v-slot="scope">
                        <div class="pass-rate">
                            <div class="rate-bar">
                                <div 
                                    class="rate-fill" 
                                    :style="{ width: getPassRate(scope.row) + '%' }"
                                    :class="{ high: getPassRate(scope.row) >= 60, medium: getPassRate(scope.row) >= 30 && getPassRate(scope.row) < 60 }"
                                ></div>
                            </div>
                            <span class="rate-text">{{ formatPassRate(scope.row) }}</span>
                        </div>
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
                    v-model:page-size="pageQueryFrom.pageSize" 
                    v-model:current-page="pageQueryFrom.pageNo"
                    @change="getProblemList"
                />
            </div>
        </div>
    </div>
</template>

<script setup lang='ts'>
import { ElTable, ElTableColumn, ElSelect, ElOption, ElInput, ElPagination } from 'element-plus';
import { Search, PriceTag as Tag } from '@element-plus/icons-vue';
import { ref, onMounted, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { reqProblemPageList } from '@/api/problem';
import type { ProblemType } from '@/api/problem/type';
import type { BasePageQueryForm } from '@/api/base';

const router = useRouter();
const route = useRoute();

const searchDifficulty = ref();
const searchStatus = ref<string | null>(null);
const searchTag = ref();
const searchName = ref();
const loading = ref(false);

watch([searchDifficulty, searchStatus, searchTag, searchName], () => {
    getProblemList();
});

const getPassRate = (row: any) => {
    if (!row.totalAttempt) return 0;
    return (row.totalPass / row.totalAttempt * 100);
};

const formatPassRate = (row: any) => {
    return `${getPassRate(row).toFixed(1)}%`;
};

const tableData = ref<ProblemType[]>([]);
const pageQueryFrom = ref<BasePageQueryForm>({
    pageNo: 1,
    pageSize: 20,
    isAsc: true,
    sortBy: ''
});
const total = ref(0);
const pages = ref(0);
const pageSizes = ref([10, 20, 50, 100]);

const toProblem = (id: number) => {
    router.push("/problem/" + id);
};

const getProblemList = async () => {
    loading.value = true;
    try {
        const tags = searchTag.value ? searchTag.value.split(/[,，]/).map((t: string) => t.trim()).filter((t: string) => t !== '') : [];
        const params: any = {
            ...pageQueryFrom.value,
            name: searchName.value,
            difficulty: searchDifficulty.value,
            status: searchStatus.value
        };
        if (tags.length > 0) {
            params.tags = tags;
        }
        const res = await reqProblemPageList(params);
        const { total: totalV, pages: pagesV, list } = res.data.data;
        total.value = totalV;
        pages.value = pagesV;
        tableData.value = list;
    } finally {
        loading.value = false;
    }
};

onMounted(() => {
    if (route.query.tag) {
        searchTag.value = route.query.tag as string;
    }
    getProblemList();
});
</script>

<style scoped lang="scss">


.problem-page {
    display: flex;
    flex-direction: column;
    gap: $space-lg;
}

// Page Header
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

// Filters
.filters-section {
    padding: $space-md $space-lg;
}

.filter-group {
    display: flex;
    align-items: center;
    gap: $space-md;
    flex-wrap: wrap;
    
    .filter-select {
        width: 120px;
    }
    
    .filter-input {
        width: 180px;
    }
    
    .search-input {
        width: 250px;
        margin-left: auto;
    }
}

.difficulty-option {
    &.easy { color: $difficulty-easy; }
    &.medium { color: $difficulty-medium; }
    &.hard { color: $difficulty-hard; }
}

.status-option {
    &.passed { color: $success; }
    &.trying { color: $warning; }
}

// Table Container
.table-container {
    padding: 0;
    overflow: hidden;
}

.problem-table {
    --el-table-row-hover-bg-color: var(--glass-bg);
    
    :deep(.el-table__header-wrapper) {
        th {
            background: var(--bg-elevated) !important;
            border-bottom: 1px solid var(--border-color) !important;
        }
    }
    
    :deep(.el-table__body-wrapper) {
        td {
            border-bottom: 1px solid var(--border-color) !important;
        }
    }
}

// Status Cell
.status-cell {
    display: flex;
    justify-content: center;
    
    .status-icon {
        width: 20px;
        height: 20px;
        display: flex;
        align-items: center;
        justify-content: center;
        
        svg {
            width: 16px;
            height: 16px;
        }
        
        &.passed {
            color: $success;
        }
        
        &.trying {
            color: $warning;
        }
    }
}

// Problem Name
.problem-name {
    color: var(--text-primary);
    font-weight: $font-weight-medium;
    cursor: pointer;
    transition: color $transition-fast;
    
    &:hover {
        color: var(--primary-start);
    }
}

// Difficulty Tag
.difficulty-tag {
    padding: $space-xs $space-sm;
    border-radius: $radius-sm;
    font-size: $font-size-xs;
    font-weight: $font-weight-medium;
    
    &.easy {
        background: rgba($difficulty-easy, 0.15);
        color: $difficulty-easy;
    }
    
    &.medium {
        background: rgba($difficulty-medium, 0.15);
        color: $difficulty-medium;
    }
    
    &.hard {
        background: rgba($difficulty-hard, 0.15);
        color: $difficulty-hard;
    }
}

// Attempt Count
.attempt-count {
    color: var(--text-secondary);
    font-size: $font-size-sm;
}

// Pass Rate
.pass-rate {
    display: flex;
    align-items: center;
    gap: $space-sm;
    
    .rate-bar {
        flex: 1;
        height: 4px;
        background: var(--border-color);
        border-radius: $radius-full;
        overflow: hidden;
        
        .rate-fill {
            height: 100%;
            background: $danger;
            border-radius: $radius-full;
            transition: width $transition-normal;
            
            &.medium {
                background: $warning;
            }
            
            &.high {
                background: $success;
            }
        }
    }
    
    .rate-text {
        font-size: $font-size-xs;
        color: var(--text-muted);
        min-width: 40px;
        text-align: right;
    }
}

// Pagination
.pagination-container {
    padding: $space-lg;
    display: flex;
    justify-content: center;
    border-top: 1px solid var(--border-color);
}
</style>
