<template>
  <div class="min-h-screen bg-gray-100 flex flex-col">
    <!-- 顶部导航 -->
    <Header @cart-click="showCart = true" />

    <!-- 购物车面板 -->
    <CartPanel v-if="showCart" @close="showCart = false" />

    <!-- 主容器 -->
    <div class="flex-1">
      <!-- 轮播区 -->
      <BannerCarousel />

      <!-- 内容区 -->
      <div class="max-w-7xl mx-auto px-4 md:px-6 py-6">
        <div class="flex gap-6">
          <!-- 侧边栏 -->
          <FilterSidebar 
            @category-select="handleCategorySelect" 
            @price-filter="handlePriceFilter"
            @reset-filters="handleResetFilters" 
          />

          <!-- 主区域 -->
          <main class="flex-1">
            <!-- 排序栏 -->
            <div class="bg-white rounded-lg shadow-sm p-4 md:p-6 mb-6">
              <div class="flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
                <!-- 搜索框 -->
                <input
                  v-model="searchTerm"
                  type="text"
                  placeholder="搜索商品..."
                  class="w-full md:w-64 px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-orange-500"
                />

                <!-- 排序选项 -->
                <div class="flex items-center gap-3">
                  <span class="text-sm text-gray-600">排序：</span>
                  <select
                    v-model="sortBy"
                    class="px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-orange-500 text-sm"
                  >
                    <option value="relevance">相关性</option>
                    <option value="price-low">价格：低到高</option>
                    <option value="price-high">价格：高到低</option>
                    <option value="rating">评分最高</option>
                  </select>
                </div>
              </div>
            </div>

            <!-- 加载状态 -->
            <div v-if="isLoading" class="flex justify-center items-center py-12">
              <div class="text-center">
                <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-orange-500"></div>
                <p class="text-gray-600 mt-4">加载中...</p>
              </div>
            </div>

            <!-- 商品列表 -->
            <div v-else class="space-y-6">
              <!-- 商品计数 -->
              <div class="text-sm text-gray-600">
                显示 <span class="font-bold">{{ displayedProducts.length }}</span> 件商品
              </div>

              <!-- 商品网格 -->
              <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
                <ProductCard
                  v-for="product in displayedProducts"
                  :key="product.id"
                  :product="product"
                  @add-to-cart="handleAddToCart"
                  @toggle-favorite="handleToggleFavorite"
                />
              </div>

              <!-- 无结果提示 -->
              <div v-if="displayedProducts.length === 0" class="text-center py-12">
                <p class="text-gray-500 text-lg mb-4">未找到匹配的商品</p>
                <button
                  @click="handleResetFilters"
                  class="bg-orange-500 hover:bg-orange-600 text-white font-medium py-2 px-6 rounded transition-colors"
                >
                  清除过滤
                </button>
              </div>

              <!-- 分页控件 -->
              <div v-if="displayedProducts.length > 0" class="flex justify-center items-center gap-2 mt-8 pt-6 border-t border-gray-200">
                <button
                  v-if="productStore.currentPage > 1"
                  @click="handlePreviousPage"
                  class="px-3 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-100 transition-colors font-medium"
                >
                  上一页
                </button>

                <div class="flex gap-2">
                  <button
                    v-for="page in pageNumbers"
                    :key="page"
                    @click="productStore.goToPage(page)"
                    :class="[
                      'px-3 py-2 border rounded-md font-medium transition-colors',
                      page === productStore.currentPage
                        ? 'bg-orange-500 text-white border-orange-500'
                        : 'border-gray-300 text-gray-700 hover:bg-gray-100'
                    ]"
                  >
                    {{ page }}
                  </button>
                </div>

                <button
                  v-if="productStore.currentPage < productStore.totalPages"
                  @click="handleNextPage"
                  class="px-3 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-100 transition-colors font-medium"
                >
                  下一页
                </button>
              </div>
            </div>
          </main>
        </div>
      </div>
    </div>

    <!-- 页脚 -->
    <Footer />

    <!-- 提示信息 -->
    <Transition name="fade">
      <div
        v-if="showNotification"
        class="fixed bottom-6 right-6 bg-green-500 text-white px-6 py-3 rounded-lg shadow-lg z-30"
      >
        {{ notificationMessage }}
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useProductStore } from '../stores/product'
import type { Product } from '../types/product'
import { searchProducts, sortProducts } from '../utils/productFilters'

