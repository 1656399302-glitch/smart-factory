import request from '@/utils/request'

// 查询报警规则列表
export function listRule(query) {
    return request({
        url: '/monitor/rule/list',
        method: 'get',
        params: query
    })
}

// 查询报警规则详细
export function getRule(ruleId) {
    return request({
        url: '/monitor/rule/' + ruleId,
        method: 'get'
    })
}

// 新增报警规则
export function addRule(data) {
    return request({
        url: '/monitor/rule',
        method: 'post',
        data: data
    })
}

// 修改报警规则
export function updateRule(data) {
    return request({
        url: '/monitor/rule',
        method: 'put',
        data: data
    })
}

// 删除报警规则
export function delRule(ruleId) {
    return request({
        url: '/monitor/rule/' + ruleId,
        method: 'delete'
    })
}
