import request from '@/utils/request'

//查询订单
export function listOrder(data) {
  return request({
    method: "get",
    url: "/datav/sequence/list",
    params: data
  })
}

//显示图像
export function sequenceView(data) {
  return request({
    method: "get",
    url: "/datav/sequence/view",
    params: data
  })
}

//显示详细
export function sequenceDetail(data) {
  return request({
    method: "get",
    url: "/datav/sequence/detail",
    params: data
  })
}