<template>
    <div class="profile-page">
        <!-- Profile Header -->
        <div class="profile-header card-glass">
            <div class="user-intro">
                <el-avatar :size="100" :src="form.avatar ? '/api' + form.avatar : ''" class="profile-avatar">
                    {{ form.username?.charAt(0).toUpperCase() }}
                </el-avatar>
                <div class="user-details">
                    <div class="name-row">
                        <h1 class="username">{{ form.username }}</h1>
                        <span class="gender-icon" v-if="form.gender !== undefined">
                            <svg v-if="form.gender" viewBox="0 0 24 24" fill="none" stroke="#3b82f6" stroke-width="2">
                                <circle cx="10.5" cy="8.5" r="5.5"/>
                                <path d="M16 3l5 0"/>
                                <path d="M21 3l0 5"/>
                                <path d="M14.5 9.5l6.5-6.5"/>
                            </svg>
                            <svg v-else viewBox="0 0 24 24" fill="none" stroke="#ec4899" stroke-width="2">
                                <circle cx="12" cy="8" r="5"/>
                                <path d="M12 13v8"/>
                                <path d="M9 18h6"/>
                            </svg>
                        </span>
                        <el-tag size="small" effect="dark" class="role-tag" v-if="form.role">{{ form.role }}</el-tag>
                    </div>
                    <p class="signature">{{ form.sign || '这个人很懒，什么都没写~' }}</p>
                    <div class="meta-row">
                        <div class="meta-item" v-if="form.school">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M22 10v6M2 10l10-5 10 5-10 5z"/>
                                <path d="M6 12v5c0 1 2 3 6 3s6-2 6-3v-5"/>
                            </svg>
                            {{ form.school }}
                        </div>
                        <div class="meta-item" v-if="form.email">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
                                <polyline points="22,6 12,13 2,6"/>
                            </svg>
                            {{ form.email }}
                        </div>
                        <div class="meta-item link" v-if="form.url">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M10 13a5 5 0 007.54.54l3-3a5 5 0 00-7.07-7.07l-1.72 1.71"/>
                                <path d="M14 11a5 5 0 00-7.54-.54l-3 3a5 5 0 007.07 7.07l1.71-1.71"/>
                            </svg>
                            <a :href="form.url" target="_blank">{{ form.url }}</a>
                        </div>
                    </div>
                </div>
            </div>
            <div class="stats-row">
                <div class="stat-box">
                    <span class="value">{{ form.easySolve + form.middleSolve + form.hardSolve }}</span>
                    <span class="label">解决</span>
                </div>
                <div class="divider"></div>
                <div class="stat-box">
                    <span class="value">{{ form.score }}</span>
                    <span class="label">积分</span>
                </div>
                <div class="divider"></div>
                <div class="stat-box">
                    <span class="value">{{ form.ranks > 0 ? form.ranks : '-' }}</span>
                    <span class="label">排名</span>
                </div>
                <!-- <div class="divider"></div>
                <div class="stat-box">
                    <span class="value">{{ form.fans }}</span>
                    <span class="label">粉丝</span>
                </div> -->
            </div>
        </div>

        <div class="content-grid">
            <!-- Left Column: Charts & StatsDetail -->
            <div class="grid-col left">
                <div class="chart-card card-glass">
                    <h3 class="card-title">数据统计</h3>
                    <div ref="chartRef" class="chart-container"></div>
                </div>
                
                <div class="solved-card card-glass">
                    <h3 class="card-title">解题分布</h3>
                    <div class="distribution-bars">
                        <div class="dist-item">
                            <div class="dist-label">简单</div>
                            <div class="dist-bar-bg">
                                <div class="dist-bar easy" :style="{ width: getPercentage(form.easySolve) }"></div>
                            </div>
                            <div class="dist-val">{{ form.easySolve }}</div>
                        </div>
                        <div class="dist-item">
                            <div class="dist-label">中等</div>
                            <div class="dist-bar-bg">
                                <div class="dist-bar medium" :style="{ width: getPercentage(form.middleSolve) }"></div>
                            </div>
                            <div class="dist-val">{{ form.middleSolve }}</div>
                        </div>
                        <div class="dist-item">
                            <div class="dist-label">困难</div>
                            <div class="dist-bar-bg">
                                <div class="dist-bar hard" :style="{ width: getPercentage(form.hardSolve) }"></div>
                            </div>
                            <div class="dist-val">{{ form.hardSolve }}</div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Right Column: Recent Activity & Solved Problems -->
            <div class="grid-col right">
                <!-- Recent Activity -->
                <div class="activity-card card-glass">
                    <h3 class="card-title">
                        最近提交
                        <span class="view-all" @click="router.push(`/status?userName=${form.username}`)">查看全部</span>
                    </h3>
                    <div class="activity-list" v-loading="loadingActivity">
                        <div v-for="item in recentSubmissions" :key="item.id" class="activity-item">
                            <div class="activity-status" :class="getStatusClass(item.status)">
                                <svg v-if="item.status === 'Accepted'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="20 6 9 17 4 12"/></svg>
                                <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
                            </div>
                            <div class="activity-info">
                                <div class="activity-main">
                                    <span class="prob-name" @click="router.push(`/problem/${item.problemId}`)">{{ item.problemName }}</span>
                                    <span class="time-ago">{{ item.submitTime }}</span>
                                </div>
                                <div class="activity-meta">
                                    <span class="lang-badge">{{ item.language }}</span>
                                    <span class="status-text" :class="getStatusClass(item.status)">{{ item.status }}</span>
                                </div>
                            </div>
                        </div>
                        <div class="empty-tip" v-if="!recentSubmissions.length">暂无提交记录</div>
                    </div>
                </div>

                <!-- Solved Problems -->
                <div class="solved-list-card card-glass">
                    <h3 class="card-title">已解决题目</h3>
                    <div class="solved-tags" v-loading="loadingSolved">
                        <div 
                            v-for="pid in solvedProblems" 
                            :key="pid" 
                            class="solved-tag" 
                            @click="router.push(`/problem/${pid}`)"
                        >
                            {{ pid }}
                        </div>
                        <div class="empty-tip" v-if="!solvedProblems.length">暂无数据</div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, nextTick } from 'vue';
