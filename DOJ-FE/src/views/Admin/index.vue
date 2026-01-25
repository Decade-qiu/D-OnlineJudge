<template>
    <div class="admin-page">
        <div class="admin-header">
            <h1>后台管理</h1>
            <div class="header-actions">
                <el-button @click="router.push('/')">回到首页</el-button>
            </div>
        </div>
        
        <div class="admin-content card-glass">
            <el-tabs v-model="activeTab">
                <el-tab-pane label="题目管理" name="problem">
                    <ProblemManager />
                </el-tab-pane>
                <!-- Add more tabs later: Announcement, User, etc. -->
            </el-tabs>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import ProblemManager from './Problem/index.vue';

const router = useRouter();
const activeTab = ref('problem');
</script>

<style scoped lang="scss">


.admin-page {
    max-width: 1400px;
    margin: 0 auto;
    padding: $space-lg;
    display: flex;
    flex-direction: column;
    gap: $space-lg;
}

.admin-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    
    h1 {
        font-size: $font-size-2xl;
        font-weight: bold;
        @include gradient-text;
    }
}

.admin-content {
    background: var(--bg-elevated);
    padding: $space-lg;
    border-radius: $radius-lg;
    min-height: 500px;
}
</style>
