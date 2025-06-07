<template>
    <div class="editor-box">
        <Editor :config="config" />
    </div>
</template>

<script setup lang='ts'>
import { ref } from 'vue';
import { configType } from '@/components/CodeEditor/index.vue';
import Editor from '@/components/CodeEditor/index.vue';

const config = ref<configType>({
    tabSize: 4,
    disabled: false,
    height: '62vh',
    width: '100%',
    language: 'cpp',
    theme: 'oneDark',
    fontSize: 16,
    editorType: 'code'
})
</script>

<style scoped lang="scss">
    .editor-box {
        margin-top: 10px;
    }
</style>