// 状态管理
const productStore = useProductStore()
const route = useRoute()
const showCart = ref(false)
const searchTerm = ref('')
const sortBy = ref<'relevance' | 'price-low' | 'price-high' | 'rating'>('relevance')
const selectedCategory = ref<string | null>(null)

// 价格过滤相关
const priceRange = ref('all')
const minPrice = ref(0)
const maxPrice = ref(Infinity)

// 通知相关
const showNotification = ref(false)
const notificationMessage = ref('')

// 计算属性
const isLoading = computed(() => productStore.isLoading)

/**
 * 显示提示信息
 */
const showMessage = (message: string) => {
  notificationMessage.value = message
  showNotification.value = true
  setTimeout(() => {
    showNotification.value = false
  }, 2000)
}

/**
 * 计算分页的页码数组
 */
const pageNumbers = computed(() => {
  const pages: number[] = []
  const currentPage = productStore.currentPage
  const totalPages = productStore.totalPages
  const maxVisible = 5 // 最多显示 5 个页码

  if (totalPages <= maxVisible) {
    // 总页数少于最大显示数，显示所有页码
    for (let i = 1; i <= totalPages; i++) {
      pages.push(i)
    }
  } else {
    // 总页数多于最大显示数
    let start = Math.max(1, currentPage - 2)
    let end = Math.min(totalPages, currentPage + 2)

    // 调整边界
    if (start === 1) {
      end = Math.min(totalPages, maxVisible)
    } else if (end === totalPages) {
      start = Math.max(1, totalPages - maxVisible + 1)
    }

    for (let i = start; i <= end; i++) {
      pages.push(i)
    }
  }

  return pages
})

/**
 * 从 URL 查询参数初始化搜索和分类
 */
const initializeFromQuery = () => {
  const search = route.query.search as string
  const category = route.query.category as string
  
  if (search) {
    searchTerm.value = search
  }
  if (category) {
    selectedCategory.value = category
  }
}

/**
 * 过滤和排序商品
 */
const displayedProducts = computed(() => {
  let result = productStore.products

  // 按分类过滤
  if (selectedCategory.value) {
    result = result.filter((p: Product) => p.category === selectedCategory.value)
  }

  // 按价格范围过滤
  result = result.filter((p: Product) => p.price >= minPrice.value && p.price <= maxPrice.value)

  // 按搜索词过滤
  if (searchTerm.value.trim()) {
    result = searchProducts(result, searchTerm.value)
  }

  // 排序
  result = sortProducts(result, sortBy.value)

  return result
})

/**
 * 添加到购物车
 */
const handleAddToCart = (product: Product) => {
  productStore.addToCart(product, 1)
  showMessage(`已添加 ${product.title} 到购物车`)
}

/**
 * 切换收藏
 */
const handleToggleFavorite = (productId: string) => {
  productStore.toggleFavorite(productId)
  const product = productStore.getProductById(productId)
  const message = product?.isFavorite ? '已添加到收藏' : '已移除收藏'
  showMessage(message)
}

/**
 * 选择分类
 */
const handleCategorySelect = (categoryId: string) => {
  selectedCategory.value = selectedCategory.value === categoryId ? null : categoryId
}

/**
 * 重置过滤条件
 */
const handleResetFilters = () => {
  selectedCategory.value = null
  searchTerm.value = ''
  sortBy.value = 'relevance'
  priceRange.value = 'all'
  minPrice.value = 0
  maxPrice.value = Infinity
}

/**
 * 处理价格过滤
 */
const handlePriceFilter = (range: string, min?: number, max?: number) => {
  priceRange.value = range
  minPrice.value = min ?? 0
  maxPrice.value = max ?? Infinity
}

/**
 * 下一页
 */
const handleNextPage = () => {
  productStore.goToPage(productStore.currentPage + 1)
  // 滚动到顶部
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

/**
 * 上一页
 */
const handlePreviousPage = () => {
  productStore.goToPage(productStore.currentPage - 1)
  // 滚动到顶部
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 页面挂载时初始化数据
onMounted(() => {
  productStore.initializeProducts()
  initializeFromQuery()
})

// 监听路由变化
watch(() => route.query, () => {
  initializeFromQuery()
})
</script>

<style scoped>
/* Transition 动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
