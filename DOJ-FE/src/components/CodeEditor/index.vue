<template>
    <div class="online-coding">
        <toolbar :config="config" :themes="Object.keys(themes)" :languages="Object.keys(languages)"
            @language="ensureLanguageCode" />
        <div class="divider"></div>
        <div class="loading-box" v-if="loading">
            <loading />
        </div>
        <editor v-else-if="currentLangCode" :config="config" :theme="currentTheme" :language="currentLangCode.language"
            :code="currentLangCode.code" />
    </div>
</template>

<script lang="ts" setup>
import { reactive, computed, shallowRef, onBeforeMount, PropType, toRefs } from 'vue';
import languages from './languages';
import * as themes from './themes';
import Toolbar from './toolbar.vue';
import Editor from './editor.vue';

export type configType = {
    tabSize: number;
    disabled: boolean;
    height: string;
    width: string;
    language: string;
    theme: string;
};

const props = defineProps({
    config: {
        type: Object as PropType<configType>,
        required: true
    }
});

const { config } = toRefs(props);

const loading = shallowRef(false)
const langCodeMap = reactive(new Map<string, { code: string; language: () => any }>())
const currentLangCode = computed(() => langCodeMap.get(config.value.language)!)
const currentTheme = computed(() => {
    return config.value.theme !== 'default' ? (themes as any)[config.value.theme] : void 0
});

// 定义异步函数，用于确保加载语言代码
const ensureLanguageCode = async (targetLanguage: string) => {
    config.value.language = targetLanguage
    loading.value = true
    const delayPromise = () => new Promise((resolve) => window.setTimeout(resolve, 260))
    if (langCodeMap.has(targetLanguage)) {
        await delayPromise()
    } else {
        const [result] = await Promise.all([languages[targetLanguage](), delayPromise()])
        langCodeMap.set(targetLanguage, result.default)
    }
    loading.value = false
};

// 设置加载状态为 true，并确保在组件挂载前初始化语言代码
loading.value = true
onBeforeMount(() => {
    ensureLanguageCode(config.value.language)
});

</script>

<style lang="scss" scoped>
@import '@/styles/variable.scss';

.online-coding {

    .divider {
        height: 1px;
        background-color: #e8e8e8;
    }

    .loading-box {
        width: 80%;
        min-height: 20rem;
        max-height: 60rem;
        /* loading height = view-height - layout-height - page-height */
        /* navbar + banner + footer */
        // $layout-height: $navbar-height + $banner-height + $footbar-height;
        /* single-card-gap * 2 + card-header + editor-header */
        // $page-height: 2rem * 2 + 3.2rem + 3rem;
        /* editor-border * 2 */
        // height: calc(100vh - $layout-height - $page-height - 2px);
    }
}
</style>
