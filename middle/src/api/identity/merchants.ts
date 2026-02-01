import { Alova } from '@/utils/http/alova/index';
import type { MerchantStatus } from '@/types/Merchant';

// 获取商家列表（服务于前端：商家身份/店铺状态影响商品展示）
export function getMerchantList(params) {
  return Alova.Get('/identity/merchants', { params });
}

// 更新商家状态（服务于前端：商家资质审核/禁用影响前端可购商品）
export function updateMerchantStatus(data: { id: string; status: MerchantStatus }) {
  return Alova.Post('/identity/merchants/status', data);
}
