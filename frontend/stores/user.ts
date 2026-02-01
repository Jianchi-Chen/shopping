import { defineStore } from 'pinia'
import { ref, computed, watch } from 'vue'
import type { User, Order } from '../types/product'
import { generateId } from '../utils/helpers'

export const useUserStore = defineStore('user', () => {
  // 状态
  const user = ref<User | null>(null)
  const orders = ref<Order[]>([])
  const isLoading = ref(false)

  // 监听用户状态变化，自动保存到 localStorage
  watch(
    [user, orders],
    () => {
      saveToLocalStorage()
    },
    { deep: true }
  )

  // localStorage 相关
  const saveToLocalStorage = () => {
    if (typeof window !== 'undefined') {
      if (user.value) {
        localStorage.setItem('user', JSON.stringify(user.value))
      }
      localStorage.setItem('orders', JSON.stringify(orders.value))
    }
  }

  const loadFromLocalStorage = () => {
    if (typeof window !== 'undefined') {
      const userData = localStorage.getItem('user')
      if (userData) {
        try {
          user.value = JSON.parse(userData)
        } catch (e) {
          console.error('Failed to load user from localStorage', e)
        }
      }

      const ordersData = localStorage.getItem('orders')
      if (ordersData) {
        try {
          orders.value = JSON.parse(ordersData)
        } catch (e) {
          console.error('Failed to load orders from localStorage', e)
        }
      }
    }
  }

  // 登录（模拟）
  const login = async (email: string, password: string) => {
    isLoading.value = true
    try {
      // 模拟 API 延迟
      await new Promise(resolve => setTimeout(resolve, 500))
      
      const nameFromEmail = email.split('@')[0] || 'user'
      user.value = {
        id: generateId(),
        name: nameFromEmail,
        email,
        phone: '13800138000',
        avatar: `https://ui-avatars.com/api/?name=${nameFromEmail}&background=random`,
        isLoggedIn: true,
      }
    } finally {
      isLoading.value = false
    }
  }

  // 注册（模拟）
  const register = async (name: string, email: string, password: string) => {
    isLoading.value = true
    try {
      // 模拟 API 延迟
      await new Promise(resolve => setTimeout(resolve, 500))
      
      user.value = {
        id: generateId(),
        name,
        email,
        phone: '',
        avatar: `https://ui-avatars.com/api/?name=${name}&background=random`,
        isLoggedIn: true,
      }
    } finally {
      isLoading.value = false
    }
  }

  // 登出
  const logout = () => {
    user.value = null
    if (typeof window !== 'undefined') {
      localStorage.removeItem('user')
    }
  }

  // 更新用户信息
  const updateProfile = (name: string, phone: string) => {
    if (user.value) {
      user.value.name = name
      user.value.phone = phone
    }
  }

  // 添加订单
  const addOrder = (order: Order) => {
    orders.value.push(order)
  }

  // 获取用户订单
  const getUserOrders = () => {
    return orders.value
  }

  // 计算属性
  const isLoggedIn = computed(() => user.value?.isLoggedIn ?? false)

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
    loadFromLocalStorage,
    saveToLocalStorage,
  }
})
