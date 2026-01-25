<template>
    <el-dialog 
        :title="isEdit ? '编辑题目' : '新增题目'" 
        v-model="visible" 
        width="800px" 
        :close-on-click-modal="false"
    >
        <el-form :model="form" label-width="100px">
            <el-form-item label="标题">
                <el-input v-model="form.name" />
            </el-form-item>
            <el-form-item label="难度">
                <el-select v-model="form.difficulty">
                    <el-option label="简单" value="简单" />
                    <el-option label="中等" value="中等" />
                    <el-option label="困难" value="困难" />
                </el-select>
            </el-form-item>
            <el-form-item label="时间限制">
                <el-input v-model="form.timeLimit" placeholder="例如: 1000">
                    <template #append>ms</template>
                </el-input>
            </el-form-item>
            <el-form-item label="内存限制">
                <el-input v-model="form.memoryLimit" placeholder="例如: 256">
                    <template #append>MB</template>
                </el-input>
            </el-form-item>
            <el-form-item label="标签">
                <el-input v-model="tagsStr" placeholder="逗号分隔，如: dp,math" />
            </el-form-item>
            
            <div class="editor-tabs">
                <el-tabs type="card" v-model="activeEditor">
                    <el-tab-pane label="题目描述" name="desc">
                        <el-input type="textarea" :rows="10" v-model="form.description" placeholder="支持 Markdown" />
                    </el-tab-pane>
                    <el-tab-pane label="输入格式" name="input">
                        <el-input type="textarea" :rows="5" v-model="form.inputStyle" placeholder="支持 Markdown" />
                    </el-tab-pane>
                    <el-tab-pane label="输出格式" name="output">
                        <el-input type="textarea" :rows="5" v-model="form.outputStyle" placeholder="支持 Markdown" />
                    </el-tab-pane>
                    <el-tab-pane label="样例输入" name="sampleIn">
                        <el-input type="textarea" :rows="5" v-model="samplesInStr" placeholder="样例之间使用 '---' 分隔" />
                        <div class="tip">样例之间使用 '---' (单独一行) 分隔</div>
                    </el-tab-pane>
                    <el-tab-pane label="样例输出" name="sampleOut">
                        <el-input type="textarea" :rows="5" v-model="samplesOutStr" placeholder="样例之间使用 '---' 分隔" />
                        <div class="tip">样例之间使用 '---' (单独一行) 分隔</div>
                    </el-tab-pane>
                    <el-tab-pane label="提示" name="range">
                        <el-input type="textarea" :rows="5" v-model="form.hint" placeholder="支持 Markdown" />
                    </el-tab-pane>
                    <el-tab-pane label="测试数据" name="testData">
                        <el-input type="textarea" :rows="5" v-model="form.testData" placeholder="评测输入（原样写入文件）" />
                    </el-tab-pane>
                    <el-tab-pane label="标准答案" name="testAns">
                        <el-input type="textarea" :rows="5" v-model="form.testAns" placeholder="评测输出（与程序输出精确匹配）" />
                    </el-tab-pane>
                </el-tabs>
            </div>
        </el-form>
        <template #footer>
            <el-button @click="visible = false">取消</el-button>
            <el-button type="primary" @click="submit" :loading="loading">确定</el-button>
        </template>
    </el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { reqCreateProblem, reqUpdateProblem, reqProblemDetail } from '@/api/problem';

const emit = defineEmits(['refresh']);

const visible = ref(false);
const isEdit = ref(false);
const loading = ref(false);
const activeEditor = ref('desc');
const tagsStr = ref('');

const form = reactive({
    id: undefined,
    name: '',
    difficulty: '简单',
    timeLimit: '',
    memoryLimit: '',
    tags: [] as string[],
    description: '',
    inputStyle: '',
    outputStyle: '',
    inputSample: [] as string[],
    outputSample: [] as string[],
    hint: '',
    testData: '',
    testAns: ''
});

const samplesInStr = ref('');
const samplesOutStr = ref('');

const open = async (row?: any) => {
    visible.value = true;
    if (row) {
        isEdit.value = true;
        try {
            const res = await reqProblemDetail(String(row.id));
            const data = res.data.data;
            Object.assign(form, data);
            
            tagsStr.value = (data.tags || []).join(',');
            samplesInStr.value = (data.inputSample || []).join('\n---\n');
            samplesOutStr.value = (data.outputSample || []).join('\n---\n');
        } catch (e) {
            ElMessage.error('加载详情失败');
        }
    } else {
        isEdit.value = false;
        resetForm();
    }
};

const resetForm = () => {
    form.id = undefined;
    form.name = '';
    form.difficulty = '简单';
    form.timeLimit = '1000';
    form.memoryLimit = '256';
    tagsStr.value = '';
    samplesInStr.value = '';
    samplesOutStr.value = '';
    form.description = '';
    form.inputStyle = '';
    form.outputStyle = '';
    form.inputSample = [];
    form.outputSample = [];
    form.hint = '';
    form.testData = '';
    form.testAns = '';
};

const submit = async () => {
    loading.value = true;
    try {
        // Process tags
        form.tags = tagsStr.value ? tagsStr.value.split(',').map(t => t.trim()).filter(t => t) : [];
        
        // Process samples (using --- as separator for multi-sample input)
        form.inputSample = samplesInStr.value ? samplesInStr.value.split('\n---\n').map(s => s.trim()) : [];
        form.outputSample = samplesOutStr.value ? samplesOutStr.value.split('\n---\n').map(s => s.trim()) : [];
        
        // Basic validation
        if (form.inputSample.length !== form.outputSample.length) {
            ElMessage.warning('输入样例和输出样例数量不匹配');
            return;
        }

        const payload = {
            ...form,
            timeLimit: Number(form.timeLimit),
            memoryLimit: Number(form.memoryLimit)
        };

        let res;
        if (isEdit.value) {
            res = await reqUpdateProblem(payload as any);
        } else {
            res = await reqCreateProblem(payload as any);
        }
        
        if (res.data.code === 200) {
            ElMessage.success(isEdit.value ? '修改成功' : '新增成功');
            visible.value = false;
            emit('refresh');
        } else {
            ElMessage.error(res.data.message || '操作失败');
        }
    } catch (e) {
        ElMessage.error('请求出错');
    } finally {
        loading.value = false;
    }
};

defineExpose({ open });
</script>

<style scoped>
.tip {
    font-size: 12px;
    color: #999;
    margin-top: 5px;
}
</style>
