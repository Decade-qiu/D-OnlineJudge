<script setup>
import LayoutFooter from '@/views/Layout/components/LayoutFooter.vue'
import LayoutFixed from '@/views/Layout/components/LayoutFixed.vue'
</script>

<template>
    <div class="container">
        <LayoutFixed />
        <main class="content">
            <RouterView />
        </main>
        <LayoutFooter />
    </div>
</template>

<style scoped lang="scss">
/* Main container for the layout, ensuring it fills the viewport height */
.container {
    display: flex; /* Use flexbox for layout */
    flex-direction: column; /* Stack children vertically */
    min-height: 100vh; /* Minimum height is the viewport height */
    margin: 0; /* Remove default margin */
    background-color: #ddd; /* Light grey background */
}

/* Content area that flexes to take available space */
.content {
    flex: 1; /* Grow to fill available space */
    padding: 15px 50px; /* Horizontal padding for content */
}
</style>