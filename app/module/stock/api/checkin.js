import request from '@/utils/request'

//查询入库单
export function listMaster(data){
	return request({
		method:"get",
		url:"/wm/in/list",
		params:data
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

// 区间查询
export function listPeriod(query) {
  return request({
    url: '/wm/in/period',
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

// 查询料品大类
export function materialSort() {
  return request({
    url: '/wm/warehouses/getSort',
    method: 'get',
  })
}
// 查询新增导入物料信息
export function  ImportMaterialList(query) {
  return request({
    url: '/wm/out/getOutMaster',
    method: 'get',
    params: query
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

//新增入库单
export function addMaster(data){
	return request({
		method:"post",
		url:"/wm/in",
		data:data
	})
}


// 查看入库单
export function seeMaster(query) {
  return request({
    url: '/wm/in',
    method: 'get',
    params: query
  })
}

// 更新入库单
export function updateMaster(data){
	return request({
		method:"put",
		url:"/wm/in",
		data:data
	})
}

// 删除入库单
export function deleteMaster(data) {
  return request({
    url: '/wm/in/batch',
    method: 'post',
     data:data
  })
}

//审核入库单
export function examineBill(data){
	return request({
    method:"post",
		url:"/wm/in/batchChecks",
		data:data
	})
}

//反审核入库单
export function examineDeBill(data){
	return request({
    method:"post",
		url:"/wm/in/batchAntiChecks",
		data:data
	})
}

//包装单位
export function listPackage(){
	return request({
		method:"get",
		url:"/inv/package/list",
	})
}

// 上一条下一条
export function NextInMaster(query) {
  return request({
    url: '/wm/in/nextOrLast/',
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

// 引入采购到货单单据
export function listPurchaseCheckinBill(data) {
  return request({
   method:"get",
   url:"/wm/in/po/checkin/leadInto",
   params:data
  })
}

// 引入采购到货单明细
export function listPurchaseCheckinDetail(data) {
  return request({
   method:"get",
   url:"/wm/in/po/checkin/leadInto/detail",
   params:data
  })
}

// 引入生产入库单据
export function listPrsCheckinBill(data) {
  return request({
   method:"get",
   url:"/wm/in/prs/checkin/leadInto",
   params:data
  })
}

// 引入生产入库单明细
export function listPrsCheckinDetail(data) {
  return request({
   method:"get",
   url:"/wm/in/prs/checkin/leadInto/detail",
   params:data
  })
}

// 引入委外入库单据
export function listPuCheckinBill(data) {
  return request({
   method:"get",
   url:"/wm/in/pu/checkin/leadInto",
   params:data
  })
}

// 引入委外入库单明细
export function listPuCheckinDetail(data) {
  return request({
   method:"get",
   url:"/wm/in/pu/checkin/leadInto/detail",
   params:data
  })
}

// 引入销售退货单据
export function listOmRejectBill(data) {
  return request({
   method:"get",
   url:"/wm/in/om/reject/leadInto",
   params:data
  })
}

// 引入销售退货单明细
export function listOmRejectDetail(data) {
  return request({
   method:"get",
   url:"/wm/in/om/reject/leadInto/detail",
   params:data
  })
}

// 引入生产退料单据
export function listPrsUnPickBill(data) {
  return request({
   method:"get",
   url:"/wm/in/prs/unpick/leadInto",
   params:data
  })
}

// 引生产退料单明细
export function listPrsUnPickDetail(data) {
  return request({
   method:"get",
   url:"/wm/in/prs/unpick/leadInto/detail",
   params:data
  })
}

// 引入委外退料单据
export function listPuUnPickBill(data) {
  return request({
   method:"get",
   url:"/wm/in/pu/unpick/leadInto",
   params:data
  })
}

// 引委外退料单明细
export function listPuUnPickDetail(data) {
  return request({
   method:"get",
   url:"/wm/in/pu/unpick/leadInto/detail",
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

//订单号查询
export function listCode(query) {
  return request({
    url: '/om/order/pipe/orderId',
    method: 'get',
    params: query
  })
}

// 查询明细
export function listDetail(data) {
  return request({
    method: "get",
    url: "/wm/in/getInDetail",
    params: data
  })
}

// 导出明细
export function exportDetail(query) {
  return request({
    url: '/wm/in/DetailExport',
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
    url: '/wm/in/operate',
    method: 'post',
    data: data
  })
}