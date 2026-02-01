import type { Product, Category } from '../types/product'

/**
 * 生成 mock 商品数据
 * 模拟从后端 API 获取的商品列表
 */
export const getMockProducts = (): Product[] => {
  const products: Product[] = [
    {
      id: '1',
      title: 'Apple iPhone 15 Pro Max',
      price: 9999,
      originalPrice: 11999,
      rating: 4.8,
      reviewCount: 2543,
      image: 'https://images.unsplash.com/photo-1592286927505-1def25115558?w=300&h=300&fit=crop',
      category: 'electronics',
      badge: '热销',
      isFavorite: false,
      specs: [
        {
          name: '颜色',
          options: [
            { name: '太空黑', value: 'space-black' },
            { name: '金色', value: 'gold' },
            { name: '银色', value: 'silver' },
            { name: '深紫色', value: 'deep-purple' },
          ],
        },
        {
          name: '内存',
          options: [
            { name: '256GB', value: '256' },
            { name: '512GB', value: '512' },
            { name: '1TB', value: '1000' },
          ],
        },
        {
          name: '闪存',
          options: [
            { name: '256GB', value: '256' },
            { name: '512GB', value: '512' },
            { name: '1TB', value: '1000' },
          ],
        },
      ],
    },
    {
      id: '2',
      title: 'Sony WH-1000XM5 无线耳机',
      price: 2899,
      originalPrice: 3299,
      rating: 4.7,
      reviewCount: 1823,
      image: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=300&h=300&fit=crop',
      category: 'electronics',
      badge: '新品',
      isFavorite: false,
    },
    {
      id: '3',
      title: 'MacBook Air M2',
      price: 9499,
      originalPrice: 10999,
      rating: 4.9,
      reviewCount: 3421,
      image: 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=300&h=300&fit=crop',
      category: 'electronics',
      isFavorite: false,
    },
    {
      id: '4',
      title: '高级咖啡机',
      price: 2299,
      originalPrice: 2999,
      rating: 4.6,
      reviewCount: 876,
      image: 'https://images.unsplash.com/photo-1517668808822-9ebb02ae2a0e?w=300&h=300&fit=crop',
      category: 'home',
      isFavorite: false,
    },
    {
      id: '5',
      title: '智能家居套装',
      price: 1599,
      originalPrice: 1999,
      rating: 4.5,
      reviewCount: 1204,
      image: 'https://images.unsplash.com/photo-1558089978-dbf41fbb6be2?w=300&h=300&fit=crop',
      category: 'home',
      badge: '热销',
      isFavorite: false,
    },
    {
      id: '6',
      title: '高级办公椅',
      price: 3299,
      originalPrice: 4299,
      rating: 4.7,
      reviewCount: 654,
      image: 'https://images.unsplash.com/photo-1552321554-5fefe8c9ef14?w=300&h=300&fit=crop',
      category: 'furniture',
      isFavorite: false,
    },
    {
      id: '7',
      title: '瑜伽垫套装',
      price: 199,
      rating: 4.4,
      reviewCount: 3287,
      image: 'https://images.unsplash.com/photo-1506126613408-eca07ce68773?w=300&h=300&fit=crop',
      category: 'sports',
      badge: '热销',
      isFavorite: false,
    },
    {
      id: '8',
      title: '防水蓝牙音箱',
      price: 399,
      originalPrice: 599,
      rating: 4.6,
      reviewCount: 2145,
      image: 'https://images.unsplash.com/photo-1589003077984-894e133da26d?w=300&h=300&fit=crop',
      category: 'electronics',
      isFavorite: false,
    },
    {
      id: '9',
      title: '运动智能手表',
      price: 1299,
      originalPrice: 1799,
      rating: 4.5,
      reviewCount: 1876,
      image: 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=300&h=300&fit=crop',
      category: 'electronics',
      badge: '新品',
      isFavorite: false,
    },
    {
      id: '10',
      title: '4K 网络摄像机',
      price: 2199,
      originalPrice: 2799,
      rating: 4.8,
      reviewCount: 921,
      image: 'https://images.unsplash.com/photo-1606933248051-5ce77bc75dcd?w=300&h=300&fit=crop',
      category: 'electronics',
      isFavorite: false,
    },
    {
      id: '11',
      title: '咖啡豆礼盒',
      price: 299,
      rating: 4.7,
      reviewCount: 654,
      image: 'https://images.unsplash.com/photo-1559056199-641a0ac8b8d5?w=300&h=300&fit=crop',
      category: 'food',
      isFavorite: false,
    },
    {
      id: '12',
      title: '户外登山包',
      price: 899,
      originalPrice: 1199,
      rating: 4.6,
      reviewCount: 1432,
      image: 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=300&h=300&fit=crop',
      category: 'sports',
      badge: '热销',
      isFavorite: false,
    },
  ]
  return products
}

/**
 * 获取商品分类
 */
export const getMockCategories = (): Category[] => {
  return [
    { id: 'electronics', name: '电子产品', image: 'https://images.unsplash.com/photo-1505228395891-9a51e7e86e81?w=150&h=150&fit=crop', productCount: 1243 },
    { id: 'home', name: '家居用品', image: 'https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=150&h=150&fit=crop', productCount: 567 },
    { id: 'furniture', name: '家具', image: 'https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=150&h=150&fit=crop', productCount: 234 },
    { id: 'sports', name: '运动户外', image: 'https://images.unsplash.com/photo-1461896836934-ffe607ba8211?w=150&h=150&fit=crop', productCount: 876 },
    { id: 'food', name: '食品饮料', image: 'https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=150&h=150&fit=crop', productCount: 432 },
  ]
}

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
