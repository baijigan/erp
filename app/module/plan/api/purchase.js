import request from '@/utils/request'

//查询单据
export function listBill(data) {
  return request({
    method: "get",
    url: "/mp/plan/list",
    params: data
  })
}

// 查询明细
export function listDetail(data) {
  return request({
    method: "get",
    url: "/mp/plan/getDetail",
    params: data
  })
}

//字典
export function dictMatching (data) {
  return request({
    url: '/system/dict/data/type/'+ data,
    method: 'get',
  })
}



// 查询采购部门列表
export function listDept(query) {
  return request({
    url: '/system/dept/list',
    method: 'get',
    params: query
  })
}

// 查询采购人员列表
export function listUser(query) {
  return request({
    url: '/system/user/list',
    method: 'get',
    params: query
  })
}


// 查询物料分类列表
export function listSort() {
  return request({
    url: 'inv/sort/tree',
    method: 'get',
  })
}

// 查询料品大类
export function materialSort() {
  return request({
    url: '/wm/warehouses/getSort',
    method: 'get',
  })
}

// 查询物料基本信息
export function listMaterial(query) {
  return request({
    url: 'inv/items/list',
    method: 'get',
    params: query
  })
}

// 查看物料
export function seeMaterial(uniqueId) {
  return request({
    url: '/inv/items/' + uniqueId,
    method: 'get'
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

// 上一条下一条
export function NextBill(query) {
  return request({
    url: '/mp/plan/nextOrLast/',
    method: 'get',
    params: query
  })
}


//新增单据
export function addBill(data) {
  return request({
    method: "post",
    url: "/mp/plan",
    data: data
  })
}


// 查看单据
export function seeBill(query) {
  return request({
    url: '/mp/plan',
    method: 'get',
    params: query
  })
}

// 更新单据
export function updateBill(data) {
  return request({
    method: "put",
    url: "/mp/plan",
    data: data
  })
}

// 删除单据
export function deleteBill(data) {
  return request({
    url: '/mp/plan/delete',
    method: 'post',
    data: data
  })
}

//审核单据
export function examineBill(data) {
  return request({
    method: "post",
    url: "/mp/plan/batchCheck",
    data: data
  })
}

//反审核单据
export function examineDeBill(data) {
  return request({
    method: "post",
    url: "/mp/plan/batchAntiCheck",
    data: data
  })
}


// 获取销售员
export function mgrSalesman(power) {
  return request({
    url: '/system/user/roleSysUser/',
    method: 'get',
    params: power
  })
}

// 获取单据参数
export function billParameters(query) {
  return request({
    url: '/system/config/form',
    method: 'get',
    params: query
  })
}

//关联物料
export function listLink(query) {
  return request({
    url: '/mp/plan/chain',
    method: 'get',
    params: query
  })
}

//订单号查询
export function listCode(query) {
  return request({
    url: '/om/order/pipe/orderId',
    method: 'get',
    params: query
  })
}

// 导出明细
export function exportDetail(query) {
  return request({
    url: '/mp/plan/detailExport',
    method: 'get',
    params: query
  })
}

//模块参数
export function moduleParameters(query) {
  return request({
    url: '/system/config/list',
    method: 'get',
    params: query
  })
}

// 操作单据
export function operationBill(data) {
  return request({
    url: '/mp/plan/operate',
    method: 'post',
    data: data
  })
}

// 查询生产计划单号
export function listPrsCode(query) {
  return request({
    url: '/mp/plan/getDetail/group',
    method: 'get',
    params: query
  })
}

// 引入物料清单
export function listImportMbom(data) {
  return request({
   method:"get",
   url:"/mp/plan/leadInto",
   params:data
  })
}