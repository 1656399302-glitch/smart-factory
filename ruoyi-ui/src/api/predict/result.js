import request from '@/utils/request'

// 查询预测结果列表
export function listResult(query) {
  return request({
    url: '/predict/result/list',
    method: 'get',
    params: query
  })
}

// 查询预测结果详细
export function getResult(id) {
  return request({
    url: '/predict/result/' + id,
    method: 'get'
  })
}

// 获取资产的维护记录列表
export function getMaintenanceRecords(assetType, assetId) {
  return request({
    url: '/predict/result/maintenance/' + assetType + '/' + assetId,
    method: 'get'
  })
}

// 确认预测结果
export function ackResult(id) {
  return request({
    url: '/predict/result/ack/' + id,
    method: 'post'
  })
}

// 忽略预测结果
export function ignoreResult(id) {
  return request({
    url: '/predict/result/ignore/' + id,
    method: 'post'
  })
}

// 标记预测结果为已完成
export function resolveResult(id) {
  return request({
    url: '/predict/result/resolve/' + id,
    method: 'post'
  })
}

// 删除预测结果
export function delResult(id) {
  return request({
    url: '/predict/result/' + id,
    method: 'delete'
  })
}

// 导出预测结果
export function exportResult(query) {
  return request({
    url: '/predict/result/export',
    method: 'post',
    params: query
  })
}
