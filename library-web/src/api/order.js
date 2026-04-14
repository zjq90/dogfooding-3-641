import request from '@/utils/request'

export function getOrderList(params) {
  return request({
    url: '/order/page',
    method: 'get',
    params
  })
}

export function getOrderById(id) {
  return request({
    url: `/order/${id}`,
    method: 'get'
  })
}

export function createOrder(data) {
  return request({
    url: '/order',
    method: 'post',
    data
  })
}

export function returnOrder(id) {
  return request({
    url: `/order/${id}/return`,
    method: 'put'
  })
}

export function getOrderStats() {
  return request({
    url: '/order/stats',
    method: 'get'
  })
}

export function getOrderMonthlyStats() {
  return request({
    url: '/order/stats/monthly',
    method: 'get'
  })
}
