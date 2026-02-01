import { Alova } from '@/utils/http/alova/index';
import type { ProductStatus } from '@/types/Product';

// 获取商品列表（服务于前端：商品列表展示、价格与状态）
export function getProductList(params) {
  return Alova.Get('/commerce/products', { params });
}

// 更新商品上下架状态（服务于前端：商品状态展示）
export function updateProductStatus(data: { id: string; status: ProductStatus }) {
  return Alova.Post('/commerce/products/status', data);
}
