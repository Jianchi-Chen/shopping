// 商品规格选项
export interface SpecOption {
  name: string
  value: string
}

// 商品规格
export interface ProductSpec {
  name: string // 如 "颜色", "内存", "闪存"
  options: SpecOption[]
}

// 商品基础信息
export interface Product {
  id: string
  title: string
  price: number
  originalPrice?: number
  rating: number
  reviewCount: number
  image: string
  category: string
  badge?: string // 如 "热销", "新品" 等
  isFavorite: boolean
  description?: string
  details?: string
  stock?: number
  specs?: ProductSpec[] // 商品规格
}

// 分类信息
export interface Category {
  id: string
  name: string
  image: string
  productCount: number
}

// 购物车项目
export interface CartItem {
  product: Product
  quantity: number
}

// 列表查询参数
export interface ProductListQuery {
  category?: string
  sortBy?: 'relevance' | 'price-low' | 'price-high' | 'rating'
  searchTerm?: string
  page?: number
  pageSize?: number
}

// 用户信息
export interface User {
  id: string
  name: string
  email: string
  phone?: string
  avatar?: string
  isLoggedIn: boolean
}

// 订单项
export interface OrderItem {
  product: Product
  quantity: number
  subtotal: number
}

// 订单
export interface Order {
  id: string
  items: OrderItem[]
  total: number
  status: 'pending' | 'confirmed' | 'shipped' | 'delivered'
  createdAt: string
  shippingAddress?: string
  paymentMethod?: string
}

// 评价
export interface Review {
  id: string
  productId: string
  userId: string
  userName: string
  rating: number
  title: string
  content: string
  createdAt: string
}
