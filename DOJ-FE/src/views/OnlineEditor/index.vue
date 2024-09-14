<template>
    <CodeEditor :config="config" />
</template>

<script setup lang='ts'>
import { ref } from 'vue';

const config = ref({
    tabSize: 4,
    disabled: false,
    height: '62vh',
    width: '100%',
    language: 'cpp',
    theme: 'oneDark'
})
</script>

<style scoped lang="scss">

</style>