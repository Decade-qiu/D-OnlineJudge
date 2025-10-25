<template>
    <div class="problem_list">
        <div class="search_setting">
            <el-select v-model="searchDifficulty" placeholder="难度" @clear="searchDifficulty = null" clearable>
                <el-option label="简单" value="简单" />
                <el-option label="中等" value="中等" />
                <el-option label="困难" value="困难" />
            </el-select>
            <el-select v-model="searchStatus" placeholder="状态" clearable @clear="searchStatus = null">
                <el-option label="已通过" value="0" />
                <el-option label="未开始" value="1" />
                <el-option label="尝试中" value="2" />
            </el-select>
            <!-- <el-select v-model="searchTag" placeholder="标签" clearable @clear="searchTag = null">
                <el-option label="数组" value="数组" />
                <el-option label="字符串" value="字符串" />
                <el-option label="链表" value="链表" />
            </el-select> -->
            <el-input placeholder="请输入标签(','分割)" v-model="searchTag" clearable />
            <el-input placeholder="请输入题目名称" v-model="searchName" clearable />
        </div>
        <el-table :data="tableData" :default-sort="{ prop: 'id', order: 'ascending' }">
            <el-table-column prop="status" label="状态" align="center" width="120">
                <template v-slot="scope">
                    <span v-if="scope.row.status === 0">
                    </span>
                    <span v-else-if="scope.row.status === 1">
                        <el-icon color="#49CE78"><CircleCheck /></el-icon>
                    </span>
                    <span v-else-if="scope.row.status === 2">
                        <el-icon color="#FF2D55"><circle-close /></el-icon>
                    </span>
                </template>
            </el-table-column>
            <el-table-column prop="id" label="编号" sortable align="center" width="100" />
            <el-table-column prop="name" label="题目" align="center" min-width="100">
                <template v-slot="scope">
                    <span class="problem_name" @click="toProblem(scope.row.id)">{{ scope.row.name }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="difficulty" label="难度" align="center" width="100">
                <template v-slot="scope">
                    <span :class="{'easy': scope.row.difficulty==='简单', 'medium': scope.row.difficulty==='中等', 'hard': scope.row.difficulty==='困难'}" class="problem_difficulty">{{ scope.row.difficulty }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="totalAttempt" label="提交数" align="center" width="150" />
            <el-table-column prop="passRate" label="通过率" :formatter="formatPassRate" sortable width="150" />
        </el-table>

        <div class="page_list">
            <el-pagination background layout="prev, pager, next, jumper, total, sizes"
            :total="total" 
            :page-count="pages" 
            :page-sizes="pageSizes" 
            v-model:page-size="pageQueryFrom.pageSize" v-model:current-page="pageQueryFrom.pageNo"
            @change="getProblemList"
            />
        </div>
    </div>
</template>

<script setup lang='ts'>
import { ElTable, ElTableColumn, ElSelect, ElInput, ElPagination, ElIcon } from 'element-plus';
import { CircleCheck, CircleClose } from '@element-plus/icons-vue';
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { reqProblemList, reqProblemPageList } from '@/api/problem';
import { reqSubmissionUserProblem } from '@/api/submission';
import type { ProblemType } from '@/api/problem/type';
import type { BasePageQueryForm } from '@/api/base';
import { onMounted } from 'vue';
import { watch } from 'vue';

const router = useRouter();

const searchDifficulty = ref();
const searchStatus = ref();
const searchTag = ref();
const searchName = ref();
watch([searchDifficulty, searchStatus, searchTag, searchName], () => {
    getProblemList();
});

const formatPassRate = (row: any) => {
    return `${(row.totalPass / row.totalAttempt * 100).toFixed(2)}%`
};

type TableData = ProblemType & { status: number };
const tableData = ref<TableData[]>([]);
const pageQueryFrom = ref<BasePageQueryForm>({
    pageNo: 1,
    pageSize: 10,
    isAsc: true,
    sortBy: ''
});
const total = ref(0);
const pages = ref(0);
const pageSizes = ref([5, 10, 20, 50]);

const toProblem = (id: number) => {
    router.push("/problem/"+id);
};

const getProblemList = async () => {
    // const res = await reqProblemList();
    const res = await reqProblemPageList({
        ...pageQueryFrom.value,
        name: searchName.value,
        difficulty: searchDifficulty.value,
        tags: searchTag.value,
        status: searchStatus.value
    });
    const { total:totalV, pages:pagesV, list } = res.data.data;
    total.value = totalV;
    pages.value = pagesV;
    tableData.value =  [];
    for (const problem of list) {
        const ss = (await reqSubmissionUserProblem(problem.id)).data.data;
        if (searchStatus.value == 0) {
            if (ss !== 1) {
                continue; // 如果状态是已通过，且查询条件是已通过，则跳过
            }
        } else if (searchStatus.value == 1) {
            if (ss !== 0) {
                continue; // 如果状态是未开始，且查询条件是未开始，则跳过
            }
        } else if (searchStatus.value == 2) {
            if (ss !== 2) {
                continue; // 如果状态是尝试中，且查询条件是尝试中，则跳过
            }
        }
        tableData.value.push({
            ...problem,
            status: ss
        });
    }
};

onMounted(() => {
    getProblemList();
});
</script>

<style scoped lang="scss">
.problem_list {

    font-weight: 700;

    .search_setting {
        display: flex;
        align-items: center;
        gap: 10px;
        margin-bottom: 20px;
        padding-top: 10px;
    }

    .el-table {
        background-color: #f9f9f9;
        border-radius: 8px;
        box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);

        .problem_name {
            color: #1E70BF;
            /* 亮蓝色 */
            cursor: pointer;
            /* 鼠标悬停时显示指针 */
        }

        .problem_difficulty {
            &.easy {
                color: #67C23A;
                /* 绿色 */
            }
            &.medium {
                color: #E6A23C;
                /* 橙色 */
            }
            &.hard {
                color: #F56C6C;
                /* 红色 */
            }
        }
    }

    .page_list {
        display: flex;
        justify-content: center;
        /* 右对齐分页器 */
        margin-top: 20px;
        /* 添加顶部间距以分隔表格和分页器 */
    }
}
</style>
