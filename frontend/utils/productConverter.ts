import type { Product } from '../types/product'
import type { BackendProduct } from '../composables/useProductApi'

/**
 * 将后端商品数据转换为前端商品数据
 * 
 * 后端缺失的字段：
 * - image: 商品图片 URL
 * - rating: 商品评分
 * - reviewCount: 评论数量
 * - badge: 商品标签（热销、新品等）
 * - isFavorite: 是否收藏
 * - specs: 商品规格
 * 
 * 这些字段需要：
 * 1. 后端补充接口返回
 * 2. 或前端使用默认值/占位符
 */
export const convertBackendProduct = (backendProduct: BackendProduct): Product => {
  return {
    id: backendProduct.id,
    title: backendProduct.title,
    price: backendProduct.price,
    originalPrice: backendProduct.originalPrice,
    category: backendProduct.category,
    
    // 缺失字段使用默认值
    image: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=300&h=300&fit=crop', // 默认占位图
    rating: 4.5, // 默认评分
    reviewCount: 0, // 默认评论数
    badge: backendProduct.stock < 10 ? '库存紧张' : undefined, // 根据库存生成标签
    isFavorite: false, // 默认未收藏
    specs: [], // 默认无规格
  }
}

/**
 * 批量转换后端商品列表
 */
export const convertBackendProducts = (backendProducts: BackendProduct[]): Product[] => {
  return backendProducts.map(convertBackendProduct)
}
