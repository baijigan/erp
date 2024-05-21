import request from '@/utils/request'

//查询入出库单
export function listMaster(data) {
  return request({
    method: "get",
    url: "/wm/out/list",
    params: data
  })
}

// 获取仓库字典设置
export function invoiceType(query) {
  return request({
    url: '/system/dict/data/list',
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

//显示生产线
export function listProductLine(query){
  return request({
    url: '/prs/beltline/list',
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

// 区间查询
export function listPeriod(query) {
  return request({
    url: '/wm/out/period',
    method: 'get',
    params: query
  })
}

// 查询加工设备
export function listEquipment(query) {
  return request({
    url: '/prs/equipment/list',
    method: 'get',
    params: query
  })
}

// 查询客户
export function listCustomer(query) {
  return request({
    url: '/om/customer/list',
    method: 'get',
    params: query
  })
}
// 新增客户
export function addCustomer(data) {
  return request({
    url: '/om/customer',
    method: 'post',
    data: data
  })
}

// 查询供方列表
export function listSupplier(query) {
  return request({
    url: '/po/supplier/list',
    method: 'get',
    params: query
  })
}

// 新增供应商
export function addSupplier(data) {
  return request({
    url: '/po/supplier',
    method: 'post',
    data: data
  })
}
// 查询料品大类
export function materialSort() {
  return request({
    url: '/wm/warehouses/getSort',
    method: 'get',
  })
}

// 查询生产商
export function listOutsource(query) {
  return request({
    url: '/pu/supplier/list',
    method: 'get',
    params: query
  })
}

// 新增生产商
export function addOutsource(data) {
  return request({
    url: '/pu/supplier',
    method: 'post',
    data: data
  })
}
// 查询部门列表
export function listDept(query) {
  return request({
    url: '/system/dept/list',
    method: 'get',
    params: query
  })
}

// 查询业务人员列表
export function listUser(query) {
  return request({
    url: '/system/user/list',
    method: 'get',
    params: query
  })
}

// 查询仓库信息
export function checkWarehouses(code) {
  return request({
    url: '/wm/warehouses/' + code,
    method: 'get'
  })
}

// 查询批次信息
export function batchNumberList(query) {
  return request({
    url: '/wm/out/batchSelect',
    method: 'get',
    params: query
  })
}

// 查询新增导入物料信息
export function  ImportMaterialList(query) {
  return request({
    url: '/wm/in/getInMaster',
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

// 导出物料
export function exportMaterial(query) {
  return request({
    url: '/inv/items/export',
    method: 'get',
    params: query
  })
}

// 下载物料导入模板
export function importMateria() {
  return request({
    url: '/inv/items/importTemplate',
    method: 'get'
  })
}

//新增出库单
export function addMaster(data) {
  return request({
    method: "post",
    url: "/wm/out",
    data: data
  })
}


// 查看出库单
export function seeMaster(query) {
  return request({
    url: '/wm/out/',
    method: 'get',
    params: query
  })
}

// 更新出库单
export function updateMaster(data) {
  return request({
    method: "put",
    url: "/wm/out",
    data: data
  })
}

// 删除出库单
export function deleteMaster(data) {
  return request({
    url: "/wm/out/remove",
    method: 'post',
    data:data
  })
}

//审核出库单
export function examineBill(data) {
  return request({
    method:"post",
    url: "/wm/out/batchCheck",
  	data:data
  })
}

//反审核出库单
export function examineDeBill(data) {
  return request({
    method:"post",
    url: "/wm/out/batchAntiCheck",
    data:data
  })
}

//包装单位
export function listPackage() {
  return request({
    method: "get",
    url: "/inv/package/list",
  })
}


// 上一条下一条
export function NextInMaster(query) {
  return request({
    url: '/wm/out/nextOrLast/',
    method: 'get',
    params: query
  })
}

// 查询货位树列表
export function listLocation(query) {
  return request({
    url: 'wm/location/list',
    method: 'get',
    params: query
  })
}

// 根据invCode获取物料关联详情
export function checkRelationCode(query) {
  return request({
    url: 'inv/items/getDetailItems',
    method: 'get',
    params: query
  })
}

//可结存日期
export function blanceAvailable(query) {
  return request({
    url: '/wm/period/available',
    method: 'get',
    params: query
  })
}

//查询仓库全部结存
export function blanceAll(query) {
  return request({
    url: '/wm/period/periodList',
    method: 'get',
    params: query
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

// 引入销售发货单单据
export function listSalesDeliverBill(data) {
  return request({
   method:"get",
   url:"/wm/out/om/deliver/leadInto",
   params:data
  })
}

// 引入销售发货单明细
export function listSalesDeliverDetail(data) {
  return request({
   method:"get",
   url:"/wm/out/om/deliver/leadInto/detail",
   params:data
  })
}

// 引入生产领料单据
export function listPrsPickBill(data) {
  return request({
   method:"get",
   url:"/wm/out/prs/pick/leadInto",
   params:data
  })
}

// 引生产领料单明细
export function listPrsPickDetail(data) {
  return request({
   method:"get",
   url:"/wm/out/prs/pick/leadInto/detail",
   params:data
  })
}

// 引入委外领料单据
export function listPuPickBill(data) {
  return request({
   method:"get",
   url:"/wm/out/pu/pick/leadInto",
   params:data
  })
}

// 引委外领料单明细
export function listPuPickDetail(data) {
  return request({
   method:"get",
   url:"/wm/out/pu/pick/leadInto/detail",
   params:data
  })
}

// 引入采购退货单据
export function listPurchaseRejectBill(data) {
  return request({
   method:"get",
   url:"/wm/out/po/reject/leadInto",
   params:data
  })
}

// 引生采购退货明细
export function listPurchaseRejectDetail(data) {
  return request({
   method:"get",
   url:"/wm/out/po/reject/leadInto/detail",
   params:data
  })
}

//关联物料
export function listLink(query) {
  return request({
    url: '/wm/in/chain',
    method: 'get',
    params: query
  })
}

// 查询明细
export function listDetail(data) {
  return request({
    method: "get",
    url: "/wm/out/getOutDetail",
    params: data
  })
}

// 导出明细
export function exportDetail(query) {
  return request({
    url: '/wm/out/DetailExport',
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
    url: '/wm/out/operate',
    method: 'post',
    data: data
  })
}

// 查询批次明细
export function batchDetail(query) {
  return request({
    url: '/wm/out/outBatch/list',
    method: 'get',
    params: query
  })
}
