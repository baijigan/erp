import request from '@/utils/request'

// 分类查询物料
export function listSortMaterial(query) {
  return request({
    url: '/wm/balance/sort/list',
    method: 'get',
    params: query
  })
}

// 仓库查询物料
export function listWhMaterial(query) {
  return request({
    url: '/wm/balance/wm/list',
    method: 'get',
    params: query
  })
}

// 物料汇总
export function seeMaterial(query) {
  return request({
    url: '/wm/balance/peek',
    method: 'get',
    params:query
  })
}

// 物料明细
export function detailedMaterial(query) {
 return request({
   url: '/wm/balance/detail',
   method: 'get',
   params: query
 })
}


// 查询计量单位
export function listPacking(query) {
  return request({
    url: 'inv/unit/list',
    method: 'get',
    params: query
  })
}

// 导出分类物料
export function exportSortMaterial(query) {
  return request({
    url: '/wm/balance/sort/export',
    method: 'get',
    params: query
  })
}
// 导出仓库物料
export function exportWhMaterial(query) {
  return request({
    url: '/wm/balance/wm/export',
    method: 'get',
    params: query
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
