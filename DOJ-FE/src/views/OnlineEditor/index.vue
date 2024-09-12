<template>
    <CodeEditor :config="config" />
</template>

<script setup lang='ts'>
import { ref } from 'vue';

const config = ref({
    tabSize: 2,
    disabled: true,
    height: '62vh',
    width: '100%',
    language: 'cpp',
    theme: 'default'
})
</script>

<style scoped lang="scss">

</style>