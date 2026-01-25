<template>
    <div class="rankings-page">
        <!-- Page Header -->
        <div class="page-header">
            <h1 class="page-title">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M6 9H4.5a2.5 2.5 0 010-5H6"/>
                    <path d="M18 9h1.5a2.5 2.5 0 000-5H18"/>
                    <path d="M4 22h16"/>
                    <path d="M10 14.66V17c0 .55-.47.98-.97 1.21C7.85 18.75 7 20.24 7 22"/>
                    <path d="M14 14.66V17c0 .55.47.98.97 1.21C16.15 18.75 17 20.24 17 22"/>
                    <path d="M18 2H6v7a6 6 0 0012 0V2z"/>
                </svg>
                排行榜
            </h1>
            <p class="page-desc">展示最优秀的编程高手</p>
        </div>

        <!-- Top 3 Podium removed as requested -->
        


        <!-- Rankings Table -->
        <div class="table-container card-glass">
            <el-table 
                :data="rankings" 
                class="rankings-table"
                v-loading="loading"
            >
                <el-table-column label="排名" width="100" align="center">
                    <template #default="{ row }">
                        <div class="rank-cell">
                            <span v-if="row.rank <= 3" class="rank-medal" :class="`rank-${row.rank}`">
                                <svg viewBox="0 0 24 24" fill="currentColor">
                                    <path d="M6 9H4.5a2.5 2.5 0 010-5H6M18 9h1.5a2.5 2.5 0 000-5H18M4 22h16M10 14.66V17c0 .55-.47.98-.97 1.21C7.85 18.75 7 20.24 7 22M14 14.66V17c0 .55.47.98.97 1.21C16.15 18.75 17 20.24 17 22M18 2H6v7a6 6 0 0012 0V2z"/>
                                </svg>
                            </span>
                            <span v-else class="rank-number">{{ row.rank }}</span>
                        </div>
                    </template>
                </el-table-column>

                <el-table-column label="用户" min-width="200">
                    <template #default="{ row }">
                        <div class="user-cell">
                            <el-avatar :size="40" :src="row.avatar ? '/api' + row.avatar : ''" class="user-avatar">
                                {{ row.username?.charAt(0) }}
                            </el-avatar>
                            <span class="username">{{ row.username }}</span>
                        </div>
                    </template>
                </el-table-column>

                <el-table-column label="积分" width="150" align="center" sortable>
                    <template #default="{ row }">
                        <span class="score" :class="getScoreClass(row.score)">{{ row.score }}</span>
                    </template>
                </el-table-column>

                <el-table-column label="常用语言" width="130" align="center">
                    <template #default="{ row }">
                        <span class="lang-tag" :class="row.mostUsedLanguage">
                            {{ formatLanguage(row.mostUsedLanguage) }}
                        </span>
                    </template>
                </el-table-column>

                <el-table-column label="解题数" width="200" align="center">
                    <template #default="{ row }">
                        <div class="solved-cell">
                            <span class="solved-tag easy">{{ row.easySolve }}</span>
                            <span class="solved-tag medium">{{ row.middleSolve }}</span>
                            <span class="solved-tag hard">{{ row.hardSolve }}</span>
                        </div>
                    </template>
                </el-table-column>
            </el-table>

            <div class="pagination-container">
                <el-pagination 
                    background 
                    layout="prev, pager, next" 
                    :total="total" 
                    :page-size="pageSize"
                    @current-change="handlePageChange" 
                />
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElTable, ElTableColumn, ElPagination, ElAvatar } from 'element-plus';
import { reqRankings } from '@/api/user';

interface RankItem {
    rank: number;
    userId: number;
    username: string;
    avatar: string;
    score: number;
    mostUsedLanguage: 'java' | 'python' | 'cpp';
    easySolve: number;
    middleSolve: number;
    hardSolve: number;
}

