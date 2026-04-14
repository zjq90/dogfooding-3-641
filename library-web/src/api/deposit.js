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

export function addDepositRecord(borrowerId, amount, type, remark) {
  const params = new URLSearchParams()
  params.append('borrowerId', borrowerId)
  params.append('amount', amount)
  params.append('type', type)
  if (remark) {
    params.append('remark', remark)
  }
  
  return request({
    url: '/deposit',
    method: 'post',
    data: params,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

export function getTotalDeposit(borrowerId) {
  return request({
    url: `/deposit/total-deposit/${borrowerId}`,
    method: 'get'
  })
}

export function getTotalRefund(borrowerId) {
  return request({
    url: `/deposit/total-refund/${borrowerId}`,
    method: 'get'
  })
}
