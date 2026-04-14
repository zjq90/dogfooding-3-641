import request from '@/utils/request'

export function getBorrowOrderList(params) {
  return request({
    url: '/borrow-order/page',
    method: 'get',
    params
  })
}

export function getBorrowOrderById(id) {
  return request({
    url: `/borrow-order/${id}`,
    method: 'get'
  })
}

export function createBorrowOrder(borrowerId, bookId, borrowDays, depositAmount) {
  const params = new URLSearchParams()
  params.append('borrowerId', borrowerId)
  params.append('bookId', bookId)
  params.append('borrowDays', borrowDays)
  if (depositAmount) {
    params.append('depositAmount', depositAmount)
  }
  
  return request({
    url: '/borrow-order',
    method: 'post',
    data: params,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

export function returnBook(orderId) {
  return request({
    url: `/borrow-order/${orderId}/return`,
    method: 'put'
  })
}

export function refundDeposit(orderId) {
  return request({
    url: `/borrow-order/${orderId}/refund-deposit`,
    method: 'put'
  })
}

export function updatePaymentStatus(orderId, paymentStatus) {
  return request({
    url: `/borrow-order/${orderId}/payment-status`,
    method: 'put',
    params: { paymentStatus }
  })
}

export function getOrderCount() {
  return request({
    url: '/borrow-order/count',
    method: 'get'
  })
}
