<template>
    <div :class="['online-coding', { 'full-page': isPageFullscreen }]">
        <toolbar v-if="config.editorType !== 'show'"
            :config="config"
            :themes="Object.keys(themes)"
            :languages="Object.keys(languages)"
            @language="ensureLanguageCode"
            @fullscreen="togglePageFullscreen"
        />
        <div class="divider"></div>
        <div class="loading-box" v-if="loading">
            <loading />
        </div>
        <component
            v-else="currentLangCode"
            :is="editorComponent"
            :config="config"
            :theme="currentTheme"
            :language="currentLangCode.language"
            :language-name="currentLangCode.languageName"
            :code="currentLangCode.code"
        />
    </div>
</template>

<script lang="ts" setup>
import { reactive, computed, shallowRef, onBeforeMount, PropType, toRefs, ref } from 'vue';
import languages from './languages';
import * as themes from './themes';
import Toolbar from './toolbar.vue';
import CodeEditor from './codeEditor.vue';
import ProblemEditor from './problemEditor.vue';
import ShowEditor from './showEditor.vue';

export type configType = {
    tabSize: number;
    fontSize: number;
    disabled: boolean;
    height: string;
    width: string;
    language: string;
    theme: string;
    editorType: string; // 'code' | 'problem'
    code?: string; // 可选属性，适用于 'show' 编辑器
};

const props = defineProps({
    config: {
        type: Object as PropType<configType>,
        required: true
    }
});

const { config } = toRefs(props);

const editorComponent = computed(() => {
    if (config.value.editorType === 'problem') {
        return ProblemEditor;
    } else if (config.value.editorType === 'show') {
        return ShowEditor;
    }
    return CodeEditor;
});

const loading = shallowRef(false)
const langCodeMap = reactive(new Map<string, { code: string; language: () => any; languageName: string; }>())
const currentLangCode = computed(() => langCodeMap.get(config.value.language)!)
const currentTheme = computed(() => {
    return config.value.theme !== 'default' ? (themes as any)[config.value.theme] : void 0
});

// 定义异步函数，用于确保加载语言代码
const ensureLanguageCode = async (targetLanguage: string) => {
    config.value.language = targetLanguage;
    loading.value = true;
    const delayPromise = () => new Promise((resolve) => window.setTimeout(resolve, 260));
    if (langCodeMap.has(targetLanguage)) {
        await delayPromise();
    } else {
        const [result] = await Promise.all([languages[targetLanguage](), delayPromise()]);
        if (config.value.code) {
            result.default.code = config.value.code;
        }
        result.default.languageName = targetLanguage;
        langCodeMap.set(targetLanguage, result.default);
    }
    loading.value = false;
};

// 设置加载状态为 true，并确保在组件挂载前初始化语言代码
loading.value = true;
onBeforeMount(() => {
    ensureLanguageCode(config.value.language)
});

// 页面全屏相关状态
const isPageFullscreen = ref(false);
const togglePageFullscreen = () => {
    isPageFullscreen.value = !isPageFullscreen.value;
    config.value.height = isPageFullscreen.value ? '88vh' : '62vh';
};


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
    }

    &.full-page {
        position: fixed;
        top: 0;
        left: 0;
        width: 100vw;
        z-index: 9999;
        background-color: #fff;
    }
}
</style>
