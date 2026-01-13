import request from '@/utils/request'

// 运行预测任务
export function runPrediction(data) {
    return request({
        url: '/predict/task/run',
        method: 'post',
        data: data
    })
}
