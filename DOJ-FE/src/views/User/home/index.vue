<template>
    <div class="user-profile">
        <div class="user-info">
            <div class="avatar-card">
                <img ref="avatarImage" src="@/assets/default.png" alt="avatar" class="avatar-img" />
                <div class="user-details">
                    <div class="user-basic">
                        <p class="user-name">{{ form.username }}
                            <span style="display: inline-block;">
                                <template v-if="form.gender">
                                    <el-icon>
                                        <Male />
                                    </el-icon>
                                </template>
                                <template v-else>
                                    <el-icon>
                                        <Female />
                                    </el-icon>
                                </template>
                            </span>
                        </p>
                        <el-tooltip effect="dark" :content="form.sign" placement="top">
                            <p class="user-sign">{{ form.sign }}</p>
                        </el-tooltip>
                    </div>
                    <div class="summary-status">
                        <a><el-icon>
                                <CircleCheck />
                            </el-icon>
                            <span class="status-text">通过题目 {{ form.easySolve + form.middleSolve + form.hardSolve }} 题</span>
                        </a>
                        <a><el-icon>
                                <Star />
                            </el-icon>
                            <span class="status-text">排名 {{ form.ranks }}</span>
                        </a>
                    </div>
                    <div class="society">
                        <div class="society-data">
                            <div>关注数</div>
                            <div>{{ form.subscribe }}</div>
                        </div>
                        <div class="society-data">
                            <div>粉丝数</div>
                            <div>{{ form.fans }}</div>
                        </div>
                        <div class="society-data">
                            <div>总得分</div>
                            <div>{{ form.score }}</div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="basic-info">
                <div class="item">
                    <h2><i class="fa fa-user" aria-hidden="true"></i>用户名</h2>
                    <el-tooltip effect="dark" :content="form.username" placement="top">
                        <p class="item-body">{{ form.username == '' ? '暂无' : form.username }}</p>
                    </el-tooltip>
                </div>
                <div class="item">
                    <h2><i class="fa fa-school" aria-hidden="true"></i>学校</h2>
                    <el-tooltip effect="dark" :content="form.school" placement="top">
                        <p class="item-body">{{ form.school == '' ? '暂无' : form.school }}</p>
                    </el-tooltip>
                </div>
                <div class="item">
                    <h2><i class="fa fa-envelope" aria-hidden="true"></i>邮箱</h2>
                    <el-tooltip effect="dark" :content="form.email" placement="top">
                        <p class="item-body">{{ form.email == '' ? '暂无' : form.email }}</p>
                    </el-tooltip>
                </div>
                <div class="item">
                    <h2><i class="fa fa-link" aria-hidden="true"></i>个人主页</h2>
                    <el-tooltip effect="dark" :content="form.url" placement="top">
                        <p class="item-body">{{ form.url == '' ? '暂无' : form.url }}</p>
                    </el-tooltip>
                </div>
            </div>
            <div class="pie-status">
                <div class="item">
                    <h2><i class="fa fa-pie-chart" aria-hidden="true" />提交统计</h2>
                    <div ref="submitRef" class="pie-body"></div>
                </div>
            </div>
        </div>
        <div class="user-submits">

        </div>
    </div>
</template>
  
<script lang="ts" setup>
import { Ref, ref, onMounted } from 'vue';
import { ElForm, ElTooltip } from 'element-plus';
import { Star, CircleCheck, Male, Female } from '@element-plus/icons-vue';
import { userInfoResponseData, userType } from '@/api/user/type';
import { reqUserInfo } from '@/api/user';
import { useUserStore } from '@/stores/userStore';
import * as echarts from "echarts";
import { nextTick } from 'vue';

const userStore = useUserStore();
const avatarImage = ref<HTMLImageElement>();
const submitRef = ref();
type FormRef = InstanceType<typeof ElForm>;
const formRef = ref<FormRef>();
let form: Ref<userInfoResponseData['data']> = ref({
    id: 0,
    username: '',
    avatar: '',
    email: '',
    password: '',
    score: 0,
    ranks: 0,
    school: '',
    gender: false,
    easySolve: 0,
    middleSolve: 0,
    hardSolve: 0,
    role: '',
    url: '',
    sign: '',
    fans: 0,
    subscribe: 0,
    ban: false,
});

