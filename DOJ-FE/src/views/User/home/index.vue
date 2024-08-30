<template>
    <div class="user-profile">
        <div class="user-info">
            <div class="avatar-card">
                <img ref="avatarImage" src="@/assets/default.png" alt="avatar" class="avatar-img" />
                <div class="user-details">
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
                    <p class="user-sign">{{ form.sign }}</p>
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
                    <h2><i class="fa fa-user" aria-hidden="true" />用户名</h2>
                    <table>
                        <tbody>
                            <tr>
                                <td>{{ form.username }}</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <div class="item">
                    <h2><i class="fa fa-school" aria-hidden="true" />学校</h2>
                    <table>
                        <tbody>
                            <tr>
                                <td>{{ form.school }}</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <div class="item">
                    <h2><i class="fa fa-envelope" aria-hidden="true" />邮箱</h2>
                    <table>
                        <tbody>
                            <tr>
                                <td>{{ form.email }}</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <div class="item">
                    <h2><i class="fa fa-link" aria-hidden="true" />个人主页</h2>
                    <table>
                        <tbody>
                            <tr>
                                <td>{{ form.url == '' ? '暂无' : form.url }}</td>
                            </tr>
                        </tbody>
                    </table>
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
import { ElForm } from 'element-plus';
import { Star, CircleCheck, Male, Female } from '@element-plus/icons-vue';
import { userInfoResponseData } from '@/api/user/type';
import { reqUserInfo } from '@/api/user';
import { useUserStore } from '@/stores/userStore';
import * as echarts from "echarts";

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

onMounted(async () => {
    const userInfoResponseData = await reqUserInfo(userStore.userInfo!.userId);
    form.value = userInfoResponseData.data.data;
    if (form.value.avatar) {
        avatarImage.value!.src = import.meta.env.VITE_APP_URL + form.value.avatar;
    };

    generatePieChart();
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
            width: 440px;

            .avatar-img {
                border-top-left-radius: 10px;
                border-top-right-radius: 10px;
                border: 10px solid #3f444d;
                border-image: linear-gradient(45deg, #ff6b6b, #f8e71c, #50e3c2, #4a90e2) 1;
            }

            .user-details {
                width: 100%;

                .user-name {
                    padding: 10px 20px;
                    font-size: 20px;
                    font-weight: bold;
                }

                .user-sign {
                    padding: 0px 20px;
                    font-size: 16px;
                    color: #666;
                }

                .summary-status {
                    border-top: 2px solid #B3B3B3;
                    margin-top: 10px;
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

                table {
                    width: 100%;
                    border-collapse: collapse;

                    td {
                        padding: 15px;
                        font-size: 16px;
                        text-align: left;
                    }
                }
            }
        }

        .pie-status {

            flex: 1;

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
                    height: 465px;
                }
            }
        }
    }
}
</style>