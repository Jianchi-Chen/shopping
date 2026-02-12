<template>
  <div class="min-h-screen bg-gray-100 flex flex-col">
    <Header @cart-click="showCart = true" />
    <CartPanel v-if="showCart" @close="showCart = false" />

    <!-- 面包屑导航 -->
    <div class="bg-white border-b border-gray-200 px-4 md:px-6 py-3">
      <div class="max-w-7xl mx-auto text-sm text-gray-600">
        <NuxtLink to="/" class="hover:text-orange-500">首页</NuxtLink>
        <span class="mx-2">/</span>
        <NuxtLink to="/user/profile" class="hover:text-orange-500">我的账户</NuxtLink>
        <span class="mx-2">/</span>
        <span class="text-gray-900">订单历史</span>
      </div>
    </div>

    <!-- 主容器 -->
    <div class="flex-1 flex">
      <!-- 侧边栏菜单 -->
      <div class="w-full md:w-48 bg-white border-r border-gray-200">
        <nav class="divide-y divide-gray-200">
          <NuxtLink
            to="/user/profile"
            class="block w-full text-left px-4 py-3 text-gray-700 hover:bg-gray-50 transition-colors"
          >
            个人资料
          </NuxtLink>
          <NuxtLink
            to="/user/profile"
            class="block w-full text-left px-4 py-3 text-gray-700 hover:bg-gray-50 transition-colors"
          >
            收货地址
          </NuxtLink>
          <NuxtLink
            to="/user/orders"
            class="block w-full text-left px-4 py-3 bg-orange-50 border-l-4 border-orange-500 text-orange-600 font-medium"
          >
            订单历史
          </NuxtLink>
        </nav>
      </div>

      <!-- 内容区域 -->
      <div class="flex-1 p-4 md:p-6">
        <div class="max-w-4xl">
          <h2 class="text-2xl font-bold text-gray-900 mb-6">我的订单</h2>

          <!-- 订单过滤器 -->
          <div class="flex flex-wrap gap-2 mb-6">
            <button
              v-for="status in statusFilters"
              :key="status.value"
              @click="selectedStatus = status.value"
              :class="[
                'px-4 py-2 rounded-lg font-medium transition-colors',
                selectedStatus === status.value
                  ? 'bg-orange-500 text-white'
                  : 'bg-white text-gray-700 border border-gray-200 hover:border-orange-500',
              ]"
            >
              {{ status.label }}
            </button>
          </div>

          <!-- 订单列表 -->
          <div v-if="filteredOrders.length > 0" class="space-y-4">
            <div
              v-for="order in filteredOrders"
              :key="order.id"
              class="bg-white rounded-lg shadow-sm p-6 border-l-4"
              :class="getStatusColor(order.status)"
            >
              <!-- 订单头部 -->
              <div class="flex flex-col md:flex-row md:items-center md:justify-between mb-4 pb-4 border-b border-gray-200">
                <div>
                  <p class="text-sm text-gray-500">订单号</p>
                  <p class="font-bold text-gray-900">{{ order.id }}</p>
                </div>
                <div class="mt-2 md:mt-0">
                  <p class="text-sm text-gray-500">订单日期</p>
                  <p class="font-medium text-gray-900">{{ formatDate(order.createdAt) }}</p>
                </div>
                <div class="mt-2 md:mt-0">
                  <span :class="getStatusBadge(order.status)" class="inline-block px-3 py-1 rounded-full text-sm font-medium">
                    {{ getStatusText(order.status) }}
                  </span>
                </div>
              </div>

              <!-- 订单商品 -->
              <div class="mb-4">
                <p class="text-sm font-semibold text-gray-700 mb-3">订单商品</p>
                <div class="space-y-2">
                  <div v-for="item in order.items" :key="item.product.id" class="flex justify-between text-sm p-2 bg-gray-50 rounded">
                    <div>
                      <p class="text-gray-900">{{ item.product.title }}</p>
                      <p class="text-gray-500">数量: {{ item.quantity }}</p>
                    </div>
                    <p class="font-medium text-gray-900">¥{{ item.subtotal.toFixed(2) }}</p>
                  </div>
                </div>
              </div>

              <!-- 订单金额 -->
              <div class="flex justify-between items-center pt-4 border-t border-gray-200">
                <div class="text-right">
                  <p class="text-sm text-gray-500 mb-1">订单总额</p>
                  <p class="text-2xl font-bold text-orange-600">¥{{ order.total.toFixed(2) }}</p>
                </div>

                <!-- 操作按钮 -->
                <div class="flex gap-2">
                  <NuxtLink
                    :to="`/product/${order.items[0]?.product.id}`"
                    class="px-4 py-2 bg-white border border-gray-300 text-gray-700 font-medium rounded-lg hover:bg-gray-50 transition-colors"
                  >
                    查看商品
                  </NuxtLink>
                  <button
                    @click="repeatOrder(order)"
                    class="px-4 py-2 bg-orange-500 hover:bg-orange-600 text-white font-medium rounded-lg transition-colors"
                  >
                    再次购买
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- 空状态 -->
          <div v-else class="bg-white rounded-lg shadow-sm p-12 text-center">
            <svg class="w-16 h-16 text-gray-300 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z"></path>
            </svg>
            <p class="text-gray-500 text-lg mb-4">暂无订单</p>
            <NuxtLink to="/" class="text-orange-500 hover:underline font-medium">开始购物 →</NuxtLink>
          </div>
        </div>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useProductStore } from '../../stores/product'
import { useUserStore } from '../../stores/user'
import type { Order } from '../../types/product'

const router = useRouter()
const productStore = useProductStore()
const userStore = useUserStore()

const showCart = ref(false)
const selectedStatus = ref('all')

const statusFilters = [
  { value: 'all', label: '全部订单' },
  { value: 'pending', label: '待支付' },
  { value: 'confirmed', label: '待发货' },
  { value: 'shipped', label: '已发货' },
  { value: 'delivered', label: '已收货' },
]

const filteredOrders = computed(() => {
  const orders = userStore.orders
  if (selectedStatus.value === 'all') {
    return orders
  }
  return orders.filter(order => order.status === selectedStatus.value)
})

onMounted(() => {
  router.replace('/account/orders')
})

const getStatusText = (status: string): string => {
  const statusMap: Record<string, string> = {
    pending: '待支付',
    confirmed: '待发货',
    shipped: '已发货',
    delivered: '已收货',
    cancelled: '已取消',
  }
  return statusMap[status] || status
}

const getStatusColor = (status: string): string => {
  const colorMap: Record<string, string> = {
    pending: 'border-orange-500',
    confirmed: 'border-blue-500',
    shipped: 'border-purple-500',
    delivered: 'border-green-500',
    cancelled: 'border-gray-400',
  }
  return colorMap[status] || 'border-gray-300'
}

const getStatusBadge = (status: string): string => {
  const badgeMap: Record<string, string> = {
    pending: 'bg-orange-100 text-orange-700',
    confirmed: 'bg-blue-100 text-blue-700',
    shipped: 'bg-purple-100 text-purple-700',
    delivered: 'bg-green-100 text-green-700',
    cancelled: 'bg-gray-100 text-gray-700',
  }
  return badgeMap[status] || 'bg-gray-100 text-gray-700'
}

const formatDate = (date: string): string => {
  return new Date(date).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  })
}

const repeatOrder = (order: Order) => {
  // 将订单商品再次加入购物车
  order.items.forEach(item => {
    productStore.addToCart(item.product, item.quantity)
  })
  // 跳转到购物车
  router.push('/checkout/cart')
}
</script>
