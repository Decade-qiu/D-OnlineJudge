<script lang="ts" setup>
const currentYear = new Date().getFullYear();
</script>

<template>
    <footer class="footer">
        <div class="footer-inner">
            <div class="footer-content">
                <!-- Logo and Description -->
                <div class="footer-brand">
                    <div class="brand-logo">
                        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="url(#footerGradient)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                            <path d="M2 17L12 22L22 17" stroke="url(#footerGradient)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                            <path d="M2 12L12 17L22 12" stroke="url(#footerGradient)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                            <defs>
                                <linearGradient id="footerGradient" x1="2" y1="2" x2="22" y2="22">
                                    <stop stop-color="#667eea"/>
                                    <stop offset="1" stop-color="#764ba2"/>
                                </linearGradient>
                            </defs>
                        </svg>
                        <span>DOJ</span>
                    </div>
                    <p class="brand-desc">Duck Online Judge - 现代化在线编程判题平台</p>
                </div>

                <!-- Links -->
                <div class="footer-links">
                    <a href="https://github.com" target="_blank" class="footer-link">
                        <svg viewBox="0 0 24 24" fill="currentColor">
                            <path d="M12 0C5.37 0 0 5.37 0 12c0 5.31 3.435 9.795 8.205 11.385.6.105.825-.255.825-.57 0-.285-.015-1.23-.015-2.235-3.015.555-3.795-.735-4.035-1.41-.135-.345-.72-1.41-1.23-1.695-.42-.225-1.02-.78-.015-.795.945-.015 1.62.87 1.845 1.23 1.08 1.815 2.805 1.305 3.495.99.105-.78.42-1.305.765-1.605-2.67-.3-5.46-1.335-5.46-5.925 0-1.305.465-2.385 1.23-3.225-.12-.3-.54-1.53.12-3.18 0 0 1.005-.315 3.3 1.23.96-.27 1.98-.405 3-.405s2.04.135 3 .405c2.295-1.56 3.3-1.23 3.3-1.23.66 1.65.24 2.88.12 3.18.765.84 1.23 1.905 1.23 3.225 0 4.605-2.805 5.625-5.475 5.925.435.375.81 1.095.81 2.22 0 1.605-.015 2.895-.015 3.3 0 .315.225.69.825.57A12.02 12.02 0 0024 12c0-6.63-5.37-12-12-12z"/>
                        </svg>
                    </a>
                </div>
            </div>

            <!-- Copyright -->
            <div class="footer-bottom">
                <p class="copyright">
                    © {{ currentYear }} Duck Online Judge. Powered by <span class="highlight">Decade</span>
                </p>
                <p class="icp">苏ICP备3102128号</p>
            </div>
        </div>
    </footer>
</template>

<style scoped lang="scss">


.footer {
    margin-top: auto;
    border-top: 1px solid var(--border-color);
    background: var(--bg-secondary);
}

.footer-inner {
    max-width: $container-max;
    margin: 0 auto;
    padding: $space-xl $container-padding;
}

.footer-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: $space-lg;
    
    @include mobile {
        flex-direction: column;
        gap: $space-lg;
        text-align: center;
    }
}

.footer-brand {
    .brand-logo {
        display: flex;
        align-items: center;
        gap: $space-sm;
        margin-bottom: $space-sm;
        
        svg {
            width: 24px;
            height: 24px;
        }
        
        span {
            font-size: $font-size-lg;
            font-weight: $font-weight-bold;
            @include gradient-text;
        }
    }
    
    .brand-desc {
        color: var(--text-muted);
        font-size: $font-size-sm;
    }
}

.footer-links {
    display: flex;
    gap: $space-md;
}

.footer-link {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 40px;
    height: 40px;
    border-radius: $radius-full;
    background: var(--glass-bg);
    color: var(--text-secondary);
    transition: all $transition-fast;
    
    svg {
        width: 20px;
        height: 20px;
    }
    
    &:hover {
        background: var(--primary-gradient);
        color: white;
        transform: translateY(-2px);
    }
}

.footer-bottom {
    padding-top: $space-lg;
    border-top: 1px solid var(--border-color);
    text-align: center;
    
    .copyright {
        color: var(--text-muted);
        font-size: $font-size-sm;
        margin-bottom: $space-xs;
        
        .highlight {
            @include gradient-text;
            font-weight: $font-weight-medium;
        }
    }
    
    .icp {
        color: var(--text-disabled);
        font-size: $font-size-xs;
    }
}
</style>