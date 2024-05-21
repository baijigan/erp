import request from '@/utils/request'

//查询单据
export function listBill(data) {
  return request({
    method: "get",
    url: "/mp/order/list",
    params: data
  })
}

// 查询明细
export function listDetail(data) {
  return request({
    method: "get",
    url: "/mp/order/getDetail",
    params: data
  })
}

// 查询流程
export function listUnconfirmed(data) {
  return request({
    method: "post",
    url: "/mp/order/getUnconfirmed",
    data: data
  })
}

//字典
export function dictMatching(data) {
  return request({
    url: '/system/dict/data/type/' + data,
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

// 引入销售订单单据
export function listImportBill(data) {
  return request({
    method: "get",
    url: "/mp/order/lead",
    params: data
  })
}


// 引入销售订单明细
export function listImportDetail(data) {
  return request({
    method: "get",
    url: "/mp/order/leadInto",
    params: data
  })
}

// 引入BOM单据
export function bomImportBill(data) {
  return request({
    method: "get",
    url: "/mp/order/leadBom",
    params: data
  })
}

// 引入BOM明细
export function bomImportDetail(data) {
  return request({
    method: "get",
    url: "/mp/order/leadIntoBom",
    params: data
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
    url: '/mp/order/nextOrLast/',
    method: 'get',
    params: query
  })
}


//新增单据
export function addBill(data) {
  return request({
    method: "post",
    url: "/mp/order",
    data: data
  })
}


// 查看单据
export function seeBill(query) {
  return request({
    url: '/mp/order',
    method: 'get',
    params: query
  })
}

// 更新单据
export function updateBill(data) {
  return request({
    method: "put",
    url: "/mp/order",
    data: data
  })
}

// 删除单据
export function deleteBill(data) {
  return request({
    url: '/mp/order/delete',
    method: 'post',
    data: data
  })
}

//审核单据
export function examineBill(data) {
  return request({
    method: "post",
    url: "/mp/order/batchCheck",
    data: data
  })
}

//反审核单据
export function examineDeBill(data) {
  return request({
    method: "post",
    url: "/mp/order/batchAntiCheck",
    data: data
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
    url: '/mp/order/chain',
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
    url: '/mp/order/getDetail/export',
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
    url: '/mp/order/operate',
    method: 'post',
    data: data
  })
}

// 齐套
export function billBeReady(data) {
  return request({
    url: '/mp/order/beReady',
    method: 'post',
    data: data
  })
}

// 下发生产
export function billPrsStatus(data) {
  return request({
    url: '/mp/order/prsStatus',
    method: 'post',
    data: data
  })
}

//根据sortName查找
export function listSortChild(query) {
  return request({
    url: '/inv/selectChild',
    method: 'get',
    params: query
  })
}

//查询配方单据
export function listRdCodeBill(data) {
  return request({
    method: "get",
    url: "/mp/order/leadBom",
    params: data
  })
}

// 查询配方明细
export function listRdCodeDetail(data) {
  return request({
    method: "get",
    url: "/mp/order/leadIntoBom",
    params: data
  })
}

// 物料计算
export function listCreate(data) {
  return request({
    method: "post",
    url: "/mp/order/createMpMbom",
    data: data
  })
}

//物料清单主表
export function listMbom(data) {
  return request({
    method: "get",
    url: "/mp/mbom/list",
    params: data
  })
}


//查询计划单据
export function listPurchase(data) {
  return request({
    method: "get",
    url: "/mp/plan/list",
    params: data
  })
}

//查询订单流程
export function listSalve(data) {
  return request({
    method: "post",
    url: "/mp/order/getSalve",
    data: data
  })
}

//查询订单流程
export function listFlow(data) {
  return request({
    method: "get",
    url: "/mp/order/flow/list",
    params: data
  })
}

//查询配方单据有条件
export function listHasRdCodeBill(data) {
  return request({
    method: "get",
    url: "/rd/ebom/list/new",
    params: data
  })
}

//根据计划单查物料清单
export function listMpMbom(data) {
  return request({
    method: "get",
    url: "/mp/order/getMpMbom",
    params: data
  })
}

export function listMpReady(data) {
  return request({
    method: "get",
    url: "/mp/order/getMpReady",
    params: data
  })
}

//生产车间/生产日期/下发
export function operationMbom(data) {
  return request({
    method: "post",
    url: "/mp/order/issued",
    data: data
  })
}


/**
 * 翻译-》列表
 * dictData 字典数据
 * needData 翻译数据
 * needStr 翻译内容
 *  strCn 赋值字段
 *  dictValue 匹配值
 * dictLabel 匹配中文
 */
export function Translate(needData=[],dictData=[],needStr="",strCn="",dictValue="dictValue",dictLabel="dictLabel"){
  return new Promise(resolve=>{
    if(needData.length>0&&dictData.length>0&&needStr!==""){
      needData.forEach(item=>{
        dictData.forEach(obj=>{
          if(item[needStr] == obj[dictValue]){
            item[strCn]= obj[dictLabel];
          }
        })
      }) 
    }
    resolve(needData)
  })
}
