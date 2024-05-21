import request from '@/utils/request'

// 登录方法
export function login(tenant, username, password, code, uuid) {
  const data = {
	tenant,
    username,
    password,	
    code,
    uuid
  }
  return request({
    'url': '/login',
    headers: {
      isToken: false
    },
    'method': 'post',
    'data': data
  })
}

// 获取用户详细信息
export function getInfo() {
  return request({
    'url': '/getInfo',
    'method': 'get'
  })
}

// 退出方法
export function logout() {
  return request({
    'url': '/auth/logout',
    'method': 'delete'
  })
}

// 获取验证码
export function getCodeImg(query) {
  return request({
    'url': '/captchaImage',
    headers: {
      isToken: false
    },
    method: 'get',
	params: query,
    timeout: 20000
  })
}
