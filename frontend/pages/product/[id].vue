<template>
  <div class="min-h-screen bg-gray-100 flex flex-col">
    <Header @cart-click="showCart = true" />
    <CartPanel v-if="showCart" @close="showCart = false" />

    <!-- 面包屑导航 -->
    <div class="bg-white py-3 px-4 md:px-6 border-b border-gray-200">
      <div class="max-w-7xl mx-auto">
        <NuxtLink to="/" class="text-orange-500 hover:underline text-sm">
          首页
        </NuxtLink>
        <span class="text-gray-400 mx-2">/</span>
        <span class="text-gray-600 text-sm">商品详情</span>
      </div>
    </div>

    <!-- 主容器 -->
    <div class="flex-1 max-w-7xl mx-auto w-full px-4 md:px-6 py-6">
      <div v-if="product" class="grid grid-cols-1 md:grid-cols-2 gap-8 bg-white p-6 rounded-lg shadow-sm">
        <!-- 左侧图片 -->
        <div class="flex items-center justify-center bg-gray-50 rounded-lg p-4">
          <img :src="product.image" :alt="product.title" class="max-w-full max-h-96 object-cover" />
        </div>

        <!-- 右侧信息 -->
        <div class="space-y-6">
          <!-- 标题和评分 -->
          <div>
            <h1 class="text-2xl md:text-3xl font-bold text-gray-900 mb-3">{{ product.title }}</h1>
            <div class="flex items-center gap-4">
              <div class="flex items-center">
                <span v-for="i in 5" :key="i" class="text-lg">
                  <span v-if="i <= Math.floor(product.rating)" class="text-yellow-400">★</span>
                  <span v-else class="text-gray-300">★</span>
                </span>
              </div>
              <span class="text-gray-600 text-sm">({{ product.reviewCount }} 条评价)</span>
            </div>
          </div>

          <!-- 价格 -->
          <div class="border-t border-b border-gray-200 py-4">
            <div class="flex items-baseline gap-3 mb-3">
              <span class="text-3xl font-bold text-orange-600">¥{{ product.price }}</span>
              <span v-if="product.originalPrice" class="text-lg text-gray-400 line-through">
                ¥{{ product.originalPrice }}
              </span>
              <span v-if="product.originalPrice" class="text-sm bg-orange-100 text-orange-600 px-2 py-1 rounded">
                省 ¥{{ product.originalPrice - product.price }}
              </span>
            </div>
            <p class="text-sm text-gray-600">库存: {{ product.stock ?? 100 }} 件</p>
          </div>

          <!-- 描述 -->
          <div v-if="product.description" class="text-gray-700">
            <h3 class="font-semibold text-gray-900 mb-2">商品描述</h3>
            <p>{{ product.description }}</p>
          </div>

          <!-- 购买选项 -->
          <div class="space-y-3">
            <div class="flex items-center gap-4">
              <span class="text-gray-700 font-medium">购买数量:</span>
              <div class="flex items-center border border-gray-300 rounded-lg w-fit">
                <button @click="quantity > 1 && quantity--" class="px-3 py-2 text-gray-600 hover:bg-gray-100">
                  −
                </button>
                <input v-model.number="quantity" type="number" min="1" class="w-12 text-center border-0 focus:outline-none" />
                <button @click="quantity++" class="px-3 py-2 text-gray-600 hover:bg-gray-100">
                  +
                </button>
              </div>
            </div>

            <!-- 动作按钮 -->
            <div class="flex gap-3 pt-4">
              <button
                @click="handleAddToCart"
                class="flex-1 bg-orange-500 hover:bg-orange-600 text-white font-bold py-3 px-6 rounded-lg transition-colors"
              >
                加入购物车
              </button>
              <button
                @click="handleToggleFavorite"
                class="px-6 py-3 border-2 rounded-lg transition-colors"
                :class="product.isFavorite 
                  ? 'border-red-500 text-red-500 bg-red-50' 
                  : 'border-gray-300 text-gray-600 hover:border-red-500 hover:text-red-500'"
              >
                {{ product.isFavorite ? '♥ 已收藏' : '♡ 收藏' }}
              </button>
            </div>
          </div>

          <!-- 配送信息 -->
          <div class="bg-gray-50 p-4 rounded-lg space-y-2 text-sm">
            <p class="text-gray-700">✓ 免运费（部分商品）</p>
            <p class="text-gray-700">✓ 7天无理由退货</p>
            <p class="text-gray-700">✓ 假一赔十</p>
          </div>
        </div>
      </div>

      <!-- 详情选项卡 -->
      <div v-if="product" class="mt-8 bg-white rounded-lg shadow-sm overflow-hidden">
        <div class="border-b border-gray-200 flex">
          <button
            v-for="tab in ['details', 'reviews']"
            :key="tab"
            @click="activeTab = tab as 'details' | 'reviews'"
            class="px-6 py-4 font-medium border-b-2 transition-colors"
            :class="activeTab === tab
              ? 'text-orange-600 border-orange-600'
              : 'text-gray-600 border-transparent hover:text-gray-900'"
          >
            {{ tab === 'details' ? '商品详情' : '用户评价' }}
          </button>
        </div>

        <div class="p-6">
          <!-- 详情标签页 -->
          <div v-if="activeTab === 'details'" class="prose prose-sm max-w-none">
            <div v-if="product.details" class="text-gray-700 whitespace-pre-wrap">
              {{ product.details }}
            </div>
            <div v-else class="text-gray-500">
              暂无详细信息
            </div>
          </div>

          <!-- 评价标签页 -->
          <div v-if="activeTab === 'reviews'" class="space-y-4">
            <div class="text-center py-8 text-gray-500">
              暂无评价，购买后可评价
            </div>
          </div>
        </div>
      </div>

      <!-- 加载或错误状态 -->
      <div v-else class="text-center py-12">
        <p class="text-gray-500">商品不存在或已下架</p>
        <NuxtLink to="/" class="text-orange-500 hover:underline mt-4 inline-block">
          返回首页
        </NuxtLink>
      </div>
    </div>

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
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useProductStore } from '../../stores/product'

const route = useRoute()
const productStore = useProductStore()
const showCart = ref(false)
const quantity = ref(1)
const activeTab = ref<'details' | 'reviews'>('details')
const showNotification = ref(false)
const notificationMessage = ref('')

// 获取当前商品
const product = computed(() => {
  return productStore.getProductById(route.params.id as string)
})

// 初始化产品数据（如未加载）
if (productStore.products.length === 0) {
  productStore.initializeProducts()
}

const showMessage = (message: string) => {
  notificationMessage.value = message
  showNotification.value = true
  setTimeout(() => {
    showNotification.value = false
  }, 2000)
}

const handleAddToCart = () => {
  if (product.value) {
    productStore.addToCart(product.value, quantity.value)
    showMessage(`已添加 ${quantity.value} 件 ${product.value.title} 到购物车`)
    quantity.value = 1
  }
}

const handleToggleFavorite = () => {
  if (product.value) {
    productStore.toggleFavorite(product.value.id)
    const message = product.value.isFavorite ? '已添加到收藏' : '已移除收藏'
    showMessage(message)
  }
}
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
