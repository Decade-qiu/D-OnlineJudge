<script lang="ts" setup>
import LayoutFooter from '@/views/Layout/components/LayoutFooter.vue'
import LayoutFixed from '@/views/Layout/components/LayoutFixed.vue'
import { provide, ref } from 'vue';

const refresh = ref(0);
const triggerRefresh = () => {
    refresh.value += 1;
};
provide('triggerRefresh', triggerRefresh);
</script>

<template>
    <div class="app-layout">
        <!-- Animated Background -->
        <div class="bg-gradient"></div>
        <div class="bg-grid"></div>
        
        <!-- Header -->
        <LayoutFixed />
        
        <!-- Main Content -->
        <main class="main-content">
            <div class="content-wrapper">
                <router-view v-slot="{ Component }" :key="$route.fullPath">
                    <transition name="page" mode="out-in">
                        <component :is="Component" />
                    </transition>
                </router-view>
            </div>
        </main>
        
        <!-- Footer -->
        <LayoutFooter />
    </div>
</template>

<style scoped lang="scss">


.app-layout {
    min-height: 100vh;
    display: flex;
    flex-direction: column;
    position: relative;
    overflow-x: hidden;
}

// Animated gradient background
.bg-gradient {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: 
        radial-gradient(ellipse at 20% 20%, rgba(102, 126, 234, 0.15) 0%, transparent 50%),
        radial-gradient(ellipse at 80% 80%, rgba(118, 75, 162, 0.15) 0%, transparent 50%),
        radial-gradient(ellipse at 50% 50%, rgba(0, 212, 255, 0.05) 0%, transparent 70%),
        var(--bg-primary);
    z-index: -2;
}

// Subtle grid pattern
.bg-grid {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-image: 
        linear-gradient(rgba(255, 255, 255, 0.02) 1px, transparent 1px),
        linear-gradient(90deg, rgba(255, 255, 255, 0.02) 1px, transparent 1px);
    background-size: 50px 50px;
    z-index: -1;
}

.main-content {
    flex: 1;
    padding-top: calc($navbar-height + $space-lg);
    padding-bottom: $space-xl;
    min-height: calc(100vh - $navbar-height - $footer-height);
}

.content-wrapper {
    max-width: $container-max;
    margin: 0 auto;
    padding: 0 $container-padding;
    
    @include mobile {
        padding: 0 $space-md;
    }
}

// Page transition animations
.page-enter-active,
.page-leave-active {
    transition: all 0.3s ease;
}

.page-enter-from {
    opacity: 0;
    transform: translateY(20px);
}

.page-leave-to {
    opacity: 0;
    transform: translateY(-10px);
}
</style>