const loading = ref(true);
const rankings = ref<RankItem[]>([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(20);

const fetchRankings = async (page: number) => {
    loading.value = true;
    const res = await reqRankings({ pageNo: page, pageSize: pageSize.value });
    if (res.data.code === 200) {
        rankings.value = res.data.data.list;
        total.value = res.data.data.total;
    }
    loading.value = false;
};

const getScoreClass = (score: number) => {
    if (score >= 1700) return 'legendary';
    if (score >= 1500) return 'epic';
    if (score >= 1300) return 'rare';
    if (score >= 1100) return 'good';
    return 'normal';
};

const formatLanguage = (lang: string) => {
    const map: Record<string, string> = { 'cpp': 'C++', 'java': 'Java', 'python': 'Python' };
    return map[lang] || lang?.toUpperCase();
};

const handlePageChange = (page: number) => {
    currentPage.value = page;
    fetchRankings(page);
};

onMounted(() => fetchRankings(currentPage.value));
</script>

<style lang="scss" scoped>


.rankings-page {
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
            color: #ffd700;
        }
    }
    
    .page-desc {
        color: var(--text-secondary);
        font-size: $font-size-sm;
    }
}

// Podium
.podium-section {
    display: flex;
    justify-content: center;
    align-items: flex-end;
    gap: $space-lg;
    padding: $space-xl 0;
}

.podium-card {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: $space-lg;
    background: var(--bg-card);
    border: 1px solid var(--border-color);
    border-radius: $radius-lg;
    position: relative;
    transition: all $transition-normal;
    
    &:hover {
        transform: translateY(-5px);
        box-shadow: var(--shadow-lg);
    }
    
    .rank-badge {
        position: absolute;
        top: -12px;
        width: 24px;
        height: 24px;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: $radius-full;
        font-size: $font-size-sm;
        font-weight: $font-weight-bold;
        color: white;
    }
    
    &.first {
        padding-bottom: $space-2xl;
        .rank-badge { background: #ffd700; }
        .crown {
            position: absolute;
            top: -35px;
            color: #ffd700;
            svg { width: 32px; height: 32px; }
        }
    }
    &.second .rank-badge { background: #c0c0c0; }
    &.third .rank-badge { background: #cd7f32; }
    
    .podium-avatar {
        margin-bottom: $space-sm;
        border: 3px solid var(--border-light);
        background: var(--primary-gradient);
        color: white;
        font-weight: $font-weight-bold;
    }
    
    .podium-name {
        font-weight: $font-weight-semibold;
        color: var(--text-primary);
        margin-bottom: $space-xs;
    }
    
    .podium-score {
        font-size: $font-size-sm;
        color: var(--text-muted);
    }
}

// Table
.table-container { padding: 0; overflow: hidden; }

.rank-cell {
    display: flex;
    justify-content: center;
    
    .rank-medal {
        svg { width: 24px; height: 24px; }
        &.rank-1 { color: #ffd700; }
        &.rank-2 { color: #c0c0c0; }
        &.rank-3 { color: #cd7f32; }
    }
    
    .rank-number {
        font-weight: $font-weight-bold;
        color: var(--text-secondary);
    }
}

.user-cell {
    display: flex;
    align-items: center;
    gap: $space-md;
    
    .user-avatar {
        background: var(--primary-gradient);
        color: white;
        font-weight: $font-weight-semibold;
    }
    
    .username { font-weight: $font-weight-medium; }
}

.score {
    font-weight: $font-weight-bold;
    &.legendary { color: #a855f7; }
    &.epic { color: #f97316; }
    &.rare { color: #3b82f6; }
    &.good { color: #22c55e; }
    &.normal { color: var(--text-muted); }
}

.lang-tag {
    padding: $space-xs $space-sm;
    border-radius: $radius-sm;
    font-size: $font-size-xs;
    font-weight: $font-weight-medium;
    &.cpp { background: rgba(#00599c, 0.15); color: #00599c; }
    &.java { background: rgba(#f8981d, 0.15); color: #f8981d; }
    &.python { background: rgba(#3776ab, 0.15); color: #3776ab; }
}

.solved-cell {
    display: flex;
    justify-content: center;
    gap: $space-sm;
}

.solved-tag {
    padding: $space-xs $space-sm;
    border-radius: $radius-sm;
    font-size: $font-size-xs;
    font-weight: $font-weight-medium;
    &.easy { background: rgba($success, 0.15); color: $success; }
    &.medium { background: rgba($warning, 0.15); color: $warning; }
    &.hard { background: rgba($danger, 0.15); color: $danger; }
}

.pagination-container {
    padding: $space-lg;
    display: flex;
    justify-content: center;
    border-top: 1px solid var(--border-color);
}
</style>