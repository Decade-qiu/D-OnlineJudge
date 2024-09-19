<template>
    <div class="problem_list">
        <div class="search_setting">
            <el-select v-model="searchDifficulty" placeholder="难度" clearable>
                <el-option label="简单" value="简单" />
                <el-option label="中等" value="中等" />
                <el-option label="困难" value="困难" />
            </el-select>
            <el-select v-model="searchStatus" placeholder="状态" clearable>
                <el-option label="已通过" value="已通过" />
                <el-option label="未通过" value="未通过" />
                <el-option label="尝试中" value="尝试中" />
            </el-select>
            <el-select v-model="searchTag" placeholder="标签" clearable>
                <el-option label="数组" value="数组" />
                <el-option label="字符串" value="字符串" />
                <el-option label="链表" value="链表" />
            </el-select>
            <el-input placeholder="请输入题目名称" v-model="searchName" clearable />
        </div>
        <el-table :data="tableData" :default-sort="{ prop: 'id', order: 'ascending' }">
            <el-table-column prop="status" label="状态" />
            <el-table-column prop="id" label="编号" sortable />
            <el-table-column prop="name" label="题目" />
            <el-table-column prop="difficulty" label="难度" />
            <el-table-column prop="totalAttempt" label="提交数" />
            <el-table-column prop="passRate" label="通过率" :formatter="formatPassRate" sortable />
        </el-table>
        <div class="page_list">
            <el-pagination background layout="prev, pager, next" :total="1000" />
        </div>
    </div>
</template>

<script setup lang='ts'>
import { ElTable, ElTableColumn, ElSelect, ElInput, ElPagination } from 'element-plus';

const formatPassRate = (row: any) => {
    return `${(row.totalPass / row.totalAttempt * 100).toFixed(2)}%`
};

const tableData = [
    {
        status: '已通过',
        id: '1',
        name: '两数之和',
        difficulty: '简单',
        totalAttempt: 100,
        totalPass: 80
    },
    {
        status: '未通过',
        id: '2',
        name: '三数之和',
        difficulty: '中等',
        totalAttempt: 100,
        totalPass: 60
    },
    {
        status: '未通过',
        id: '3',
        name: '四数之和',
        difficulty: '困难',
        totalAttempt: 100,
        totalPass: 40
    }
];
</script>

<style scoped lang="scss">
.problem_list {

    .search_setting {
        display: flex;
        align-items: center;
        /* 垂直居中对齐 */
        gap: 10px;
        /* 添加间隔以分开选择框和输入框 */
        margin-bottom: 20px;
        padding: 10px;
        /* 添加底部间距以分隔搜索设置和表格 */
        // background-color: #fff;
        /* 设置背景色为白色 */
        // border-radius: 8px;
        // box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
        /* 添加阴影 */

        el-select,
        el-input {
            background-color: #f5f5f5;
            /* 设置深灰色背景 */
            border-radius: 4px;
            /* 添加圆角 */
            border: 1px solid #ddd;
            /* 添加边框 */
        }

        el-select {
            min-width: 100px;
            /* 设置最小宽度以确保显示完整 */
        }

        el-input {
            flex: 1;
            /* 让输入框填满剩余空间 */
        }
    }

    .el-table {
        .el-table__header {
            background-color: #333;
            /* 设置表头的深色背景 */
            color: #fff;
            /* 设置表头文字颜色 */
        }

        .el-table__body {
            background-color: #fff;
            /* 保持表格主体为白色 */
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
