<script lang="ts" setup>
// 你可以在此处导入需要的其他组件或工具函数
</script>

<template>
    <div class="home">

        <div class="left">
            <div class="announcement">
                <h2><i class="fas fa-bullhorn"></i> 公告</h2>
                <!-- 公告表格 -->
                <table class="announcement-table">
                    <thead>
                        <tr>
                            <th>标题</th>
                            <th>时间</th>
                            <th>用户</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td><a href="#">NewOJ用户答疑群: 649382035</a></td>
                            <td>2023-10-21 20:51:52</td>
                            <td>管理员</td>
                        </tr>
                        <tr>
                            <td><a href="#">Java和Python评测功能上线</a></td>
                            <td>2023-08-10 18:35:24</td>
                            <td>管理员</td>
                        </tr>
                        <tr>
                            <td><a href="#">《算法竞赛入门经典（第2版）》每日一题</a></td>
                            <td>2023-08-11 18:25:01</td>
                            <td>管理员</td>
                        </tr>
                        <tr>
                            <td><a href="#">NewOJ官方题库上线比赛功能</a></td>
                            <td>2023-04-17 21:48:25</td>
                            <td>管理员</td>
                        </tr>
                        <tr>
                            <td><a href="#">蓝桥杯历年真题答案下载地址</a></td>
                            <td>2023-04-16 18:20:43</td>
                            <td>管理员</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>

        <div class="right">
            <div class="coding-today">
                <h2><i class="fas fa-code"></i> 统计</h2>
                <table class="coding-stats-table">
                    <tbody>
                        <tr>
                            <td>今日提交</td>
                            <td class="count">3</td>
                        </tr>
                        <tr>
                            <td>本周提交</td>
                            <td class="count">123</td>
                        </tr>
                        <tr>
                            <td>30天提交</td>
                            <td class="count">809</td>
                        </tr>
                        <tr>
                            <td>累计提交</td>
                            <td class="count">526262</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>

    </div>
</template>

<style scoped lang="scss">
.home {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    padding: 20px;

    .left {
        width: 65%;
        display: flex;
        flex-direction: column;
        gap: 20px;
    }

    .right {
        width: 33%;
        display: flex;
        flex-direction: column;
        gap: 20px;
    }

    .announcement,
    .coding-today {
        background-color: #f9f9f9;
        border-radius: 8px;
        box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
    }

    .announcement h2,
    .coding-today h2 {
        font-size: 18px;
        margin: 0;
        border-bottom: 2px solid #ccc;
        padding: 15px;
        display: flex;
        align-items: center;
        gap: 8px;
        background-color: #F3F4F5;
        border-radius: 8px 8px 0 0;

        i {
            margin-right: 10px;
            color: #333;
        }
    }

    .announcement-table,
    .coding-stats-table {
        width: 100%;
        border-collapse: collapse;
    }


    .announcement-table th,
    .announcement-table td {
        padding: 12px;
        border-bottom: 1px solid #E8E9E9;
        font-size: 16px;
        text-align: left;
    }

    .announcement-table th {
        color: #333;
        /* 可选：设置表头文本颜色 */
        font-weight: bold;
        font-size: 16px;
    }

    .announcement-table a {
        color: #007bff;
        text-decoration: none;

        &:hover {
            text-decoration: underline;
        }
    }

    .coding-stats-table td {
        padding: 15px;
        border-bottom: 1px solid #E8E9E9;
        font-size: 16px;
        text-align: center;
    }

    /* 去掉最后一个元素的底部边框 */
    .announcement-table tr:last-child td,
    .coding-stats-table tr:last-child td {
        border-bottom: none;
    }
}
</style>