import { Alova } from '@/utils/http/alova/index';
import type { CustomerStatus } from '@/types/Customer';

// 获取用户列表（服务于前端：用户登录与订单查询）
export function getCustomerList(params) {
  return Alova.Get('/identity/users', { params });
}

// 更新用户状态（服务于前端：用户可登录/下单）
export function updateCustomerStatus(data: { id: string; status: CustomerStatus }) {
  return Alova.Post('/identity/users/status', data);
}
