<template>
  <div class="min-h-screen bg-gray-100 flex flex-col">
    <Header @cart-click="showCart = true" />
    <CartPanel v-if="showCart" @close="showCart = false" />

    <!-- 步骤条 -->
    <div class="bg-white border-b border-gray-200 px-4 md:px-6 py-6">
      <div class="max-w-7xl mx-auto flex items-center justify-center gap-2 md:gap-4">
        <div v-for="(step, index) in steps" :key="step" class="flex items-center gap-2 md:gap-4">
          <div
            class="w-8 h-8 rounded-full flex items-center justify-center font-semibold text-sm"
            :class="index <= currentStep ? 'bg-green-500 text-white' : 'bg-gray-200 text-gray-600'"
          >
            ✓
          </div>
          <span class="text-xs md:text-sm hidden sm:inline">{{ step }}</span>
          <span v-if="index < steps.length - 1" class="w-6 md:w-12 h-0.5 bg-gray-300 hidden md:block"></span>
        </div>
      </div>
    </div>

    <!-- 主容器 -->
    <div class="flex-1 flex items-center justify-center px-4 py-12">
      <div class="max-w-md w-full text-center">
        <!-- 成功图标 -->
        <div class="mb-6">
          <div class="inline-block bg-green-100 rounded-full p-4 mb-4">
            <svg class="w-16 h-16 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
            </svg>
          </div>
        </div>

        <!-- 成功提示 -->
        <h1 class="text-3xl font-bold text-gray-900 mb-2">订单已提交！</h1>
        <p class="text-gray-600 mb-6">感谢您的购买，我们已经收到您的订单</p>

        <!-- 订单信息 -->
        <div class="bg-white rounded-lg shadow-sm p-6 mb-6 text-left space-y-4">
          <div class="border-b border-gray-200 pb-4">
            <p class="text-sm text-gray-500">订单号</p>
            <p class="font-bold text-gray-900 break-all">{{ orderId }}</p>
          </div>

          <div class="border-b border-gray-200 pb-4">
            <p class="text-sm text-gray-500">订单总额</p>
            <p class="text-2xl font-bold text-orange-600">¥{{ cartTotal.toFixed(2) }}</p>
          </div>

          <div>
            <p class="text-sm text-gray-500 mb-2">订单商品</p>
            <div class="space-y-2">
              <div v-for="item in cartItems" :key="item.product.id" class="flex justify-between text-sm">
                <span class="text-gray-600">{{ item.product.title }}</span>
                <span class="text-gray-900 font-medium">× {{ item.quantity }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 提示信息 -->
        <div class="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-6">
          <p class="text-sm text-blue-700">
            <span class="font-semibold">📧 您的订单已确认</span><br />
            <span class="text-xs">订单详情已发送至您的邮箱，请留意查收。</span>
          </p>
        </div>

        <!-- 行动按钮 -->
        <div class="space-y-3">
          <NuxtLink
            to="/account/orders"
            class="block w-full bg-orange-500 hover:bg-orange-600 text-white font-bold py-3 px-4 rounded-lg transition-colors"
          >
            查看我的订单
          </NuxtLink>

          <NuxtLink
            to="/"
            class="block w-full bg-gray-100 hover:bg-gray-200 text-gray-800 font-medium py-3 px-4 rounded-lg transition-colors"
          >
            继续购物
          </NuxtLink>
        </div>

        <!-- 帮助链接 -->
        <p class="text-xs text-gray-500 mt-6">
          有问题？<NuxtLink to="#" class="text-orange-500 hover:underline">联系客服</NuxtLink>
        </p>
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

const productStore = useProductStore()
const userStore = useUserStore()
const router = useRouter()
const showCart = ref(false)
const currentStep = ref(3)
const orderId = ref('')

const steps = ['购物车', '确认订单', '支付', '完成订单']

const cartItems = computed(() => productStore.cart)
const cartTotal = computed(() => productStore.cartTotal)

onMounted(async () => {
  if (!userStore.isLoggedIn) {
    try {
      await userStore.loadCurrentUser()
    } catch {
      router.push('/auth/login')
      return
    }
  }

  if (cartItems.value.length === 0) {
    return
  }

  let shipping = {
    name: '',
    phone: '',
    address: '',
  }
  if (typeof window !== 'undefined') {
    const cached = sessionStorage.getItem('checkoutShipping')
    if (cached) {
      try {
        shipping = JSON.parse(cached)
      } catch {
        shipping = { name: '', phone: '', address: '' }
      }
    }
  }

  try {
    const orderApi = useOrderApi()
    const result = await orderApi.createOrder({
      items: cartItems.value.map(item => ({
        productId: item.product.id,
        quantity: item.quantity,
        selectedSpecs: {},
      })),
      shippingAddress: {
        receiverName: shipping.name || '',
        receiverPhone: shipping.phone || '',
        province: '',
        city: '',
        district: '',
        detail: shipping.address || '',
      },
      remark: '',
    })
    orderId.value = result.orderNo || result.id || ''
    await userStore.loadOrders()
  } catch (error) {
    console.error('Failed to create order:', error)
  } finally {
    productStore.clearCart()
  }
})
</script>
