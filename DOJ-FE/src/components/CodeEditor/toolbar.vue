<template>
    <div class="toolbar">
        <div class="item">
            <label for="language">language:</label>
            <select name="language" id="language" :value="config.language" @change="handleSelectLanguage">
                <option :value="option" :key="option" v-for="option in languages">
                    {{ option }}
                </option>
            </select>
        </div>
        <div class="item">
            <label for="theme">theme:</label>
            <select name="theme" id="theme" v-model="config.theme">
                <option :value="option" :key="option" v-for="option in ['default', ...themes]">
                    {{ option }}
                </option>
            </select>
        </div>
        <div class="item">
            <label for="fontSize">tabSize:</label>
            <select name="fontSize" id="fontSize" v-model.number="config.fontSize">
                <option :value="option" :key="option" v-for="option in [16, 18, 20, 22, 24, 26]">
                    {{ option }}
                </option>
            </select>
        </div>
        <div class="item">
            <label for="tabSize">tabSize:</label>
            <select name="tabSize" id="tabSize" v-model.number="config.tabSize">
                <option :value="option" :key="option" v-for="option in [2, 4, 6, 8]">
                    {{ option }}
                </option>
            </select>
        </div>
        <div class="item">
            <button class="full-button" @click="handleFullScreen">
                <el-icon class="full-icon">
                    <FullScreen />
                </el-icon>
            </button>
        </div>
    </div>
</template>
  
<script lang="ts" setup>
import { toRefs } from 'vue';
import { defineProps, defineEmits } from 'vue';
import { FullScreen } from '@element-plus/icons-vue';
import { configType } from './index.vue';

const props = defineProps<{
    config: configType,
    languages: Array<string>,
    themes: Array<string>,
}>();

const emit = defineEmits(['language', 'fullscreen']);
const { config, languages, themes } = toRefs(props);

const handleSelectLanguage = (event: Event) => {
    const target = event.target as HTMLSelectElement;
    emit('language', target.value);
};

const handleFullScreen = () => {
    emit('fullscreen');
};
</script>
  
<style lang="scss" scoped>
.toolbar {
    display: flex;
    justify-content: flex-start;
    align-items: center;
    gap: 50px;
    height: 3rem;
    padding: 0 1em;
    background-color: var(--bg-elevated);
    border-bottom: 1px solid var(--border-color);
    color: var(--text-primary);

    .item {
        display: inline-flex;
        align-items: center;

        label {
            display: inline-block;
            margin-right: 0.2em;
            color: var(--text-secondary);
        }

        .full-button {
            border: none;
            background: none;
            color: var(--text-primary);
            cursor: pointer;

            .full-icon {
                font-size: 1.5rem;
            }
            
            &:hover {
                color: var(--primary-start);
            }
        }

        &:last-child {
            margin-left: auto;
        }
    }

    input,
    button,
    select {
        margin: 0;
        background-color: var(--bg-primary);
        color: var(--text-primary);
        border: 1px solid var(--border-color);
        padding: 4px 8px;
        border-radius: 4px;
        
        &:focus {
            outline: none;
            border-color: var(--primary-start);
        }
    }

    select {
        max-width: 8em;
    }
}
</style>
  