import request from '@/utils/request'

// 查询点位绑定列表（分页）
export function listBinding(query) {
  return request({
    url: '/three/deviceSceneBinding/list',
    method: 'get',
    params: query
  })
}

// 查询点位绑定详细
export function getBinding(id) {
  return request({
    url: '/three/deviceSceneBinding/' + id,
    method: 'get'
  })
}

// 新增点位绑定
export function addBinding(data) {
  return request({
    url: '/three/deviceSceneBinding',
    method: 'post',
    data: data
  })
}

// 修改点位绑定
export function updateBinding(data) {
  return request({
    url: '/three/deviceSceneBinding',
    method: 'put',
    data: data
  })
}

// 删除点位绑定
export function delBinding(ids) {
  return request({
    url: '/three/deviceSceneBinding/' + ids,
    method: 'delete'
  })
}