import { useRouter } from 'vue-router';
import { ElAvatar, ElTag } from 'element-plus';
import { reqUserInfo } from '@/api/user';
import { reqSubmissionPageList } from '@/api/submission';
import { reqProblemDetail } from '@/api/problem';
import { useUserStore } from '@/stores/userStore';
import * as echarts from "echarts";

const router = useRouter();
const userStore = useUserStore();
const chartRef = ref();

const form = ref({
    id: 0,
    username: '',
    avatar: '',
    email: '',
    score: 0,
    ranks: 0,
    school: '',
    gender: true,
    easySolve: 0,
    middleSolve: 0,
    hardSolve: 0,
    url: '',
    sign: '',
    fans: 0,
    subscribe: 0,
    role: '',
});

const recentSubmissions = ref<any[]>([]);
const solvedProblems = ref<number[]>([]);
const loadingActivity = ref(false);
const loadingSolved = ref(false);

const getStatusClass = (status: string) => {
    if (status === 'Accepted') return 'accepted';
    if (status === 'Wrong Answer') return 'wrong';
    if (status?.includes('Error')) return 'error';
    if (status?.includes('Exceeded')) return 'tle';
    return 'pending';
};

const getPercentage = (val: number) => {
    const total = (form.value.easySolve + form.value.middleSolve + form.value.hardSolve) || 1;
    return Math.min(100, Math.max(5, (val / total) * 100)) + '%';
}

const generateChart = () => {
    if (!chartRef.value) return;
    const chart = echarts.init(chartRef.value);
    const data = [
        { name: '简单', value: form.value.easySolve },
        { name: '中等', value: form.value.middleSolve },
        { name: '困难', value: form.value.hardSolve },
    ].filter(item => item.value > 0);

    if (data.length === 0) {
        data.push({ name: '暂无数据', value: 1 });
    }

    const option = {
        tooltip: {
            trigger: 'item',
            backgroundColor: 'rgba(15, 15, 35, 0.9)',
            borderColor: 'rgba(255,255,255,0.1)',
            textStyle: { color: '#fff' }
        },
        legend: {
            bottom: '0%',
            left: 'center',
            textStyle: { color: 'var(--text-secondary)' } // Dynamic color handled by CSS vars usually, but echarts needs direct value. 
            // In dark mode this might need adjustment, but text-secondary is usually ok.
        },
        series: [{
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['50%', '45%'],
            avoidLabelOverlap: false,
            itemStyle: { 
                borderRadius: 6, 
                borderColor: 'var(--bg-card)', 
                borderWidth: 2 
            },
            label: { show: false },
            data: data,
            color: data[0].name === '暂无数据' ? ['#333'] : ['#10b981', '#f59e0b', '#ef4444']
        }]
    };
    chart.setOption(option);
    
    // Resize chart on window resize
    window.addEventListener('resize', () => {
        chart.resize();
    });
};

