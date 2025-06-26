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
    <div class="container">
        <LayoutFixed />
        <main class="content">
            <!-- <RouterView /> -->
            <router-view v-slot="{ Component }" :key="$route.fullPath">
                <transition name="fade" mode="out-in">
                    <!-- <keep-alive> -->
                        <component :is="Component" />
                    <!-- </keep-alive> -->
                </transition>
            </router-view>
        </main>
        <LayoutFooter />
    </div>
</template>

<style scoped lang="scss">
/* 定义 fade 过渡效果 */
.fade-enter-active,
.fade-leave-active {
    transition: all 0.3s ease;
}

/* 进入时的初始状态 */
.fade-enter-from,
.fade-leave-to

/* .fade-leave-active in <2.1.8 */
    {
    transform: translateY(10px);
    /* 从下方开始 */
    opacity: 0;
    /* 从透明开始 */
}

/* 进入后的最终状态 */
.fade-enter-to,
.fade-leave-from

/* .fade-leave-active in <2.1.8 */
    {
    transform: translateY(0);
    /* 滑动到正常位置 */
    opacity: 1;
    /* 变为不透明 */
}

/* Main container for the layout, ensuring it fills the viewport height */
.container {
    display: flex;
    /* Use flexbox for layout */
    flex-direction: column;
    /* Stack children vertically */
    min-height: 100vh;
    /* Minimum height is the viewport height */
    background-color: #ddd;
    /* Light grey background */
}

/* Content area that flexes to take available space */
.content {
    flex: 1;
    /* Grow to fill available space */
    margin: 15px 50px;
    margin-top: 70px;
}

</style>