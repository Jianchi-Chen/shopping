<template>
  <div class="bg-white rounded-lg shadow-sm p-6">
    <h2 class="text-2xl font-bold text-gray-900 mb-6">我的订单</h2>

    <div class="flex gap-2 mb-6 overflow-x-auto pb-2">
      <button
        v-for="status in orderStatusFilters"
        :key="status.value"
        @click="emit('select-filter', status.value)"
        :class="[
          'px-4 py-2 rounded-lg whitespace-nowrap transition-colors',
          activeFilter === status.value
            ? 'bg-orange-500 text-white'
            : 'bg-gray-100 text-gray-700 hover:bg-gray-200',
        ]"
      >
        {{ status.label }}
      </button>
    </div>

    <div v-if="loading" class="text-center py-12">
      <p class="text-gray-500">加载中...</p>
    </div>

    <div v-else-if="orders.length > 0" class="space-y-4">
      <div
        v-for="order in orders"
        :key="order.id"
        class="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow"
      >
        <div class="flex justify-between items-start mb-4">
          <div>
            <p class="font-medium text-gray-900">订单号: {{ order.orderNo }}</p>
            <p class="text-xs text-gray-500 mt-1">下单时间: {{ order.createdAt }}</p>
          </div>
          <span
            :class="[
              'px-3 py-1 text-xs font-medium rounded',
              getOrderStatusClass(order.status),
            ]"
          >
            {{ getOrderStatusText(order.status) }}
          </span>
        </div>

        <div class="mb-4">
          <p class="text-sm text-gray-600">商家: {{ order.shopName }}</p>
          <p class="text-sm text-gray-600">共 {{ order.itemCount }} 件商品</p>
        </div>

        <div class="flex justify-between items-center pt-4 border-t border-gray-200">
          <div>
            <span class="text-sm text-gray-600">订单金额: </span>
            <span class="text-lg font-semibold text-orange-600">¥{{ order.totalAmount.toFixed(2) }}</span>
          </div>
          <div class="flex gap-2">
            <button
              @click="emit('view-detail', order.id)"
              class="px-4 py-2 border border-gray-300 text-gray-700 text-sm rounded hover:bg-gray-50 transition-colors"
            >
              查看详情
            </button>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="text-center py-12">
      <p class="text-gray-500 text-lg mb-4">暂无订单</p>
      <NuxtLink to="/" class="text-orange-500 hover:underline">去购物 →</NuxtLink>
    </div>
  </div>
</template>

<script setup lang="ts">
interface OrderFilter {
  value: string | null
  label: string
}

interface OrderListItem {
  id: string
  orderNo: string
  status: string
  createdAt: string
  shopName: string
  itemCount: number
  totalAmount: number
}

defineProps<{
  orders: OrderListItem[]
  loading: boolean
  orderStatusFilters: OrderFilter[]
  activeFilter: string | null
  getOrderStatusText: (status: string) => string
  getOrderStatusClass: (status: string) => string
}>()

const emit = defineEmits<{
  (e: 'select-filter', value: string | null): void
  (e: 'view-detail', id: string): void
}>()
</script>