const fetchRecentActivity = async (username: string) => {
    loadingActivity.value = true;
    try {
        const res = await reqSubmissionPageList({
            pageNo: 1,
            pageSize: 5,
            userId: username,
            isAsc: false,
            sortBy: 'id'
        });
        const list = res.data.data.list;
        
        recentSubmissions.value = await Promise.all(list.map(async (item: any) => {
            // Optimistically try to get problem name, though api might return it immediately if joined
            let problemName = item.problemName || String(item.problemId);
            if (!item.problemName) {
                 const pRes = await reqProblemDetail(String(item.problemId));
                 problemName = pRes.data?.data?.name || problemName;
            }
            return {
                id: item.id,
                problemId: item.problemId,
                problemName: problemName,
                status: item.status,
                language: item.language,
                submitTime: new Date(item.submitTime).toLocaleDateString()
            };
        }));
    } finally {
        loadingActivity.value = false;
    }
};

const fetchSolvedProblems = async (username: string) => {
    loadingSolved.value = true;
    try {
        const res = await reqSubmissionPageList({
            pageNo: 1,
            pageSize: 50,
            userId: username,
            status: 'Accepted',
            isAsc: false,
            sortBy: 'id'
        });
        const list = res.data.data.list;
        const ids = new Set(list.map((item: any) => item.problemId));
        solvedProblems.value = Array.from(ids).sort((a: any, b: any) => a - b);
    } finally {
        loadingSolved.value = false;
    }
}

onMounted(async () => {
    const uid = userStore.userInfo!.userId;
    const res = await reqUserInfo(uid);
    form.value = res.data.data;
    
    fetchRecentActivity(form.value.username);
    fetchSolvedProblems(form.value.username);
    
    nextTick(() => generateChart());
});
</script>

<style scoped lang="scss">


.profile-page {
    display: flex;
    flex-direction: column;
    gap: $space-lg;
    max-width: 1200px;
    margin: 0 auto;
    width: 100%;
}

// Profile Header
.profile-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: $space-xl;
    gap: $space-xl;
    
    @include mobile {
        flex-direction: column;
        align-items: flex-start;
    }
}

.user-intro {
    display: flex;
    align-items: center;
    gap: $space-lg;
    
    .profile-avatar {
        background: var(--primary-gradient);
        font-size: 40px;
        font-weight: bold;
        color: white;
        border: 4px solid var(--bg-elevated);
        box-shadow: var(--shadow-md);
    }
    
    @include mobile {
        flex-direction: column;
        align-items: flex-start;
        
        .profile-avatar {
            margin-bottom: $space-md;
        }
    }
}

.user-details {
    .name-row {
        display: flex;
        align-items: center;
        gap: $space-sm;
        margin-bottom: $space-xs;
        
        .username {
            font-size: $font-size-3xl;
            font-weight: 700;
            color: var(--text-primary);
            line-height: 1.2;
        }
        
        .gender-icon svg {
            width: 20px;
            height: 20px;
        }
    }
    
    .signature {
        color: var(--text-secondary);
        margin-bottom: $space-md;
        font-size: $font-size-sm;
    }
    
    .meta-row {
        display: flex;
        flex-wrap: wrap;
        gap: $space-lg;
        
        .meta-item {
            display: flex;
            align-items: center;
            gap: $space-xs;
            color: var(--text-muted);
            font-size: $font-size-sm;
            
            svg { width: 16px; height: 16px; }
            
            &.link a {
                color: var(--primary-start);
                &:hover { text-decoration: underline; }
            }
        }
    }
}

