import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { User, Order, Product, OrderItem } from '../types/product'

export const useUserStore = defineStore('user', () => {
  // 状态
  const user = ref<User | null>(null)
  const orders = ref<Order[]>([])
  const isLoading = ref(false)

  const mapOrderStatus = (status: string): Order['status'] => {
    switch (status) {
      case 'PENDING_PAYMENT':
        return 'pending'
      case 'PENDING_SHIPMENT':
        return 'confirmed'
      case 'SHIPPED':
        return 'shipped'
      case 'COMPLETED':
        return 'delivered'
      default:
        return 'confirmed'
    }
  }

  const mapOrderItems = (items: any[]): OrderItem[] => {
    return items.map((item) => {
      const product: Product = {
        id: item.productId,
        title: item.title,
        price: Number(item.price),
        originalPrice: undefined,
        rating: 0,
        reviewCount: 0,
        image: item.image || 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=300&h=300&fit=crop',
        category: '',
        isFavorite: false,
      }
      return {
        product,
        quantity: item.quantity,
        subtotal: Number(item.price) * item.quantity,
      }
    })
  }

  const loadCurrentUser = async () => {
    isLoading.value = true
    try {
      const authApi = useAuth()
      const profile = await authApi.getCurrentUser()
      user.value = {
        id: profile.id,
        username: profile.username,
        name: profile.name || profile.username,
        email: profile.email || '',
        phone: profile.phone || '',
        avatar: profile.avatar || '',
        isLoggedIn: true,
      }
    } catch (error) {
      user.value = null
      throw error
    } finally {
      isLoading.value = false
    }
  }

  const loadOrders = async () => {
    try {
      const orderApi = useOrderApi()
      const response = await orderApi.getMyOrders({ page: 1, pageSize: 50 })
      orders.value = (response.list || []).map((backendOrder: any) => {
        return {
          id: backendOrder.orderNo || backendOrder.id,
          items: mapOrderItems(backendOrder.items || []),
          total: Number(backendOrder.totalAmount || 0),
          status: mapOrderStatus(backendOrder.status),
          createdAt: backendOrder.createdAt,
          shippingAddress: '',
          paymentMethod: '',
        }
      })
    } catch (error) {
      console.error('Failed to load orders:', error)
      orders.value = []
    }
  }

  // 登录（真实 API）
  const login = async (email: string, password: string) => {
    isLoading.value = true
    try {
      const authApi = useAuth()
      const username = email.split('@')[0] || email
      await authApi.login(username, password)
      await loadCurrentUser()
      await loadOrders()
    } finally {
      isLoading.value = false
    }
  }

  // 注册（真实 API）
  const register = async (name: string, email: string, password: string) => {
    isLoading.value = true
    try {
      const authApi = useAuth()
      const username = email.split('@')[0] || name
      await authApi.register(username, password, 'USER')
      await authApi.updateProfile(name)
      await loadCurrentUser()
      await loadOrders()
    } finally {
      isLoading.value = false
    }
  }

  // 登出
  const logout = () => {
    user.value = null
    const authApi = useAuth()
    authApi.logout()
    if (typeof window !== 'undefined') {
      localStorage.removeItem('user')
    }
  }

  // 更新用户信息
  const updateProfile = async (name?: string, phone?: string, avatar?: string) => {
    if (!user.value) return
    const authApi = useAuth()
    const result = await authApi.updateProfile(name, phone, avatar)
    user.value = {
      ...user.value,
      username: result.username || user.value.username,
      name: result.name || user.value.name,
      phone: result.phone || user.value.phone,
      avatar: result.avatar !== undefined ? result.avatar : user.value.avatar,
    }
  }

  // 添加订单
  const addOrder = (order: Order) => {
    orders.value.push(order)
  }

  // 获取用户订单
  const getUserOrders = () => orders.value

  // 计算属性
  const isLoggedIn = computed(() => user.value !== null)

  return {
    // 状态
    user,
    orders,
    isLoading,
    isLoggedIn,
    // 方法
    login,
    register,
    logout,
    updateProfile,
    addOrder,
    getUserOrders,
    loadCurrentUser,
    loadOrders,
  }
})
