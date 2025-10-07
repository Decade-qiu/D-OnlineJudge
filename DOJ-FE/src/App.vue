<script setup lang="ts">
import { onMounted } from 'vue';
import { useWebSocket } from '@/utils/websocket';

onMounted(() => {
  // 在应用根组件挂载时，初始化 WebSocket 连接
  useWebSocket();
});
</script>

<template>
    <!-- 一级路由出口组件 -->
    <RouterView />
</template>

<style scoped lang="scss"></style>

