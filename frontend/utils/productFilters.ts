import type { Product } from '../types/product'

/**
 * 搜索商品 - 简单的客户端搜索实现
 */
export const searchProducts = (products: Product[], searchTerm: string): Product[] => {
  if (!searchTerm.trim()) return products
  
  const term = searchTerm.toLowerCase()
  return products.filter(product =>
    product.title.toLowerCase().includes(term) ||
    product.category.toLowerCase().includes(term)
  )
}

/**
 * 排序商品
 */
export const sortProducts = (
  products: Product[],
  sortBy: 'relevance' | 'price-low' | 'price-high' | 'rating' = 'relevance'
): Product[] => {
  const sorted = [...products]
  
  switch (sortBy) {
    case 'price-low':
      sorted.sort((a, b) => a.price - b.price)
      break
    case 'price-high':
      sorted.sort((a, b) => b.price - a.price)
      break
    case 'rating':
      sorted.sort((a, b) => b.rating - a.rating)
      break
    case 'relevance':
    default:
      break
  }
  
  return sorted
}
