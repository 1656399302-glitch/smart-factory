import request from '@/utils/request'

// 查询工厂模型列表（分页）
export function listFactoryModel(query) {
  return request({
    url: '/three/factoryModel/list',
    method: 'get',
    params: query
  })
}

// 根据工厂ID查询工厂模型列表（不分页）
export function listFactoryModelByFactory(factoryId) {
  return request({
    url: '/three/factoryModel/byFactory/' + factoryId,
    method: 'get'
  })
}

// 查询工厂模型详细
export function getFactoryModel(id) {
  return request({
    url: '/three/factoryModel/' + id,
    method: 'get'
  })
}

// 新增工厂模型
export function addFactoryModel(data) {
  return request({
    url: '/three/factoryModel',
    method: 'post',
    data: data
  })
}

// 修改工厂模型
export function updateFactoryModel(data) {
  return request({
    url: '/three/factoryModel',
    method: 'put',
    data: data
  })
}

// 删除工厂模型
export function delFactoryModel(ids) {
  return request({
    url: '/three/factoryModel/' + ids,
    method: 'delete'
  })
}

// 发布工厂模型
export function publishFactoryModel(id) {
  return request({
    url: '/three/factoryModel/' + id + '/publish',
    method: 'post'
  })
}


