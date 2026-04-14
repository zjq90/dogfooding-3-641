import request from '@/utils/request'

export function getDepositList(params) {
  return request({
    url: '/deposit/page',
    method: 'get',
    params
  })
}

export function getDepositById(id) {
  return request({
    url: `/deposit/${id}`,
    method: 'get'
  })
}
