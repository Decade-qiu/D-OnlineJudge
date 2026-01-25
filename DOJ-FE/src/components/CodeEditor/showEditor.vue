<template>
    <div class="editor">
        <div class="main">
            <codemirror v-model="code" :style="{
                width: config.width,
                height: config.height,
                backgroundColor: 'var(--bg-primary)',
                color: 'var(--text-primary)',
                fontSize: fontSizeStr,
            }" placeholder="Please enter the code." :extensions="extensions" :disabled="true"
                :indent-with-tab="true" :tab-size="config.tabSize" @update="handleStateUpdate" @ready="handleReady" />
        </div>
        <div class="divider"></div>
        <div class="footer">
            <div class="infos">
                <span class="item">Spaces: {{ config.tabSize }}</span>
                <span class="item">Length: {{ state.length }}</span>
                <span class="item">Lines: {{ state.lines }}</span>
                <span class="item">Cursor: {{ state.cursor }}</span>
                <span class="item">Selected: {{ state.selected }}</span>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { reactive, shallowRef, computed, watch, onMounted, ref } from 'vue'
import { EditorView, ViewUpdate } from '@codemirror/view'
import { Codemirror } from 'vue-codemirror'
import { ElButton, ElMessage } from 'element-plus'
import { Close, VideoPlay } from '@element-plus/icons-vue'
import { configType } from './index.vue'
import { reqSubmit } from '@/api/submit'
import { executeMessage } from '@/api/submit/type'

const props = defineProps<{
    config: configType,
    code: string,
    theme: Object | Array<string>,
    language: Function,
    languageName: string
}>();

// 组件暴露
defineExpose({
    Codemirror
});

// 响应式状态
const code = shallowRef(props.code);
const cmView = shallowRef<EditorView>();
const inputModel = shallowRef(false);  // 控制输入区域显示与否
const output = shallowRef<executeMessage>();  // 保存输出结果
const outputVisible = shallowRef(false);  // 控制 output 区域显示与否
const outputTextVis = shallowRef(false);  // 控制输入输出区域显示与否

const fontSizeStr = computed(() => `${props.config.fontSize}px`);
const timeInfo = computed(() => {
    return output.value?.time ? `${(output.value.time * 1000).toFixed(2)} ms` : '0.00 ms';
});
const memoryInfo = computed(() => {
    return output.value?.memory ? `${(output.value.memory).toFixed(2)} KB` : '0.00 KB';
});

const loading = ref(true);
const svg = `
        <path class="path" d="
          M 30 15
          L 28 17
          M 25.61 25.61
          A 15 15, 0, 0, 1, 15 30
          A 15 15, 0, 1, 1, 27.99 7.5
          L 15 15
        " style="stroke-width: 4px; fill: #F9F9F9"/>
      `

// 计算属性
const extensions = computed(() => {
    const result = [];
    if (props.language) {
        result.push(props.language());
    }
    if (props.theme) {
        result.push(props.theme);
    }
    return result;
});

const handleReady = ({ view }: any) => {
    cmView.value = view;
};

const state = reactive({
    lines: null as null | number,
    cursor: null as null | number,
    selected: null as null | number,
    length: null as null | number
});

const handleStateUpdate = (viewUpdate: ViewUpdate) => {
    const ranges = viewUpdate.state.selection.ranges
    state.selected = ranges.reduce((plus, range) => plus + range.to - range.from, 0)
    state.cursor = ranges[0].anchor
    state.length = viewUpdate.state.doc.length
    state.lines = viewUpdate.state.doc.lines
};

const handleSubmit = async () => {

    outputTextVis.value = false;

    output.value = {
        exitValue: -1, // 设置为 null 表示还没有结果
        status: 'Running',
        message: '',
        time: 0,
        memory: 0,
    };

    // 将代码字符串转换为 Blob 文件
    const codeBlob = new Blob([code.value], { type: 'text/plain' });

    // 获取语言扩展名
    const languageExtension = getLanguageExtension(props.languageName);

    // 创建 FormData
    const formData = new FormData();
    formData.append('file', codeBlob, `Main.${languageExtension}`);
    formData.append('language', props.languageName);

    ElMessage.success('提交成功');

    // 发送请求
    const response = (await reqSubmit(formData)).data;

    // 成功时处理
    if (response.code === 200) {
        const data = response.data;
        if (data.exitValue === 0 || data.exitValue >= 10) {
            output.value = data;
        } else {
            output.value = data;
        }
        if (data.message.trim() !== '') {
            outputTextVis.value = true;
        }else{
            outputTextVis.value = false;
        }
    }
    // 显示 output 区域
    outputVisible.value = true;
};

