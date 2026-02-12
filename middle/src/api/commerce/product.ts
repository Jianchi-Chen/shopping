import { Alova } from '@/utils/http/alova/index';

/**
 * 商品列表
 */
export function getProductList(params: any) {
  return Alova.Get('/commerce/products', { params });
}

/**
 * 商品详情
 */
export function getProductDetail(id: number) {
  return Alova.Get(`/commerce/products/${id}`);
}

/**
 * 更新商品状态
 */
export function updateProductStatus(data: { id: string; status: string }) {
  return Alova.Post('/commerce/products/status', data);
}

/**
 * 新增商品
 */
export function createProduct(data: any) {
  return Alova.Post('/commerce/products', data);
}

/**
 * 更新商品
 */
export function updateProduct(id: string, data: any) {
  return Alova.Put(`/commerce/products/${id}`, data);
}
