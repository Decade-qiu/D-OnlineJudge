<template>
    <div class="online-editor-page">
        <!-- Page Header -->
        <div class="page-header">
            <h1 class="page-title">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polyline points="16 18 22 12 16 6"/>
                    <polyline points="8 6 2 12 8 18"/>
                </svg>
                在线编程
            </h1>
            <p class="page-desc">随时随地编写并运行代码</p>
        </div>

        <div class="editor-container card-glass">
            <Editor :config="config" />
        </div>
    </div>
</template>

<script setup lang='ts'>
import { ref } from 'vue';
import { configType } from '@/components/CodeEditor/index.vue';
import Editor from '@/components/CodeEditor/index.vue';

const config = ref<configType>({
    tabSize: 4,
    disabled: false,
    height: '65vh',
    width: '100%',
    language: 'cpp',
    theme: 'default',
    fontSize: 16,
    editorType: 'code'
})
</script>

<style scoped lang="scss">


.online-editor-page {
    display: flex;
    flex-direction: column;
    gap: $space-lg;
    max-width: 1400px;
    margin: 0 auto;
    width: 100%;
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

.editor-container {
    padding: $space-md;
    border-radius: $radius-lg;
    overflow: hidden;
}
</style>