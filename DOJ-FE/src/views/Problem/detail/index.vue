<template>
    <div class="problem-container">
        <!-- 标题部分 -->
        <div class="problem-header">
            <h1>{{ problem?.name }}</h1>
            <div class="limits">
                <span class="meta">内存限制：{{ problem?.memoryLimit }} KB</span>
                <span class="meta">时间限制：{{ problem?.timeLimit }} s</span>
                <span class="meta">标准输入输出</span>
            </div>
            <div class="stats">
                <span class="meta">提交：{{ problem?.totalAttempt }}</span>
                <span class="meta">通过：{{ problem?.totalPass }}</span>
            </div>
        </div>

        <!-- 功能按钮 -->
        <div class="buttons">
            <button class="btn submit-btn" @click="submit">提交</button>
            <button class="btn record-btn">提交记录</button>
            <button class="btn stats-btn">统计</button>
        </div>

        <div class="item description">
            <h2>题目描述</h2>
            <p class="item-body">{{ problem?.description }}</p>
        </div>

        <div class="item input-format">
            <h2>输入格式</h2>
            <p class="item-body">{{ problem?.inputStyle }}</p>
        </div>

        <div class="item input-format">
            <h2>输出格式</h2>
            <p class="item-body">{{ problem?.outputStyle }}</p>
        </div>

        <div class="item io-example">
            <h2>输入样例 <button class="copy-btn" @click="paste(inputSampleRef)">复制</button> </h2>
            <div class="example-container">
                <div class="example-content" ref="inputSampleRef">
                    <template v-for="input in multiLine(problem?.inputSample)">
                        <div class="line">{{ input }}</div>
                    </template>
                </div>
            </div>
        </div>

        <div class="item io-example">
            <h2>输出样例 <button class="copy-btn" @click="paste(outputSampleRef)">复制</button> </h2>
            <div class="example-container">
                <div class="example-content" ref="outputSampleRef">
                    <template v-for="output in multiLine(problem?.outputSample)">
                        <div class="line">{{ output }}</div>
                    </template>
                </div>
            </div>
        </div>

        <div class="item input-format">
            <h2>数据范围</h2>
            <p class="item-body">{{ problem?.dataRange }}</p>
        </div>

    </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import { reqProblemDetail } from '@/api/problem';
import type { ProblemType } from '@/api/problem/type';
import router from '@/router';

const route = useRoute();
const problem = ref<ProblemType>();

const inputSampleRef = ref();
const outputSampleRef = ref();

const multiLine = (list: string | undefined) => {
    if (!list) return [];
    list = list.substring(1, list.length - 1);
    let res = list.split(",")
        .map((item) => item.replaceAll('\"', '').trim())
        .filter((item) => item !== "");
    // console.log(res);
    return res
};

const submit = () => {
    const pid = problem.value?.id;
    const pname = problem.value?.name;
    router.push({ name: 'submit', params: { pid, pname } });
};

const paste = (exampleContent: any) => {
    // console.log(111);
    if (exampleContent) {
        const textToCopy = exampleContent.innerText;
        navigator.clipboard.writeText(textToCopy)
            .catch((err) => {
                console.error("复制失败：", err);
            });
    }
};

onMounted(async () => {
    const pid = route.params.id as string;
    problem.value = (await reqProblemDetail(pid)).data.data;
});

</script>

<style scoped lang="scss">
.problem-container {
    display: flex;
    flex-direction: column;
    margin-top: 10px;
    padding: 30px;
    background: #fff;
    border: 1px solid #ddd;
    border-radius: 10px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.problem-header {
    display: flex;
    flex-direction: column;
    gap: 10px;

    text-align: center;

    .limits,
    .stats {
        margin-top: 10px;
        font-weight: 550;
        color: #666;
        margin-bottom: 5px;
    }

    .meta {
        margin-right: 15px;
        padding: 8px 12px;
        background: #f8f9fa;
        border: 1px solid #e9ecef;
        border-radius: 5px;
        transition: all 0.3s ease;

        &:hover {
            background: #e9ecef;
        }
    }

    h1 {
        font-size: 2em;
        color: #2c3e50;
        margin-bottom: 15px;
    }
}

.buttons {
    display: flex;
    flex-direction: row;
    margin: 20px 0;
    gap: 0;

    .btn {
        padding: 12px 25px;
        border: none;
        cursor: pointer;
        font-weight: 500;
        transition: all 0.3s ease;

        &:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }

        &.submit-btn {
            background-color: #4CAF50;
            color: white;
            border-radius: 5px 0 0 5px;
        }

        &.record-btn {
            background-color: #2196F3;
            color: white;
        }

        &.stats-btn {
            background-color: #FF9800;
            color: white;
            border-radius: 0 5px 5px 0;
        }
    }
}

.description,
.input-format,
.io-example {
    margin-top: 20px;

    &.item {
        background-color: #f9f9f9;
        border-radius: 5px;
        border: 1px solid #ddd;
        transition: all 0.3s ease;

        &:hover {
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }

        h2 {
            margin: 0;
            border-bottom: 1px solid #ccc;
            padding: 15px;
            background-color: #F3F4F5;
            border-radius: 10px 10px 0 0;
            color: #2c3e50;
            font-size: 1.5em;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .item-body {
            padding: 20px 30px;
            background-color: #fff;
            border-radius: 18px;
            font-size: large;
        }

        .copy-btn {
            padding: 5px 15px;
            border: 1px solid #ddd;
            border-radius: 4px;
            background: white;
            cursor: pointer;
            transition: all 0.3s ease;

            &:hover {
                background: #f0f0f0;
                transform: translateY(-1px);
            }
        }
    }

    .example-container {
        display: flex;
        align-items: flex-start;
        background-color: #f9f9f9;
        border: 1px solid #ddd;
        border-radius: 0 0 5px 5px;
        padding: 20px;

        .example-content {
            flex-grow: 1;
            display: flex;
            flex-direction: column;
            background-color: #fff;
            border: 1px solid #e9ecef;
            border-radius: 5px;
            box-shadow: 0 1px 1px rgba(0, 0, 0, 0.1);
            margin: 0;
            padding: 15px 25px;
            font-family: monospace;
            white-space: pre-wrap;
            overflow-y: auto;
            line-height: 1.6;

            .line {
                margin: 5px 0;
                font-size: large;
                font-family: monospace;
                color: #2c3e50;

                &:not(:last-child) {
                    margin-bottom: 5px;
                }

                &:last-child {
                    margin-bottom: 0;
                }
            }
        }
    }
}
</style>