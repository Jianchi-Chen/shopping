<template>
  <div class="min-h-screen bg-gray-100">
    <Header @cart-click="showCart = true" />
    <CartPanel v-if="showCart" @close="showCart = false" />

    <!-- 页面标题 -->
    <div class="bg-white border-b border-gray-200">
      <div class="max-w-7xl mx-auto px-4 md:px-6 py-6">
        <h1 class="text-2xl md:text-3xl font-bold text-gray-900">我的订单</h1>
        <p class="text-gray-500 mt-2">查看和管理您的订单</p>
      </div>
    </div>

    <!-- 订单筛选 -->
    <div class="bg-white border-b border-gray-200 sticky top-0 z-10">
      <div class="max-w-7xl mx-auto px-4 md:px-6 py-4">
        <div class="flex flex-wrap gap-2">
          <button
            v-for="filter in filters"
            :key="filter.value"
            @click="selectedFilter = filter.value"
            :class="{
              'bg-orange-500 text-white': selectedFilter === filter.value,
              'bg-gray-100 text-gray-700 hover:bg-gray-200': selectedFilter !== filter.value,
            }"
            class="px-4 py-2 rounded-lg font-medium transition-colors"
          >
            {{ filter.label }}
          </button>
        </div>
      </div>
    </div>

    <!-- 订单列表 -->
    <div class="max-w-7xl mx-auto px-4 md:px-6 py-8">
      <div v-if="loading" class="text-center py-12">
        <div class="inline-block">
          <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-orange-500"></div>
        </div>
      </div>

      <div v-else-if="filteredOrders.length === 0" class="text-center py-12">
        <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z"></path>
        </svg>
        <p class="mt-4 text-gray-500">还没有订单</p>
        <NuxtLink to="/" class="mt-4 inline-block text-orange-500 hover:text-orange-600 font-medium">
          继续购物
        </NuxtLink>
      </div>

      <div v-else class="space-y-4">
        <div v-for="order in filteredOrders" :key="order.id" class="bg-white rounded-lg shadow-sm overflow-hidden">
          <!-- 订单头部 -->
          <div class="border-b border-gray-200 p-4 md:p-6 bg-gray-50">
            <div class="flex flex-col md:flex-row md:justify-between md:items-center gap-4">
              <div>
                <p class="text-sm text-gray-500">订单号</p>
                <p class="font-bold text-gray-900">{{ order.orderNo }}</p>
              </div>
              <div class="flex flex-col sm:flex-row gap-4 md:gap-8">
                <div>
                  <p class="text-sm text-gray-500">订单状态</p>
                  <p class="font-medium">
                    <span :class="getStatusColor(order.status)">{{ getStatusLabel(order.status) }}</span>
                  </p>
                </div>
                <div>
                  <p class="text-sm text-gray-500">订单时间</p>
                  <p class="font-medium text-gray-900">{{ formatDate(order.createdAt) }}</p>
                </div>
              </div>
            </div>
          </div>

          <!-- 订单商品 -->
          <div class="divide-y divide-gray-200">
            <div
              v-for="item in order.items"
              :key="item.productId"
              class="p-4 md:p-6 hover:bg-gray-50 transition-colors"
            >
              <div class="grid grid-cols-1 md:grid-cols-12 gap-4">
                <!-- 商品图片 -->
                <div class="md:col-span-2">
                  <img
                    v-if="item.image"
                    :src="item.image"
                    :alt="item.title"
                    class="w-full h-32 object-cover rounded-lg"
                    @error="(e) => ((e.target as HTMLImageElement).src = 'data:image/svg+xml,%3Csvg xmlns=%27http://www.w3.org/2000/svg%27 width=%27200%27 height=%27200%27%3E%3Crect fill=%27%23e0e0e0%27 width=%27200%27 height=%27200%27/%3E%3Ctext x=%2750%25%27 y=%2750%25%27 font-family=%27Arial%27 font-size=%2716%27 fill=%27%23999%27 text-anchor=%27middle%27 dy=%27.3em%27%3ENo Image%3C/text%3E%3C/svg%3E')"
                  />
                  <div v-else class="w-full h-32 bg-gray-200 rounded-lg flex items-center justify-center">
                    <span class="text-gray-400 text-sm">无图片</span>
                  </div>
                </div>

                <!-- 商品信息 -->
                <div class="md:col-span-5">
                  <h3 class="font-semibold text-gray-900 line-clamp-2">{{ item.title }}</h3>
                  <div class="mt-2 text-sm text-gray-500">
                    <p>数量: {{ item.quantity }}</p>
                    <p class="mt-1">单价: ¥{{ item.price }}</p>
                  </div>
                </div>

                <!-- 小计价格 -->
                <div class="md:col-span-2 text-right md:text-center">
                  <p class="text-2xl font-bold text-orange-600">¥{{ (item.price * item.quantity).toFixed(2) }}</p>
                </div>

                <!-- 操作按钮 -->
                <div class="md:col-span-3 flex flex-col gap-2">
                  <button
                    @click="openCommentModal(order, item)"
                    class="w-full px-4 py-2 bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-lg font-medium transition-colors"
                  >
                    評論
                  </button>
                  <NuxtLink
                    :to="`/product/${item.productId}`"
                    class="w-full px-4 py-2 bg-orange-500 hover:bg-orange-600 text-white rounded-lg font-medium text-center transition-colors"
                  >
                    查看商品
                  </NuxtLink>
                </div>
              </div>
            </div>
          </div>

          <!-- 物流信息 -->
          <div class="border-t border-gray-200 p-4 md:p-6 bg-gray-50">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <p class="text-sm text-gray-500 mb-2">收货地址</p>
                <p class="font-medium text-gray-900">{{ order.receiverName }}</p>
                <p class="text-gray-600 text-sm">{{ order.receiverPhone }}</p>
                <p class="text-gray-600 text-sm mt-1">
                  {{ order.province }}{{ order.city }}{{ order.district }}{{ order.addressDetail }}
                </p>
              </div>
              <div>
                <p class="text-sm text-gray-500 mb-2">订单总额</p>
                <p class="text-3xl font-bold text-orange-600">¥{{ order.totalAmount }}</p>
              </div>
            </div>
          </div>

          <!-- 订单备注 -->
          <div v-if="order.remark" class="border-t border-gray-200 p-4 md:p-6">
            <p class="text-sm text-gray-500 mb-2">订单备注</p>
            <p class="text-gray-700">{{ order.remark }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 評論彈窗 -->
    <div
      v-if="commentModal.show"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4"
      @click.self="commentModal.show = false"
    >
      <div class="bg-white rounded-lg max-w-md w-full shadow-xl">
        <div class="border-b border-gray-200 p-4 md:p-6">
          <h2 class="text-lg font-bold text-gray-900">評論商品</h2>
        </div>

        <div class="p-4 md:p-6 space-y-4">
          <!-- 商品信息 -->
          <div class="flex items-center gap-3">
            <img
              v-if="commentModal.item?.image"
              :src="commentModal.item?.image"
              :alt="commentModal.item?.title"
              class="w-12 h-12 object-cover rounded"
              @error="(e) => ((e.target as HTMLImageElement).style.display = 'none')"
            />
            <div class="flex-1 min-w-0">
              <p class="text-sm font-medium text-gray-900 truncate">{{ commentModal.item?.title }}</p>
              <p class="text-xs text-gray-500">{{ commentModal.order?.orderNo }}</p>
            </div>
          </div>

          <!-- 評分 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">評分</label>
            <div class="flex gap-2">
              <button
                v-for="score in [1, 2, 3, 4, 5]"
                :key="score"
                @click="commentModal.rating = score"
                class="text-3xl transition-transform hover:scale-110"
                :class="score <= commentModal.rating ? 'text-yellow-400' : 'text-gray-300'"
              >
                ★
              </button>
            </div>
          </div>

          <!-- 評論內容 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">評論內容</label>
            <textarea
              v-model="commentModal.content"
              placeholder="分享您的購物體驗（可選）"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500"
              rows="3"
            ></textarea>
          </div>

          <!-- 按钮 -->
          <div class="flex gap-2 pt-4">
            <button
              @click="commentModal.show = false"
              class="flex-1 px-4 py-2 bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-lg font-medium transition-colors"
            >
              取消
            </button>
            <button
              @click="submitComment"
              :disabled="commentModal.rating === 0 || submitting"
              :class="{ 'opacity-50 cursor-not-allowed': commentModal.rating === 0 || submitting }"
              class="flex-1 px-4 py-2 bg-orange-500 hover:bg-orange-600 text-white rounded-lg font-medium transition-colors disabled:hover:bg-orange-500"
            >
              {{ submitting ? '提交中...' : '提交評論' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <Footer />

    <Transition name="fade">
      <div
        v-if="showNotification"
        class="fixed top-6 left-1/2 -translate-x-1/2 bg-green-500 text-white px-6 py-3 rounded-lg shadow-lg z-50"
      >
        {{ notificationMessage }}
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { useOrderApi } from '../../composables/useOrderApi'
import { useApi } from '../../composables/useApi'

const router = useRouter()
const userStore = useUserStore()
const orderApi = useOrderApi()
const { request } = useApi()

const loading = ref(true)
const orders = ref<any[]>([])
const selectedFilter = ref('all')
const submitting = ref(false)
const showNotification = ref(false)
const notificationMessage = ref('')

const showCart = ref(false)

const filters = [
  { label: '全部订单', value: 'all' },
  { label: '待付款', value: 'PENDING_PAYMENT' },
  { label: '待发货', value: 'PENDING_SHIPMENT' },
  { label: '已发货', value: 'SHIPPED' },
  { label: '已完成', value: 'COMPLETED' },
]

const commentModal = ref({
  show: false,
  order: null as any,
  item: null as any,
  rating: 0,
  content: '',
})

const filteredOrders = computed(() => {
  if (selectedFilter.value === 'all') {
    return orders.value
  }
  return orders.value.filter((order) => order.status === selectedFilter.value)
})

const getStatusLabel = (status: string) => {
  const labels: Record<string, string> = {
    PENDING_PAYMENT: '待付款',
    PENDING_SHIPMENT: '待发货',
    SHIPPED: '已发货',
    COMPLETED: '已完成',
    CLOSED: '已关闭',
    AFTER_SALE: '售后中',
  }
  return labels[status] || status
}

const getStatusColor = (status: string) => {
  const colors: Record<string, string> = {
    PENDING_PAYMENT: 'text-red-600',
    PENDING_SHIPMENT: 'text-blue-600',
    SHIPPED: 'text-purple-600',
    COMPLETED: 'text-green-600',
    CLOSED: 'text-gray-600',
    AFTER_SALE: 'text-orange-600',
  }
  return colors[status] || 'text-gray-600'
}

const formatDate = (date: string | Date) => {
  const d = typeof date === 'string' ? new Date(date) : date
  return d.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}

const openCommentModal = (order: any, item: any) => {
  commentModal.value = {
    show: true,
    order,
    item,
    rating: 0,
    content: '',
  }
}

const notify = (message: string) => {
  notificationMessage.value = message
  showNotification.value = true
  setTimeout(() => {
    showNotification.value = false
  }, 2000)
}

const submitComment = async () => {
  if (commentModal.value.rating === 0) return

  try {
    submitting.value = true

    // 调用API提交評論
    await request('/commerce/reviews', {
      method: 'POST',
      body: JSON.stringify({
        productId: commentModal.value.item?.productId,
        orderId: commentModal.value.order?.id,
        rating: commentModal.value.rating,
        content: commentModal.value.content,
      }),
    })

    // 提交成功
    commentModal.value.show = false
    const message: any = (window as any).$message
    message?.success('評論提交成功')
    notify('評論提交成功')
  } catch (error) {
    console.error('Error submitting comment:', error)
    const message: any = (window as any).$message
    message?.error('提交失敗，請稍後重試')
    notify('提交失敗，請稍後重試')
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  if (!userStore.isLoggedIn) {
    router.push('/auth/login')
    return
  }

  try {
    const response = await orderApi.getMyOrders({ page: 1, pageSize: 100 })
    orders.value = response.list || []
  } catch (error) {
    console.error('加载订单失败:', error)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
