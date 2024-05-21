import request from '@/utils/request'

//字典
export function dictMatching (data) {
  return request({
    url: '/system/dict/data/type/'+ data,
    method: 'get',
  })
}