<script lang="ts" setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

// Stats data (can be fetched from API)
const stats = ref([
    { label: '累计提交', value: '526,262', icon: 'upload' },
    { label: '今日提交', value: '128', icon: 'today' },
    { label: '题目总数', value: '1,024', icon: 'book' },
    { label: '活跃用户', value: '3,892', icon: 'users' },
]);

// Announcements
import { reqAnnouncementList } from '@/api/announcement';
import { computed } from 'vue';

interface AnnouncementItem {
    id: number;
    title: string;
    content: string;
    date: string;
    isNew: boolean;
}

const announcements = ref<AnnouncementItem[]>([]);
const currentPage = ref(1);
const pageSize = 6; // Adjust based on height match
const dialogVisible = ref(false);
const currentAnnouncement = ref<AnnouncementItem | null>(null);

const totalPages = computed(() => Math.ceil(announcements.value.length / pageSize));
const displayedAnnouncements = computed(() => {
    const start = (currentPage.value - 1) * pageSize;
    return announcements.value.slice(start, start + pageSize);
});

const showAnnouncement = (item: AnnouncementItem) => {
    currentAnnouncement.value = item;
    dialogVisible.value = true;
};

const formatDate = (dateStr: string) => {
    const d = new Date(dateStr);
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}/${month}/${day}`;
};

const fetchAnnouncements = async () => {
    try {
        const res = await reqAnnouncementList();
        if (res.data.code === 200) {
            announcements.value = res.data.data.map((item: any) => ({
                id: item.id,
                title: item.title,
                content: item.content,
                date: formatDate(item.createTime),
                isNew: (Date.now() - new Date(item.createTime).getTime()) < 7 * 24 * 60 * 60 * 1000 // New if within 7 days
            }));
        }
    } catch (e) {
        console.error('Failed to fetch announcements', e);
    }
};

// Features (Updated content)
const features = ref([
    { 
        icon: 'trophy', 
        title: '排行榜', 
        desc: '查看顶尖选手排名，追逐榜单荣耀',
        link: '/rankings'
    },
    { 
        icon: 'fire', 
        title: '热门题目', 
        desc: '挑战最受关注的经典算法题目',
        link: '/problem'
    },
    { 
        icon: 'calendar', 
        title: '近期比赛', 
        desc: '参与在线竞赛，实时检验实力',
        link: '/status' // Placeholder
    },
]);

onMounted(() => {
    fetchAnnouncements();
});

const goToProblems = () => {
    router.push('/problem');
};

const goToEditor = () => {
    router.push('/online');
};
</script>

<template>
    <div class="home-page">
        <!-- Hero Section -->
        <section class="hero animate-fade-in-up">
            <div class="hero-content">
                <h1 class="hero-title">
                    <span class="text-gradient">Duck Online Judge</span>
                </h1>
                <p class="hero-subtitle">
                    现代化的在线编程判题平台，助你提升算法能力
                </p>
                <div class="hero-actions">
                    <button class="btn-primary btn-lg" @click="goToProblems">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M4 19.5A2.5 2.5 0 016.5 17H20"/>
                            <path d="M6.5 2H20v20H6.5A2.5 2.5 0 014 19.5v-15A2.5 2.5 0 016.5 2z"/>
                        </svg>
                        开始刷题
                    </button>
                    <button class="btn-secondary btn-lg" @click="goToEditor">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <polyline points="16 18 22 12 16 6"/>
                            <polyline points="8 6 2 12 8 18"/>
                        </svg>
                        在线编程
                    </button>
                </div>
            </div>
            
            <!-- Floating decoration -->
            <div class="hero-decoration">
                <div class="floating-card card-1">
                    <span class="status accepted">AC</span>
                </div>
                <div class="floating-card card-2">
                    <span class="code-snippet">&lt;/&gt;</span>
                </div>
                <div class="floating-card card-3">
                    <span class="status runtime">100ms</span>
                </div>
            </div>
        </section>

        <!-- Stats Section -->
        <section class="stats-section">
            <div class="stats-grid">
                <div v-for="stat in stats" :key="stat.label" class="stat-card card-glass">
                    <div class="stat-icon">
                        <!-- Upload icon -->
                        <svg v-if="stat.icon === 'upload'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M21 15v4a2 2 0 01-2 2H5a2 2 0 01-2-2v-4"/>
                            <polyline points="17 8 12 3 7 8"/>
                            <line x1="12" y1="3" x2="12" y2="15"/>
                        </svg>
                        <!-- Today icon -->
                        <svg v-if="stat.icon === 'today'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
                            <line x1="16" y1="2" x2="16" y2="6"/>
                            <line x1="8" y1="2" x2="8" y2="6"/>
                            <line x1="3" y1="10" x2="21" y2="10"/>
                        </svg>
                        <!-- Book icon -->
                        <svg v-if="stat.icon === 'book'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M4 19.5A2.5 2.5 0 016.5 17H20"/>
                            <path d="M6.5 2H20v20H6.5A2.5 2.5 0 014 19.5v-15A2.5 2.5 0 016.5 2z"/>
                        </svg>
                        <!-- Users icon -->
                        <svg v-if="stat.icon === 'users'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/>
                            <circle cx="9" cy="7" r="4"/>
                            <path d="M23 21v-2a4 4 0 00-3-3.87"/>
                            <path d="M16 3.13a4 4 0 010 7.75"/>
                        </svg>
                    </div>
                    <div class="stat-info">
                        <span class="stat-value">{{ stat.value }}</span>
                        <span class="stat-label">{{ stat.label }}</span>
                    </div>
                </div>
            </div>
        </section>

        <!-- Main Content -->
        <section class="main-content">
            <div class="content-grid">
                <!-- Announcements -->
                <div class="announcements-card card-glass">
                    <div class="card-header">
                        <h2>
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M22 17H2a3 3 0 003 3h14a3 3 0 003-3zM6 5a3 3 0 00-3 3v9h18V8a3 3 0 00-3-3H6z"/>
                            </svg>
                            公告
                        </h2>
                    </div>
                    <div class="card-body">
                        <ul class="announcement-list">
                            <li v-for="item in displayedAnnouncements" :key="item.id" class="announcement-item">
                                <a href="#" class="announcement-link" @click.prevent="showAnnouncement(item)">
                                    <span class="announcement-title">
                                        {{ item.title }}
                                        <span v-if="item.isNew" class="badge-new">NEW</span>
                                    </span>
                                    <span class="announcement-date">{{ item.date }}</span>
                                </a>
                            </li>
                        </ul>
                        <div class="pagination-mini" v-if="totalPages > 1">
                            <button class="page-btn" :disabled="currentPage === 1" @click="currentPage--">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="15 18 9 12 15 6"/></svg>
                            </button>
                            <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
                            <button class="page-btn" :disabled="currentPage === totalPages" @click="currentPage++">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 18 15 12 9 6"/></svg>
                            </button>
                        </div>
                    </div>
                </div>

                <!-- Features -->
                <div class="features-section">
                    <div v-for="feature in features" :key="feature.title" class="feature-card card-glass" @click="router.push(feature.link)" style="cursor: pointer;">
                        <div class="feature-icon">
                            <!-- Trophy icon -->
                            <svg v-if="feature.icon === 'trophy'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M6 9H4.5a2.5 2.5 0 010-5H6"/>
                                <path d="M18 9h1.5a2.5 2.5 0 000-5H18"/>
                                <path d="M4 22h16"/>
                                <path d="M10 14.66V17c0 .55-.47.98-.97 1.21C7.85 18.75 7 20.24 7 22"/>
                                <path d="M14 14.66V17c0 .55.47.98.97 1.21C16.15 18.75 17 20.24 17 22"/>
                                <path d="M18 2H6v7a6 6 0 0012 0V2z"/>
                            </svg>
                            <!-- Fire icon -->
                            <svg v-if="feature.icon === 'fire'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M8.5 14.5A2.5 2.5 0 0 0 11 12c0-1.38-.5-2-1-3-1.072-2.143-.224-4.054 2-6 .5 2.5 2 4.9 4 6.5 2 1.6 3 3.5 3 5.5a7 7 0 1 1-14 0c0-1.1.2-2.2.5-3.3a7 7 0 0 0 12.1 7.1"/>
                            </svg>
                            <!-- Calendar icon -->
                            <svg v-if="feature.icon === 'calendar'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
                                <line x1="16" y1="2" x2="16" y2="6"/>
                                <line x1="8" y1="2" x2="8" y2="6"/>
                                <line x1="3" y1="10" x2="21" y2="10"/>
                            </svg>
                        </div>
                        <h3>{{ feature.title }}</h3>
                        <p>{{ feature.desc }}</p>
                    </div>
                </div>
            </div>
        </section>

        <!-- Announcement Dialog -->
        <el-dialog
            v-model="dialogVisible"
            :title="currentAnnouncement?.title"
            width="600px"
            class="announcement-dialog"
        >
            <div class="announcement-content">
                <div class="announcement-meta">
                    <span>发布时间：{{ currentAnnouncement?.date }}</span>
                </div>
                <div class="announcement-body">
                    {{ currentAnnouncement?.content }}
                </div>
            </div>
        </el-dialog>
    </div>
</template>

<style scoped lang="scss">


.home-page {
    display: flex;
    flex-direction: column;
    gap: $space-2xl;
}

// Hero Section
.hero {
    position: relative;
    text-align: center;
    padding: $space-3xl 0;
    
    .hero-content {
        position: relative;
        z-index: 1;
    }
    
    .hero-title {
        font-size: clamp($font-size-4xl, 8vw, 72px);
        font-weight: $font-weight-bold;
        margin-bottom: $space-md;
        line-height: 1.1;
    }
    
    .hero-subtitle {
        font-size: $font-size-xl;
        color: var(--text-secondary);
        margin-bottom: $space-xl;
        max-width: 500px;
        margin-left: auto;
        margin-right: auto;
    }
    
    .hero-actions {
        display: flex;
        gap: $space-md;
        justify-content: center;
        flex-wrap: wrap;
        
        .btn-lg {
            padding: $space-md $space-xl;
            font-size: $font-size-base;
            display: flex;
            align-items: center;
            gap: $space-sm;
            
            svg {
                width: 20px;
                height: 20px;
            }
        }
    }
}

// Hero floating cards
.hero-decoration {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    pointer-events: none;
    overflow: hidden;
}

.floating-card {
    position: absolute;
    padding: $space-sm $space-md;
    background: var(--glass-bg);
    backdrop-filter: blur(8px);
    border: 1px solid var(--glass-border);
    border-radius: $radius-md;
    font-weight: $font-weight-semibold;
    animation: float 6s ease-in-out infinite;
    
    &.card-1 {
        top: 20%;
        left: 10%;
        animation-delay: 0s;
        
        .accepted {
            color: $success;
        }
    }
    
    &.card-2 {
        top: 30%;
        right: 15%;
        animation-delay: 2s;
        
        .code-snippet {
            @include gradient-text;
            font-family: $font-mono;
        }
    }
    
    &.card-3 {
        bottom: 25%;
        left: 20%;
        animation-delay: 4s;
        
        .runtime {
            color: $info;
        }
    }
}

@keyframes float {
    0%, 100% { transform: translateY(0px); }
    50% { transform: translateY(-20px); }
}

// Stats Section
.stats-section {
    .stats-grid {
        display: grid;
        grid-template-columns: repeat(4, 1fr);
        gap: $space-lg;
        
        @include mobile {
            grid-template-columns: repeat(2, 1fr);
        }
    }
}

.stat-card {
    display: flex;
    align-items: center;
    gap: $space-md;
    padding: $space-lg;
    @include hover-glow;
    
    .stat-icon {
        width: 48px;
        height: 48px;
        display: flex;
        align-items: center;
        justify-content: center;
        background: var(--primary-gradient);
        border-radius: $radius-md;
        
        svg {
            width: 24px;
            height: 24px;
            color: white;
        }
    }
    
    .stat-info {
        display: flex;
        flex-direction: column;
    }
    
    .stat-value {
        font-size: $font-size-2xl;
        font-weight: $font-weight-bold;
        color: var(--text-primary);
    }
    
    .stat-label {
        font-size: $font-size-sm;
        color: var(--text-muted);
    }
}

// Main Content
.content-grid {
    display: grid;
    grid-template-columns: 2fr 1fr;
    gap: $space-xl;
    
    @include mobile {
        grid-template-columns: 1fr;
    }
}

// Announcements
.announcements-card {
    .card-header {
        padding: $space-lg;
        border-bottom: 1px solid var(--border-color);
        
        h2 {
            display: flex;
            align-items: center;
            gap: $space-sm;
            font-size: $font-size-lg;
            font-weight: $font-weight-semibold;
            margin: 0;
            
            svg {
                width: 20px;
                height: 20px;
                color: var(--primary-start);
            }
        }
    }
    
    .card-body {
        padding: 0;
    }

    padding-bottom: 0;
}

.announcement-list {
    list-style: none;
    margin: 0;
    padding: 0;
}

.announcement-item {
    border-bottom: 1px solid var(--border-color);
    
    &:last-child {
        border-bottom: none;
    }
}

.announcement-link {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: $space-md $space-lg;
    color: var(--text-primary);
    text-decoration: none;
    transition: background $transition-fast;
    
    &:hover {
        background: var(--glass-bg);
    }
    
    .announcement-title {
        display: flex;
        align-items: center;
        gap: $space-sm;
        font-size: $font-size-sm;
    }
    
    .badge-new {
        padding: 2px 6px;
        font-size: 10px;
        font-weight: $font-weight-bold;
        background: var(--primary-gradient);
        color: white;
        border-radius: $radius-sm;
    }
    
    .announcement-date {
        font-size: $font-size-xs;
        color: var(--text-muted);
    }
}

// Features
.features-section {
    display: flex;
    flex-direction: column;
    gap: $space-md;
}

.feature-card {
    padding: $space-lg;
    @include hover-glow;
    
    .feature-icon {
        width: 40px;
        height: 40px;
        display: flex;
        align-items: center;
        justify-content: center;
        background: rgba(102, 126, 234, 0.1);
        border-radius: $radius-md;
        margin-bottom: $space-md;
        
        svg {
            width: 20px;
            height: 20px;
            color: var(--primary-start);
        }
    }
    
    h3 {
        font-size: $font-size-base;
        font-weight: $font-weight-semibold;
        margin-bottom: $space-xs;
    }
    
    p {
        font-size: $font-size-sm;
        color: var(--text-secondary);
        margin: 0;
    }
}
// Pagination
.pagination-mini {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: $space-md;
    padding: $space-md;
    border-top: 1px solid var(--border-color);
    
    .page-btn {
        background: transparent;
        border: none;
        color: var(--text-secondary);
        cursor: pointer;
        padding: 4px;
        border-radius: $radius-sm;
        display: flex;
        align-items: center;
        
        &:disabled {
            opacity: 0.3;
            cursor: not-allowed;
        }
        
        &:hover:not(:disabled) {
            background: var(--bg-hover);
            color: var(--primary-start);
        }
        
        svg {
            width: 16px;
            height: 16px;
        }
    }
    
    .page-info {
        font-size: $font-size-xs;
        color: var(--text-muted);
    }
}

// Dialog Styles
.announcement-content {
    .announcement-meta {
        margin-bottom: $space-md;
        color: var(--text-muted);
        font-size: $font-size-sm;
        border-bottom: 1px solid var(--border-color);
        padding-bottom: $space-sm;
    }
    
    .announcement-body {
        line-height: $line-height-relaxed;
        color: var(--text-primary);
        white-space: pre-wrap;
    }
}
</style>