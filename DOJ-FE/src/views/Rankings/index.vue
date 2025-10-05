<template>
    <div class="rankings-page">
        <el-card class="rank-card">
            <template #header>
                <div class="card-header">
                    <el-icon :size="30" color="#f1c40f">
                        <Trophy />
                    </el-icon>
                    <h1>用户排行榜</h1>
                </div>
            </template>

            <el-table :data="rankings" style="width: 100%" v-loading="loading" stripe class="rank-table">

                <el-table-column label="排名" width="120" align="center">
                    <template #default="{ row }">
                        <div class="rank-cell">
                            <el-icon v-if="row.rank <= 3" :class="`rank-icon rank-${row.rank}`">
                                <Trophy />
                            </el-icon>
                            <span class="rank-number">{{ row.rank }}</span>
                        </div>
                    </template>
                </el-table-column>

                <el-table-column label="用户" min-width="200">
                    <template #default="{ row }">
                        <div class="user-info-cell">
                            <el-avatar :size="40" :src="'/api' + row.avatar" />
                            <span class="username">{{ row.username }}</span>
                        </div>
                    </template>
                </el-table-column>

                <el-table-column label="分数" width="250" align="center" sortable>
                    <template #default="{ row }">
                        <span :class="getScoreClass(row.score)">{{ row.score }}</span>
                    </template>
                </el-table-column>

                <el-table-column label="常用语言" width="150" align="center">
                    <template #default="{ row }">
                        <div :class="['language-cell', `lang-${row.mostUsedLanguage}`]">
                            <i :class="getLangIconClass(row.mostUsedLanguage)"></i>
                            <span>{{ row.mostUsedLanguage.toUpperCase() }}</span>
                        </div>
                    </template>
                </el-table-column>

                <el-table-column label="解题数 (易/中/难)" width="240" align="center">
                    <template #default="{ row }">
                        <div class="solved-cell">
                            <el-tag type="success" effect="light" round>{{ row.easySolve }}</el-tag>
                            <el-tag type="warning" effect="light" round>{{ row.middleSolve }}</el-tag>
                            <el-tag type="danger" effect="light" round>{{ row.hardSolve }}</el-tag>
                        </div>
                    </template>
                </el-table-column>

            </el-table>

            <div class="pagination-container">
                <el-pagination background layout="prev, pager, next" :total="total" :page-size="pageSize"
                    @current-change="handlePageChange" />
            </div>
        </el-card>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElCard, ElTable, ElTableColumn, ElPagination, ElAvatar, ElTag, ElIcon, vLoading } from 'element-plus';
import { Trophy } from '@element-plus/icons-vue';
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
    console.log(res);
    if (res.data.code === 200) {
        rankings.value = res.data.data.list;
        total.value = res.data.data.total;
    }
    loading.value = false;
};

const getScoreClass = (score: number) => {
    if (score >= 1700) return 'score-tier-1'; // 传说
    if (score >= 1500) return 'score-tier-2'; // 史诗
    if (score >= 1300) return 'score-tier-3'; // 稀有
    if (score >= 1100) return 'score-tier-4'; // 优秀
    return 'score-tier-5'; // 普通
};

const getLangIconClass = (lang: string) => {
    const baseClass = 'lang-icon';
    if (lang === 'java') return `${baseClass} fa-brands fa-java`;
    if (lang === 'python') return `${baseClass} fa-brands fa-python`;
    if (lang === 'cpp') return `${baseClass} fa-brands fa-cuttlefish`;
    return baseClass;
};

const handlePageChange = (page: number) => {
    currentPage.value = page;
    fetchRankings(page);
};

onMounted(() => {
    fetchRankings(currentPage.value);
});
</script>

<style lang="scss" scoped>
.rankings-page {
    padding: 24px;
    background-color: #f0f2f5;
    min-height: calc(100vh - 50px);
}

.rank-card {
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-header {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 10px;

    h1 {
        margin: 0;
        font-size: 24px;
        color: #333;
    }
}

.rank-table {
    :deep(.el-table__header-wrapper) {
        th {
            background-color: #fafafa !important;
            color: #606266;
            font-weight: 600;
        }
    }
}

.rank-cell {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;

    .rank-icon {
        font-size: 24px;

        &.rank-1 {
            color: #ffd700;
        }

        // Gold
        &.rank-2 {
            color: #c0c0c0;
        }

        // Silver
        &.rank-3 {
            color: #cd7f32;
        }

        // Bronze
    }

    .rank-number {
        font-weight: bold;
        font-size: 1.2em;
    }
}

.user-info-cell {
    display: flex;
    align-items: center;

    .username {
        margin-left: 12px;
        font-weight: 500;
        color: #303133;
    }
}

.language-cell {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 6px;
    font-weight: 500;

    &.lang-java {
        color: #f8981d;
    }

    &.lang-python {
        color: #3776ab;
    }

    &.lang-cpp {
        color: #850505;
    }

    .lang-icon {
        font-size: 22px;
    }
}

.solved-cell {
    display: flex;
    justify-content: center;
    gap: 8px;
}

.pagination-container {
    margin-top: 24px;
    display: flex;
    justify-content: center;
}

// Score Tiers
.score-tier-1 {
    font-weight: bold;
    color: #a335ee;
}

// 传说紫
.score-tier-2 {
    font-weight: bold;
    color: #ff8000;
}

// 史诗橙
.score-tier-3 {
    font-weight: bold;
    color: #0070dd;
}

// 稀有蓝
.score-tier-4 {
    font-weight: bold;
    color: #19be6b;
}

// 优秀绿
.score-tier-5 {
    font-weight: bold;
    color: #909399;
}

// 普通灰</style>