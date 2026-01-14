import request from '@/utils/request'

// 查询传感器数据列表（历史数据）
export function listSensorData(query) {
    return request({
        url: '/monitor/data/list',
        method: 'get',
        params: query
    })
}

// 获取指定设备各传感器的最新数据
export function getLatestData(deviceNumber) {
    return request({
        url: '/monitor/data/latest',
        method: 'get',
        params: { deviceNumber: deviceNumber }
    })
}

// 采集上报接口
export function collectData(data) {
    return request({
        url: '/monitor/data/collect',
        method: 'post',
        data: data
    })
}

// 删除传感器数据
export function delSensorData(ids) {
    return request({
        url: '/monitor/data/' + ids,
        method: 'delete'
    })
}
