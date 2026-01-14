import request from '@/utils/request'

// 查询设备传感器列表
export function listSensor(query) {
    return request({
        url: '/monitor/sensor/list',
        method: 'get',
        params: query
    })
}

// 查询指定设备的传感器（不分页）
export function listSensorByDevice(deviceNumber) {
    return request({
        url: '/monitor/sensor/listByDevice/' + deviceNumber,
        method: 'get'
    })
}

// 查询设备传感器详细
export function getSensor(sensorId) {
    return request({
        url: '/monitor/sensor/' + sensorId,
        method: 'get'
    })
}

// 新增设备传感器
export function addSensor(data) {
    return request({
        url: '/monitor/sensor',
        method: 'post',
        data: data
    })
}

// 修改设备传感器
export function updateSensor(data) {
    return request({
        url: '/monitor/sensor',
        method: 'put',
        data: data
    })
}

// 删除设备传感器
export function delSensor(sensorId) {
    return request({
        url: '/monitor/sensor/' + sensorId,
        method: 'delete'
    })
}
