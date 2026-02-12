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
    shopName: backendProduct.shopName,
    status: backendProduct.status,
    stock: backendProduct.stock,
    
    // 使用后端返回的真实数据
    image: backendProduct.images?.split(',')[0] || 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=300&h=300&fit=crop',
    rating: backendProduct.rating || 0, // 使用后端的真实评分
    reviewCount: backendProduct.reviewCount || 0, // 使用后端的真实评论数
    badge: backendProduct.stock < 10 ? '库存紧张' : undefined,
    isFavorite: false,
    specs: [],
  }
}

/**
 * 批量转换后端商品列表
 */
export const convertBackendProducts = (backendProducts: BackendProduct[]): Product[] => {
  return backendProducts.map(convertBackendProduct)
}
