<template>
    <div class="editor-box">
        <CodeEditor :config="config" />
    </div>
</template>

<script setup lang='ts'>
import { ref } from 'vue';

const config = ref({
    tabSize: 4,
    disabled: false,
    height: '62vh',
    width: '100%',
    language: 'cpp',
    theme: 'oneDark',
    fontSize: 16,
})
</script>

<style scoped lang="scss">
    .editor-box {
        margin-top: 10px;
    }
</style>