import { Alova } from '@/utils/http/alova/index';

/**
 * 商家列表
 */
export function getMerchantList(params: any) {
  return Alova.Get('/identity/merchants', { params });
}

/**
 * 更新商家状态
 */
export function updateMerchantStatus(data: { id: number; status: string }) {
  return Alova.Post('/identity/merchants/status', { data });
}