.stats-row {
    display: flex;
    align-items: center;
    gap: $space-xl;
    
    .divider {
        width: 1px;
        height: 40px;
        background: var(--border-color);
    }
    
    .stat-box {
        display: flex;
        flex-direction: column;
        align-items: center;
        min-width: 60px;
        
        .value {
            font-size: $font-size-2xl;
            font-weight: 800;
            color: var(--text-primary);
        }
        
        .label {
            font-size: $font-size-xs;
            color: var(--text-muted);
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
    }
    
    @include mobile {
        width: 100%;
        justify-content: space-around;
        padding-top: $space-md;
        border-top: 1px solid var(--border-color);
    }
}

// Content Grid
.content-grid {
    display: grid;
    grid-template-columns: 350px 1fr;
    gap: $space-lg;
    
    @include mobile {
        grid-template-columns: 1fr;
    }
}

.grid-col {
    display: flex;
    flex-direction: column;
    gap: $space-lg;
}

.card-title {
    font-size: $font-size-lg;
    font-weight: 600;
    margin-bottom: $space-lg;
    padding-bottom: $space-sm;
    border-bottom: 1px solid var(--border-color);
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .view-all {
        font-size: $font-size-xs;
        color: var(--primary-start);
        cursor: pointer;
        font-weight: normal;
        
        &:hover { text-decoration: underline; }
    }
}

.chart-card, .solved-card, .activity-card, .solved-list-card {
    padding: $space-lg;
}

.chart-container {
    height: 250px;
}

// Distribution Bars
.distribution-bars {
    display: flex;
    flex-direction: column;
    gap: $space-md;
    
    .dist-item {
        display: flex;
        align-items: center;
        gap: $space-md;
        font-size: $font-size-sm;
        
        .dist-label {
            width: 40px;
            color: var(--text-secondary);
        }
        
        .dist-bar-bg {
            flex: 1;
            height: 8px;
            background: var(--bg-primary);
            border-radius: $radius-full;
            overflow: hidden;
            
            .dist-bar {
                height: 100%;
                border-radius: $radius-full;
                
                &.easy { background: $success; }
                &.medium { background: $warning; }
                &.hard { background: $danger; }
            }
        }
        
        .dist-val {
            width: 30px;
            text-align: right;
            font-weight: 600;
        }
    }
}

// Activity List
.activity-list {
    display: flex;
    flex-direction: column;
    gap: $space-md;
}

.activity-item {
    display: flex;
    align-items: flex-start;
    gap: $space-md;
    padding-bottom: $space-md;
    border-bottom: 1px solid var(--border-color);
    
    &:last-child {
        border-bottom: none;
        padding-bottom: 0;
    }
    
    .activity-status {
        width: 36px;
        height: 36px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        flex-shrink: 0;
        
        svg { width: 18px; height: 18px; }
        
        &.accepted { background: $success-light; color: $success; }
        &.wrong { background: $danger-light; color: $danger; }
        &.error { background: $danger-light; color: $danger; }
        &.tle { background: $warning-light; color: $warning; }
        &.pending { background: var(--bg-primary); color: var(--text-muted); }
    }
    
    .activity-info {
        flex: 1;
        
        .activity-main {
            display: flex;
            justify-content: space-between;
            margin-bottom: $space-xs;
            
            .prob-name {
                font-weight: 600;
                color: var(--text-primary);
                cursor: pointer;
                &:hover { color: var(--primary-start); }
            }
            
            .time-ago {
                font-size: $font-size-xs;
                color: var(--text-muted);
            }
        }
        
        .activity-meta {
            display: flex;
            gap: $space-md;
            font-size: $font-size-xs;
            
            .lang-badge {
                background: var(--bg-primary);
                padding: 2px 6px;
                border-radius: 4px;
                color: var(--text-secondary);
            }
            
            .status-text {
                &.accepted { color: $success; }
                &.wrong { color: $danger; }
            }
        }
    }
}

// Solved Tags
.solved-tags {
    display: flex;
    flex-wrap: wrap;
    gap: $space-sm;
    
    .solved-tag {
        padding: 4px 10px;
        background: var(--bg-primary);
        border: 1px solid var(--border-color);
        border-radius: $radius-sm;
        font-size: $font-size-sm;
        color: var(--text-primary);
        cursor: pointer;
        transition: all $transition-fast;
        font-family: $font-mono;
        
        &:hover {
            border-color: $success;
            color: $success;
            background: $success-light;
        }
    }
}

.empty-tip {
    color: var(--text-muted);
    font-size: $font-size-sm;
    text-align: center;
    padding: $space-lg;
    width: 100%;
}
</style>