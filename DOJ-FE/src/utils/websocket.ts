import { ElNotification } from 'element-plus';
import router from '@/router';
import { h } from 'vue';

let socket: WebSocket | null = null;

// 辅助函数：创建丰富的通知内容
const renderResultMessage = (result: any) => {
    const time = result.time ? `${(result.time * 1000).toFixed(2)} ms` : 'N/A';
    const memory = result.memory ? `${result.memory.toFixed(2)} KB` : 'N/A';

    return h('div', null, [
        h('p', { style: { margin: '0 0 5px 0' } }, [
            h('strong', null, '状态: '),
            h('span', { style: { fontWeight: 'bold', color: result.status === 'Accepted' ? '#67c23a' : '#f56c6c' } }, ` ${result.status}`)
        ]),
        h('p', { style: { margin: '0 0 5px 0' } }, [
            h('strong', null, '耗时: '),
            h('span', null, time)
        ]),
        h('p', { style: { margin: '0 0 5px 0' } }, [
            h('strong', null, '内存: '),
            h('span', null, memory)
        ]),
        result.message && result.status !== 'Accepted' ? h('div', null, [
            h('strong', null, '信息: '),
            h('pre', { style: { marginTop: '5px', padding: '5px', background: '#f5f5f5', border: '1px solid #e3e3e3', borderRadius: '4px', whiteSpace: 'pre-wrap', wordBreak: 'break-all' } }, result.message)
        ]) : null
    ]);
};

const connectWebSocket = () => {
    if (socket && socket.readyState === WebSocket.OPEN) {
        return;
    }
    if (socket && socket.readyState === WebSocket.CONNECTING) {
        return;
    }

    const wsUrl = 'ws://127.0.0.1:8084/ws/submission'; // 直接连接后端 WebSocket 地址

    socket = new WebSocket(wsUrl);

    socket.onopen = () => {
        console.log('WebSocket 连接已建立。');
    };

    socket.onmessage = (event) => {
        try {
            const result = JSON.parse(event.data);
            console.log('收到判题结果:', result);

            ElNotification({
                title: `提交 #${result.id} 已完成`,
                message: renderResultMessage(result),
                type: result.status === 'Accepted' ? 'success' : 'error',
                duration: 15000, // 延长显示时间
                onClick: () => {
                    router.push(`/status?submissionId=${result.id}`);
                },
                position: 'bottom-right',
            });

        } catch (e) {
            console.error('处理 WebSocket 消息失败:', e);
        }
    };

    socket.onclose = (event) => {
        console.log('WebSocket 连接已关闭:', event);
        socket = null;
        // 可选：实现一个延迟重连机制
        // setTimeout(() => {
        //     console.log('尝试重新连接 WebSocket...');
        //     connectWebSocket();
        // }, 5000);
    };

    socket.onerror = (error) => {
        console.error('WebSocket 发生错误:', error);
        // 可以在这里添加一些错误提示
    };
};

const subscribeToSubmission = (submissionId: number) => {
    const trySubscribe = () => {
        if (socket && socket.readyState === WebSocket.OPEN) {
            console.log(`订阅提交 ID: ${submissionId}`);
            socket.send(JSON.stringify({ submissionId }));
        } else if (socket && socket.readyState === WebSocket.CONNECTING) {
            // 如果正在连接，则等待一会再试
            console.log('WebSocket 正在连接，稍后重试订阅...');
            setTimeout(trySubscribe, 500);
        } else {
            // 如果连接已关闭或不存在，则尝试重连并订阅
            console.log('WebSocket 未连接，正在尝试重新连接...');
            connectWebSocket();
            setTimeout(trySubscribe, 500);
        }
    };
    trySubscribe();
};

export const useWebSocket = () => {
    // 确保只有一个 WebSocket 连接实例
    if (!socket) {
        connectWebSocket();
    }

    return {
        subscribeToSubmission
    };
};