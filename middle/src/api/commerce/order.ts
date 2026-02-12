import { Alova } from '@/utils/http/alova/index';

/**
 * 订单列表
 */
export function getOrderList(params: any) {
  return Alova.Get('/commerce/orders', { params });
}

/**
 * 更新订单状态
 */
export function updateOrderStatus(data: { id: string; status: string }) {
  return Alova.Post('/commerce/orders/status', data);
}

/**
 * 退款处理
 */
export function handleRefund(data: { id: number; refundStatus: string }) {
  return Alova.Post('/commerce/orders/refund', { data });
}
