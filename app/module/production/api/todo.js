import request from '@/utils/request'

// 查询翔云待办列表
export function listTodo(query) {
  return request({
    url: '/xiangyun/todo/list',
    method: 'get',
    params: query
  })
}

// 查询翔云待办详细
export function getTodo(autoId) {
  return request({
    url: '/xiangyun/todo/' + autoId,
    method: 'get'
  })
}

// 新增翔云待办
export function addTodo(data) {
  return request({
    url: '/xiangyun/todo',
    method: 'post',
    data: data
  })
}

// 修改翔云待办
export function updateTodo(data) {
  return request({
    url: '/xiangyun/todo',
    method: 'put',
    data: data
  })
}

// 删除翔云待办
export function delTodo(autoId) {
  return request({
    url: '/xiangyun/todo/' + autoId,
    method: 'delete'
  })
}