function truncateText(): void {
    const selectedKeys: (keyof userType)[] = ['username', 'school', 'email', 'url'];

    selectedKeys.forEach((key) => {
        const element = document.getElementById(key);
        const tooltip = document.getElementById(`tooltip-${key}`);
        if (element && tooltip) {
            const originalText = element.innerText.trim();
            if (originalText.length > 13) {
                element.innerText = originalText.slice(0, 13) + '...';

                // 设置 tooltip 的内容
                tooltip.innerText = originalText;

                // 鼠标悬停时显示 tooltip
                element.addEventListener('mouseenter', () => {
                    tooltip.style.display = 'block'; // 显示 tooltip
                });

                // 鼠标移出时隐藏 tooltip
                element.addEventListener('mouseleave', () => {
                    tooltip.style.display = 'none'; // 隐藏 tooltip
                });
            } else {
                // 如果文本不需要截断，隐藏 tooltip
                tooltip.style.display = 'none';
            }
        }
    });
};

onMounted(async () => {
    const userInfoResponseData = await reqUserInfo(userStore.userInfo!.userId);
    form.value = userInfoResponseData.data.data;
    if (form.value.avatar) {
        avatarImage.value!.src = import.meta.env.VITE_APP_URL + form.value.avatar;
    };

    nextTick(() => {
        truncateText();
        generatePieChart();
    });
});

const generatePieChart = () => {
    const chart = echarts.init(submitRef.value);
    const _data = [
        { name: '正确', value: 11 },
        { name: '答案错误', value: 7 },
        { name: '时间超限', value: 15 },
        { name: '内存超限', value: 9 },
        { name: '运行错误', value: 6 },
        { name: '编译错误', value: 6 },
        { name: '未知错误', value: 0 },
    ];
    const data = _data.filter(item => item.value !== 0);
    const correct = _data.find(item => item.name === '正确');
    const error = _data.reduce((prev, curr) => prev + curr.value, 0) - correct!.value;
    const dataT = [
        { name: '正确', value: correct!.value },
        { name: '错误', value: error },
    ];

    // 自定义颜色数组
    const colorArray = ['#5470C6', '#91CC75', '#FAC858', '#9900CC', '#73C0DE', '#3BA272'];
    const richColor: any = {};
    colorArray.forEach((item, idx) => {
        richColor[`color${idx}`] = {
            fontSize: 14,
            fontWeight: 'bold',
            color: item
        }
    });
    const colorArrayT = ['#5470C6', '#EE6666'];

    const option = {
        tooltip: {
            trigger: 'item',
            backgroundColor: 'rgba(0, 0, 0, 0.7)',
            borderWidth: 0,
            textStyle: {
                color: '#fff'
            },
            formatter: function (params: any) {
                return `<b style="font-size: 14px;">${params.name}</b><br/>` +
                    `<span style="font-size: 12px;">${params.value} (${params.percent}%)</span>`;
            }
        },
        legend: {
            top: 10,
            icon: 'circle',
            show: true,
            data: data.map((item) => item.name),
            formatter: function (name: string) {
                const item = data.find((item) => item.name === name);
                if (item) {
                    return `${name}: ${item.value}`;
                }
                return name;
            },
            textStyle: {
                fontSize: 16,
                fontWeight: 'bold',
                lineHeight: '25',
            }
        },
        series: [
            {
                type: 'pie',
                center: ['50%', '60%'],
                radius: ['40%', '65%'],
                label: {
                    show: true,
                    position: 'outside',
                    formatter: function (params: any) {
                        return `{color${params.dataIndex}|${params.name}}`;
                    },
                    rich: richColor
                },
                labelLine: {
                    lineStyle: {
                        width: 2
                    }
                },
                data: data,
                emphasis: {
                    itemStyle: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                },
                itemStyle: {
                    // 将饼图的颜色设置为与自定义颜色数组对应
                    color: function (params: any) {
                        const colorIndex = data.findIndex(item => item.name === params.name);
                        return colorArray[colorIndex % colorArray.length];
                    }
                }
            },
            {
                type: 'pie',
                radius: ['0%', '30%'],
                center: ['50%', '60%'],
                label: {
                    show: true,
                    position: 'inside',
                    formatter: function (params: any) {
                        return `{color|${params.name}}`;
                    },
                    rich: {
                        color: {
                            color: '#FFF',
                            fontSize: 14,
                            fontWeight: 'bold'
                        }
                    }
                },
                labelLine: {
                    lineStyle: {
                        width: 2
                    }
                },
                data: dataT,
                emphasis: {
                    itemStyle: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                },
                itemStyle: {
                    // 将饼图的颜色设置为与自定义颜色数组对应
                    color: function (params: any) {
                        const colorIndex = dataT.findIndex(item => item.name === params.name);
                        return colorArrayT[colorIndex % colorArrayT.length];
                    }
                }
            }
        ],
    };
    chart.setOption(option);
};

