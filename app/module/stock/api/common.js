import request from '@/utils/request'

// 查询部门列表
export function listDept(query) {
  return request({
    url: '/system/dept/list',
    method: 'get',
    params: query
  })
}

//字典
export function dictMatching (data) {
  return request({
    url: '/system/dict/data/type/'+ data,
    method: 'get',
  })
}

// 获取仓库列表
export function listWarehouses(query) {
  return request({
    url: '/wm/warehouses/list',
    method: 'get',
    params: query
  })
}