// 获取语言扩展名的辅助函数
const getLanguageExtension = (languageName: string) => {
    if (!languageName) return 'txt'; // 默认 txt 后缀

    // 你可以根据不同语言函数推断其扩展名
    const language = languageName.toLowerCase();

    switch (language) {
        case 'javascript': return 'js';
        case 'python': return 'py';
        case 'cpp': return 'cpp';
        case 'java': return 'java';
        // 添加更多语言映射
        default: return 'txt';
    }
};

// 监听 props 变化
onMounted(() => {
    watch(
        () => props.code,
        (_code) => {
            code.value = _code
        }
    );
    const handleKeyDown = (event: KeyboardEvent) => {
        const isSaveShortcut = 
            (event.ctrlKey || event.metaKey) && event.key.toLowerCase() === 's';

        if (isSaveShortcut) {
            event.preventDefault(); // 阻止默认保存行为
            console.log('Save shortcut (Ctrl+S / Cmd+S) is disabled in the editor.');
            // 可选：在这里执行其他逻辑，比如保存代码等
        }
    };

    window.addEventListener('keydown', handleKeyDown);
});

// log 方法直接使用 console
const log = console.log
</script>

<style lang="scss" scoped>
@import '@/styles/variable.scss';

.editor {
    .divider {
        height: 1px;
        background-color: $border-color;
    }

    .main {

        .code {
            width: 30%;
            height: 100px;
            margin: 0;
            padding: 0.4em;
            overflow: scroll;
            border-left: 1px solid $border-color;
            font-family: monospace;
        }
    }

    .footer {
        height: 3rem;
        padding: 0 1em;
        display: flex;
        justify-content: space-between;
        align-items: center;
        font-size: 90%;
        background-color: var(--bg-elevated);
        border-top: 1px solid var(--border-color);

        .buttons {
            .item {
                margin-right: 1em;
                display: inline-flex;
                justify-content: center;
                align-items: center;
                background-color: transparent;
                border: 1px dashed var(--border-color);
                font-size: $font-size-small;
                color: var(--text-secondary);
                cursor: pointer;

                .iconfont {
                    margin-left: $xs-gap;
                }

                &:hover {
                    color: var(--text-primary);
                    border-color: var(--text-primary);
                }
            }
        }

        .infos {
            color: var(--text-secondary);
            .item {
                margin-left: 2em;
                display: inline-block;
                font-feature-settings: 'tnum';
            }
        }
    }

    .output {
        margin-top: 20px;
        border: 1px solid var(--border-color);
        border-radius: 4px;
        padding: 20px;
        background-color: var(--bg-elevated);
        box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);

        .output-header {
            display: flex;
            justify-content: flex-start;
            //margin-bottom: 10px;
            color: var(--text-primary);

            .closeoutput {
                margin-left: auto;
                cursor: pointer;
            }

            .running-status {
                background-color: var(--bg-primary);
                border: 1px solid var(--border-color);
                border-radius: 4px;
                margin-left: 10px;
                padding: 0 10px;

                span {
                    color: var(--danger);
                    font-size: 16px;
                }
            }

            .custom-loading-svg {
                background-color: transparent;
                /* 设置背景为透明 */
                border: none;
                /* 清除边框 */
                padding: 0;
                /* 去掉内边距 */
            }

            span {
                font-size: 18px;
                font-weight: bold;
            }

            /* 根据状态动态改变颜色 */
            .error-text {
                color: var(--danger);
                /* 红色表示错误 */
            }

            .success-text {
                color: var(--success);
                /* 绿色表示成功 */
            }
        }

        .input-output-section {
            display: flex;
            flex-direction: column;
            gap: 10px;
            margin-top: 15px;

            .input-field,
            .output-field {
                display: flex;
                flex-direction: column;
            }

            label {
                font-size: 14px;
                margin-bottom: 5px;
                font-weight: bold;
                color: var(--text-secondary);
            }

            .input-content {
                background-color: var(--bg-primary);
                border: 1px solid var(--border-color);
                border-radius: 4px;
                padding: 10px;
                font-size: 14px;
                color: var(--text-primary);
                line-height: 1.5;
            }

            .output-content {
                background-color: var(--bg-primary);
                border: 1px solid var(--border-color);
                border-radius: 4px;
                padding: 10px;
                font-size: 14px;
                color: var(--text-primary);
                line-height: 1.5;
                white-space: pre-wrap;
                word-wrap: break-word;
            }
        }
    }

}
</style>