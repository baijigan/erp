import request from '@/utils/request'

//查询单据
export function listBill(data){
	return request({
		method:"get",
		url:"/prs/product/list",
		params:data
	})
}

// 查询明细
export function listDetail(data) {
  return request({
    method: "get",
    url: "/prs/product/getDetail",
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

// 获取仓库列表
export function listWarehouses(query) {
  return request({
    url: '/wm/warehouses/listAll',
    method: 'get',
    params: query
  })
}


// 上一条下一条
export function NextBill(query) {
  return request({
    url: '/prs/product/nextOrLast/',
     method: 'get',
    params: query
  })
}


//新增单据
export function addBill(data){
	return request({
		method:"post",
		url:"/prs/product",
		data:data
	})
}


// 查看单据
export function seeBill(query) {
  return request({
    url: '/prs/product',
    method: 'get',
    params:query
  })
}

// 更新单据
export function updateBill(data){
	return request({
		method:"put",
		url:"/prs/product",
		data:data
	})
}

// 删除单据
export function deleteBill(data) {
  return request({
    url: '/prs/product/delete',
   method: 'post',
    data:data
  })
}

//审核单据
export function examineBill(data){
	return request({
		method:"post",
		url:"/prs/product/check",
		data:data
	})
}

//反审核单据
export function examineDeBill(data){
	return request({
		method:"post",
		url:"/prs/product/antiCheck",
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
    url: '/prs/product/chain',
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
    url: '/prs/product/detailExport',
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
    url: '/prs/product/operate',
    method: 'post',
    data: data
  })
}

// 选择生产订单
export function listImportBill(data) {
  return request({
   method:"get",
   url:"/prs/product/lead",
   params:data
  })
}


// 引入工艺路线
export function listImportDetail(query) {
  return request({
    url: '/prs/process',
    method: 'get',
    params: query
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

// 查询工人
export function listWorker(query) {
  return request({
    url: '/prs/worker/list',
    method: 'get',
    params: query
  })
}

// 查询班组
export function listTeam(query) {
  return request({
    url: '/prs/team/list',
    method: 'get',
    params: query
  })
}


//新增报工单
export function addBillYield(data){
	return request({
		method:"post",
		url:"/prs/yield",
		data:data
	})
}

// 查看单据
export function seeBillYield(query) {
  return request({
    url: '/prs/yield',
    method: 'get',
    params:query
  })
}

// 更新报工单
export function updateBillYield(data){
	return request({
		method:"put",
		url:"/prs/yield",
		data:data
	})
}


//审核报工单
export function examineBillYield(data){
	return request({
		method:"post",
		url:"/prs/yield/check",
		data:data
	})
}

//反审核报工单
export function examineDeBillYield(data){
	return request({
		method:"post",
		url:"/prs/yield/antiCheck",
		data:data
	})
}

// 查询工艺路线
export function seeProcess(query) {
  return request({
    url: '/prs/process',
    method: 'get',
    params: query
  })
}
