import request from '@/utils/request'

// 查询报警记录列表
export function listAlarm(query) {
    return request({
        url: '/monitor/alarm/list',
        method: 'get',
        params: query
    })
}

// 查询报警记录详细
export function getAlarm(alarmId) {
    return request({
        url: '/monitor/alarm/' + alarmId,
        method: 'get'
    })
}

// 确认报警
export function ackAlarm(alarmId) {
    return request({
        url: '/monitor/alarm/ack/' + alarmId,
        method: 'post'
    })
}

// 关闭报警
export function resolveAlarm(alarmId, remark) {
    return request({
        url: '/monitor/alarm/resolve/' + alarmId,
        method: 'post',
        data: { remark: remark }
    })
}