</script>
  
<style scoped lang="scss">
.user-profile {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    padding: 20px;

    .user-info {
        display: flex;
        justify-content: start;
        align-items: start;
        gap: 20px;

        .avatar-card {
            display: flex;
            flex-direction: column;
            align-items: start;
            border-radius: 10px;
            background-color: #FFFFFF;
            box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
            width: 34.5%;

            .avatar-img {
                border-top-left-radius: 10px;
                border-top-right-radius: 10px;
                border: 10px solid #3f444d;
                border-image: linear-gradient(45deg, #ff6b6b, #f8e71c, #50e3c2, #4a90e2) 1;
                width: 100%;
                object-fit: contain;
                /* 保证图片不失真，裁剪超出部分 */
                aspect-ratio: 1 / 1;
                /* 确保图片保持1:1的比例 */
            }

            .user-details {
                width: 100%;

                .user-basic {
                    display: flex;
                    flex-direction: row;
                    justify-content: space-between;

                    .user-name {
                        padding: 13px 20px;
                        font-size: 20px;
                        font-weight: bold;
                    }

                    .user-sign {
                        padding: 13px 20px;
                        font-size: 20px;
                        color: #666;
                        white-space: nowrap;
                        /* 禁止换行 */
                        overflow: hidden;
                        /* 隐藏溢出的文本 */
                        text-overflow: ellipsis;
                        /* 显示省略号 */
                        max-width: 180px;
                        /* 设置最大宽度 */
                        cursor: pointer;
                        /* 鼠标指针变为可点击 */
                    }


                }

                .summary-status {
                    border-top: 2px solid #B3B3B3;
                    margin-top: 5px;
                    display: flex;
                    justify-content: space-between;
                    align-items: center;

                    a {
                        font-size: 18px;
                        padding: 10px 20px;
                        display: flex;
                        gap: 5px;
                        align-items: center;

                        &:hover {
                            color: #409EFF;
                        }

                        .status-text {
                            display: inline-block;
                            margin-bottom: 2px;
                        }
                    }
                }

                .society {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    // border-top: 2px solid #B3B3B3;

                    .society-data {
                        display: flex;
                        flex-direction: column;
                        align-items: center;
                        margin: 5px 20px;

                        div {
                            font-size: 16px;
                            color: #666;
                        }
                    }
                }
            }
        }

        .basic-info {
            display: flex;
            flex-direction: column;
            gap: 28px;
            width: 25%;

            .item {
                background-color: #f9f9f9;
                border-radius: 8px;
                box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
                width: 100%;

                h2 {
                    font-size: 18px;
                    margin: 0;
                    border-bottom: 1px solid #ccc;
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

                .item-body {
                    width: 100% !important;
                    padding: 15px;
                    font-size: 16px;
                    text-align: left;
                    white-space: nowrap;
                    /* 禁止换行 */
                    overflow: hidden !important;
                    /* 隐藏溢出的文本 */
                    text-overflow: ellipsis !important;
                    /* 显示省略号 */
                    max-width: 100% !important;
                    /* 设置最大宽度 */
                    cursor: pointer;
                    /* 鼠标指针变为可点击 */
                }
            }
        }

        .pie-status {
            width: 45%;

            .item {
                background-color: #f9f9f9;
                border-radius: 8px;
                box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);

                h2 {
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

                .pie-body {
                    width: 100%;
                    height: 460px;
                }
            }
        }
    }
}
</style>