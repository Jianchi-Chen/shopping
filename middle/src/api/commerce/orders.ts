import { Alova } from '@/utils/http/alova/index';
import type { OrderStatus, RefundStatus } from '@/types/Order';

// 获取订单列表（服务于前端：订单列表与状态展示）
export function getOrderList(params) {
  return Alova.Get('/commerce/orders', { params });
}

// 更新订单状态（服务于前端：订单状态流转/售后进度展示）
export function updateOrderStatus(data: { id: string; status: OrderStatus }) {
  return Alova.Post('/commerce/orders/status', data);
}

// 更新售后状态（服务于前端：售后进度展示）
export function updateRefundStatus(data: { id: string; refundStatus: RefundStatus }) {
  return Alova.Post('/commerce/orders/refund', data);
}
