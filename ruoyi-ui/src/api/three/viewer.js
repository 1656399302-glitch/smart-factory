import request from '@/utils/request'

// Viewer 聚合接口：发布态工厂模型 + 设备点位 + 状态
export function getViewer(factoryId) {
  return request({
    url: '/scene/viewer/' + factoryId,
    method: 'get'
  })
}


