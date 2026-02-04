import { defineStore } from 'pinia'
import { ref, computed, watch } from 'vue'
import type { Product, CartItem } from '../types/product'
import { convertBackendProducts } from '../utils/productConverter'

export const useProductStore = defineStore('product', () => {
  // 状态
  const products = ref<Product[]>([])
  const cart = ref<CartItem[]>([])
  const favorites = ref<Set<string>>(new Set())
  const isLoading = ref(false)
  const displayedPage = ref(1)
  const itemsPerPage = 12

  // 初始化商品数据
  const initializeProducts = async () => {
    isLoading.value = true
    try {
      // 使用真实 API
      const productApi = useProductApi()
      const response = await productApi.getProducts({ 
        page: 1, 
        pageSize: 100, // 获取前100个商品
        status: 'ON_SALE' // 只获取在售商品
      })
      products.value = convertBackendProducts(response.list)
      
      // 从 localStorage 恢复收藏和购物车
      loadFromLocalStorage()
    } catch (error) {
      console.error('Failed to load products:', error)
      products.value = []
      loadFromLocalStorage()
    } finally {
      isLoading.value = false
    }
  }

  // localStorage 存储和恢复
  const saveToLocalStorage = () => {
    if (typeof window !== 'undefined') {
      // 保存购物车
      const cartData = cart.value.map(item => ({
        productId: item.product.id,
        quantity: item.quantity,
      }))
      localStorage.setItem('cart', JSON.stringify(cartData))

      // 保存收藏
      const favoritesArray = Array.from(favorites.value)
      localStorage.setItem('favorites', JSON.stringify(favoritesArray))
    }
  }

  const loadFromLocalStorage = () => {
    if (typeof window !== 'undefined') {
      // 恢复购物车
      const cartData = localStorage.getItem('cart')
      if (cartData) {
        try {
          const parsed = JSON.parse(cartData)
          cart.value = parsed.map((item: { productId: string; quantity: number }) => {
            const product = products.value.find(p => p.id === item.productId)
            return product ? { product, quantity: item.quantity } : null
          }).filter(Boolean) as CartItem[]
        } catch (e) {
          console.error('Failed to load cart from localStorage', e)
        }
      }

      // 恢复收藏
      const favoritesData = localStorage.getItem('favorites')
      if (favoritesData) {
        try {
          const parsed = JSON.parse(favoritesData)
          favorites.value = new Set(parsed)
          // 同步更新商品的 isFavorite 状态
          products.value.forEach(product => {
            product.isFavorite = favorites.value.has(product.id)
          })
        } catch (e) {
          console.error('Failed to load favorites from localStorage', e)
        }
      }
    }
  }

  // 监听购物车和收藏的变化，自动保存到 localStorage
  watch([cart, favorites], () => {
    saveToLocalStorage()
  }, { deep: true })

  // 根据 ID 获取单个商品
  const getProductById = (id: string) => {
    return products.value.find(p => p.id === id)
  }

  // 添加到购物车
  const addToCart = (product: Product, quantity: number = 1) => {
    const existingItem = cart.value.find(item => item.product.id === product.id)
    if (existingItem) {
      existingItem.quantity += quantity
    } else {
      cart.value.push({ product, quantity })
    }
  }

  // 从购物车移除
  const removeFromCart = (productId: string) => {
    const index = cart.value.findIndex(item => item.product.id === productId)
    if (index > -1) {
      cart.value.splice(index, 1)
    }
  }

  // 更新购物车数量
  const updateCartQuantity = (productId: string, quantity: number) => {
    const item = cart.value.find(item => item.product.id === productId)
    if (item) {
      if (quantity <= 0) {
        removeFromCart(productId)
      } else {
        item.quantity = quantity
      }
    }
  }

  // 切换收藏
  const toggleFavorite = (productId: string) => {
    if (favorites.value.has(productId)) {
      favorites.value.delete(productId)
    } else {
      favorites.value.add(productId)
    }
    // 同时更新商品的 isFavorite 状态
    const product = getProductById(productId)
    if (product) {
      product.isFavorite = !product.isFavorite
    }
  }

  // 清空购物车
  const clearCart = () => {
    cart.value = []
  }

  // 计算购物车总价
  const cartTotal = computed(() => {
    return cart.value.reduce((sum, item) => sum + item.product.price * item.quantity, 0)
  })

  // 计算购物车项数
  const cartCount = computed(() => {
    return cart.value.reduce((sum, item) => sum + item.quantity, 0)
  })

  // 分页相关
  const totalPages = computed(() => Math.ceil(products.value.length / itemsPerPage))

  const paginatedProducts = computed(() => {
    const start = (displayedPage.value - 1) * itemsPerPage
    const end = start + itemsPerPage
    return products.value.slice(start, end)
  })

  const goToPage = (page: number) => {
    displayedPage.value = Math.max(1, Math.min(page, totalPages.value))
  }

  const nextPage = () => {
    goToPage(displayedPage.value + 1)
  }

  const prevPage = () => {
    goToPage(displayedPage.value - 1)
  }

  // 公开 currentPage 用于模板
  const currentPage = computed(() => displayedPage.value)

  return {
    // 状态
    products,
    cart,
    favorites,
    isLoading,
    displayedPage,
    itemsPerPage,
    paginatedProducts,
    totalPages,
    currentPage,
    // 计算属性
    cartTotal,
    cartCount,
    // 方法
    initializeProducts,
    getProductById,
    addToCart,
    removeFromCart,
    updateCartQuantity,
    toggleFavorite,
    clearCart,
    saveToLocalStorage,
    loadFromLocalStorage,
    goToPage,
    nextPage,
    prevPage,
  }
})

