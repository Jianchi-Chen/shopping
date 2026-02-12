import { Alova } from '@/utils/http/alova/index';

/**
 * 用户列表
 */
export function getUserList(params: any) {
  return Alova.Get('/identity/users', { params });
}

/**
 * 更新用户状态
 */
export function updateUserStatus(data: { id: string; status: string }) {
  return Alova.Post('/identity/users/status', data);
}
