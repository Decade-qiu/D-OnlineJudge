<template>
    <div class="editor">
        <div class="main">
            <codemirror v-model="code" :style="{
                width: config.width,
                height: config.height,
                backgroundColor: '#fff',
                color: '#333'
            }" placeholder="Please enter the code." :extensions="extensions" :disabled="config.disabled"
                :indent-with-tab="true" :tab-size="config.tabSize"
                @update="handleStateUpdate" @ready="handleReady" />
        </div>
        <div class="divider"></div>
        <div class="footer">
            <div class="buttons">
                <button class="item" @click="handleUndo">Undo</button>
                <button class="item" @click="handleRedo">Redo</button>
                <!-- <button class="item" @click="handlePrint">Print</button> -->
            </div>
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
import { reactive, shallowRef, computed, watch, onMounted } from 'vue'
import { EditorView, ViewUpdate } from '@codemirror/view'
import { redo, undo } from '@codemirror/commands'
import { Codemirror } from 'vue-codemirror'
import { configType } from './index.vue'

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
}

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
}
</style>