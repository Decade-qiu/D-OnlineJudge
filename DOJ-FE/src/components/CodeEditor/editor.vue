<template>
    <div class="editor">
        <div class="main">
            <codemirror v-model="code" :style="{
                width: config.width,
                height: config.height,
                backgroundColor: '#fff',
                color: '#333'
            }" placeholder="Please enter the code." :extensions="extensions" :disabled="config.disabled"
                :indent-with-tab="true" :tab-size="config.tabSize" @update="handleStateUpdate" @ready="handleReady" />
        </div>
        <div class="divider"></div>
        <div class="footer">
            <div class="buttons">
                <el-button type="info" text @click="handleUndo">Undo</el-button>
                <el-button type="info" text @click="handleRedo">Redo</el-button>
                <el-button type="success" size="large" text @click="handleSubmit">submit</el-button>
            </div>
            <div class="infos">
                <span class="item">Spaces: {{ config.tabSize }}</span>
                <span class="item">Length: {{ state.length }}</span>
                <span class="item">Lines: {{ state.lines }}</span>
                <span class="item">Cursor: {{ state.cursor }}</span>
                <span class="item">Selected: {{ state.selected }}</span>
            </div>
        </div>

        <!-- 仅在 outputVisible 为 true 时显示 output 区域 -->
        <div class="output" v-if="outputVisible">
            <codemirror v-model="output" :style="{
                width: config.width,
                backgroundColor: '#fff',
                color: '#333'
            }" :disabled="true" :indent-with-tab="true" :tab-size="config.tabSize" />
        </div>
    </div>
</template>

<script lang="ts" setup>
import { reactive, shallowRef, computed, watch, onMounted } from 'vue'
import { EditorView, ViewUpdate } from '@codemirror/view'
import { redo, undo } from '@codemirror/commands'
import { Codemirror } from 'vue-codemirror'
import { ElButton, ElMessage } from 'element-plus'
import { configType } from './index.vue'
import { reqSubmit } from '@/api/submit'

const props = defineProps<{
    config: configType,
    code: string,
    theme: Object | Array<string>,
    language: Function
}>();

// 组件暴露
defineExpose({
    Codemirror
});

// 响应式状态
const code = shallowRef(props.code);
const cmView = shallowRef<EditorView>();
const output = shallowRef('');  // 保存输出结果
const outputVisible = shallowRef(false);  // 控制 output 区域显示与否

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

const handleUndo = () => {
    undo({
        state: cmView.value!.state,
        dispatch: cmView.value!.dispatch
    });
}

const handleRedo = () => {
    redo({
        state: cmView.value!.state,
        dispatch: cmView.value!.dispatch
    });
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
    try {
        // 将代码字符串转换为 Blob 文件
        const codeBlob = new Blob([code.value], { type: 'text/plain' });

        // 获取语言扩展名
        const languageExtension = getLanguageExtension(props.language);

        // 创建 FormData
        const formData = new FormData();
        formData.append('file', codeBlob, `Main.${languageExtension}`);
        formData.append('language', props.language.name);

        // 发送请求
        const response = (await reqSubmit(formData)).data;

        // 成功时处理
        if (response.code === 200) {
            const data = response.data;
            if (data.exitValue === 0) {
                ElMessage.success('提交成功');
                output.value = `运行时间: ${data.time}ms\n运行内存: ${data.memory}KB\n输出信息: ${data.message}`;
            } else {
                ElMessage.error('提交失败');
                output.value = `错误信息: ${data.exitValue}\n${data.message}`;
            }

            // 显示 output 区域
            outputVisible.value = true;
        }
    } catch (error) {
        ElMessage.error('提交失败，请重试');
        console.error(error);
    }
};

// 获取语言扩展名的辅助函数
const getLanguageExtension = (languageFunc: Function) => {
    if (!languageFunc) return 'txt'; // 默认 txt 后缀

    // 你可以根据不同语言函数推断其扩展名
    const language = languageFunc.name.toLowerCase();

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
    )
})

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
        background-color: #fff;

        .buttons {
            .item {
                margin-right: 1em;
                display: inline-flex;
                justify-content: center;
                align-items: center;
                background-color: transparent;
                border: 1px dashed $border-color;
                font-size: $font-size-small;
                color: $text-secondary;
                cursor: pointer;

                .iconfont {
                    margin-left: $xs-gap;
                }

                &:hover {
                    color: $text-color;
                    border-color: $text-color;
                }
            }
        }

        .infos {
            .item {
                margin-left: 2em;
                display: inline-block;
                font-feature-settings: 'tnum';
            }
        }
    }

    .output {
        margin-top: 1em;
    }
}
</style>