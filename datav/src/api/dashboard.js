import request from '@/utils/request'

export function getList(params) {
  return request({
    url: '/bulletin/s1/centerleft1',
    method: 'get',
    params
  })
}


