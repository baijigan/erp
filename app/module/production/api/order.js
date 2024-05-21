import request from '@/utils/request'

//查询单据
export function listBill(data){
	return request({
		method:"get",
		url:"/prs/order/list",
		params:data
	})
}

// 查询明细
export function listDetail(data) {
  return request({
    method: "get",
    url: "/prs/order/getDetail",
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

// 引入MBOM主表
export function listImportPlan(data) {
  return request({
   method:"get",
   url:"/prs/order/leadInto/plan",
   params:data
  })
}

// 引入
export function listImportSales(data) {
  return request({
    method:"get",
    url:"/prs/order/leadInto/sales",
    params:data
   })
}


// 查询料品大类
export function materialSort() {
  return request({
    url: '/wm/warehouses/getSort',
    method: 'get',
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

// 上一条下一条
export function NextBill(query) {
  return request({
    url: '/prs/order/nextOrLast/',
     method: 'get',
    params: query
  })
}


//新增单据
export function addBill(data){
	return request({
		method:"post",
		url:"/prs/order",
		data:data
	})
}


// 查看单据
export function seeBill(query) {
  return request({
    url: '/prs/order',
    method: 'get',
    params:query
  })
}

// 更新单据
export function updateBill(data){
	return request({
		method:"put",
		url:"/prs/order",
		data:data
	})
}

// 删除单据
export function deleteBill(data) {
  return request({
      url: '/prs/order/delete',
     method: 'post',
     data:data
  })
}

//审核单据
export function examineBill(data){
	return request({
		method:"post",
		url:"/prs/order/check",
		data:data
	})
}

//反审核单据
export function examineDeBill(data){
	return request({
		method:"post",
		url:"/prs/order/antiCheck",
		data:data
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
    url: '/prs/order/chain',
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
    url: '/prs/order/detailExport',
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
    url: '/prs/order/operate',
    method: 'post',
    data: data
  })
}

// 查询产线
export function listBeltline(query) {
  return request({
    url: '/prs/beltline/list',
    method: 'get',
    params: query
  })
}

// 查询领料
export function listPick(data){
	return request({
		method:"get",
		url:"/prs/pick/getDetail",
		params:data
	})
}

// 查询完工
export function listProduct(data){
	return request({
		method:"get",
		url:"/prs/product/list",
		params:data
	})
}


// 获取仓库列表
export function listWarehouses(query) {
  return request({
    url: '/wm/warehouses/listAll',
    method: 'get',
    params: query
  })
}

// 操作->更新生产日期
export function operationArrangeDate(data) {
  return request({
    url: '/prs/order/check',
    method: 'put',
    data: data
  })
}

// 操作->更新生产线
export function operationBeltline(data) {
  return request({
    url: '/prs/order/check',
    method: 'put',
    data: data
  })
}

// 获取工艺路线
export function listProcess(query) {
  return request({
    url: '/prs/process/list',
    method: 'get',
    params: query
  })
}