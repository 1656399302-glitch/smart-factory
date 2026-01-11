import request from '@/utils/request'

// 查询设备模型列表（分页）
export function listDeviceModel(query) {
  return request({
    url: '/three/deviceModel/list',
    method: 'get',
    params: query
  })
}

// 查询设备模型详细
export function getDeviceModel(id) {
  return request({
    url: '/three/deviceModel/' + id,
    method: 'get'
  })
}

// 新增设备模型
export function addDeviceModel(data) {
  return request({
    url: '/three/deviceModel',
    method: 'post',
    data: data
  })
}

// 修改设备模型
export function updateDeviceModel(data) {
  return request({
    url: '/three/deviceModel',
    method: 'put',
    data: data
  })
}

// 删除设备模型
export function delDeviceModel(ids) {
  return request({
    url: '/three/deviceModel/' + ids,
    method: 'delete'
  })
}


