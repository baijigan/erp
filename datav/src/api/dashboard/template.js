import request from '@/utils/request'

const host= "http://api.cingsoft.net:8093"
export function getOrderList() {
  return request({
    url: host+ '/dashboard/template/order/list',
    method: 'get'
  })
}

export function getOrderTotal() {
  return request({
    url: host+ '/dashboard/template/order/total',
    method: 'get'
  })
}

export function getSalesPersonTotal() {
  return request({
    url: host+ '/dashboard/template/sales/person/total',
    method: 'get'
  })
}

export function getSalesAreaTotal() {
  return request({
    url: host+ '/dashboard/template/sales/area/total',
    method: 'get'
  })
}
