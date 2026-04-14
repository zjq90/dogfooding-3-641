import request from '@/utils/request'

export function getBorrowerList(params) {
  return request({
    url: '/borrower/page',
    method: 'get',
    params
  })
}

export function getBorrowerById(id) {
  return request({
    url: `/borrower/${id}`,
    method: 'get'
  })
}

export function addBorrower(data) {
  return request({
    url: '/borrower',
    method: 'post',
    data
  })
}

export function updateBorrower(id, data) {
  return request({
    url: `/borrower/${id}`,
    method: 'put',
    data
  })
}

export function deleteBorrower(id) {
  return request({
    url: `/borrower/${id}`,
    method: 'delete'
  })
}

export function addDeposit(id, amount, remark) {
  const params = new URLSearchParams()
  params.append('amount', amount)
  if (remark) params.append('remark', remark)
  
  return request({
    url: `/borrower/${id}/deposit`,
    method: 'post',
    data: params,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

export function refundDeposit(id, amount, remark) {
  const params = new URLSearchParams()
  params.append('amount', amount)
  if (remark) params.append('remark', remark)
  
  return request({
    url: `/borrower/${id}/refund`,
    method: 'post',
    data: params,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

export function getAllBorrowers() {
  return request({
    url: '/borrower/list',
    method: 'get'
  })
}
