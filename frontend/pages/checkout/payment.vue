<template>
  <div class="min-h-screen bg-gray-100 flex flex-col">
    <Header @cart-click="showCart = true" />
    <CartPanel v-if="showCart" @close="showCart = false" />

    <!-- 面包屑 -->
    <div class="bg-white py-3 px-4 md:px-6 border-b border-gray-200">
      <div class="max-w-7xl mx-auto">
        <NuxtLink to="/" class="text-orange-500 hover:underline text-sm">首页</NuxtLink>
        <span class="text-gray-400 mx-2">/</span>
        <NuxtLink to="/checkout/cart" class="text-orange-500 hover:underline text-sm">购物车</NuxtLink>
        <span class="text-gray-400 mx-2">/</span>
        <NuxtLink to="/checkout" class="text-orange-500 hover:underline text-sm">确认订单</NuxtLink>
        <span class="text-gray-400 mx-2">/</span>
        <span class="text-gray-600 text-sm">支付</span>
      </div>
    </div>

    <!-- 步骤条 -->
    <div class="bg-white border-b border-gray-200 px-4 md:px-6 py-6">
      <div class="max-w-7xl mx-auto flex items-center justify-center gap-2 md:gap-4">
        <div v-for="(step, index) in steps" :key="step" class="flex items-center gap-2 md:gap-4">
          <div
            class="w-8 h-8 rounded-full flex items-center justify-center font-semibold text-sm"
            :class="
              index < currentStep
                ? 'bg-green-500 text-white'
                : index === currentStep
                ? 'bg-orange-500 text-white'
                : 'bg-gray-200 text-gray-600'
            "
          >
            {{ index < currentStep ? '✓' : index + 1 }}
          </div>
          <span class="text-xs md:text-sm hidden sm:inline">{{ step }}</span>
          <span v-if="index < steps.length - 1" class="w-6 md:w-12 h-0.5 bg-gray-300 hidden md:block"></span>
        </div>
      </div>
    </div>

    <!-- 主容器 -->
    <div class="flex-1 max-w-7xl mx-auto w-full px-4 md:px-6 py-6">
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <!-- 左侧支付方式 -->
        <div class="lg:col-span-2">
          <div class="bg-white rounded-lg shadow-sm p-6">
            <h2 class="text-lg font-bold text-gray-900 mb-6">选择支付方式</h2>

            <div class="space-y-4">
              <!-- 信用卡 -->
              <label class="flex items-start p-4 border-2 border-gray-300 rounded-lg hover:border-orange-500 cursor-pointer transition-colors"
                :class="paymentMethod === 'card' ? 'border-orange-500 bg-orange-50' : ''">
                <input v-model="paymentMethod" type="radio" value="card" class="mt-1 mr-4" />
                <div class="flex-1">
                  <p class="font-bold text-gray-900">信用卡 / 借记卡</p>
                  <p class="text-sm text-gray-500 mt-1">支持 Visa、MasterCard、银联等主流卡</p>
                </div>
              </label>

              <!-- 银行转账 -->
              <label class="flex items-start p-4 border-2 border-gray-300 rounded-lg hover:border-orange-500 cursor-pointer transition-colors"
                :class="paymentMethod === 'transfer' ? 'border-orange-500 bg-orange-50' : ''">
                <input v-model="paymentMethod" type="radio" value="transfer" class="mt-1 mr-4" />
                <div class="flex-1">
                  <p class="font-bold text-gray-900">银行转账</p>
                  <p class="text-sm text-gray-500 mt-1">直接转账至我们的账户</p>
                </div>
              </label>

              <!-- 电子钱包 -->
              <label class="flex items-start p-4 border-2 border-gray-300 rounded-lg hover:border-orange-500 cursor-pointer transition-colors"
                :class="paymentMethod === 'wallet' ? 'border-orange-500 bg-orange-50' : ''">
                <input v-model="paymentMethod" type="radio" value="wallet" class="mt-1 mr-4" />
                <div class="flex-1">
                  <p class="font-bold text-gray-900">电子钱包</p>
                  <p class="text-sm text-gray-500 mt-1">支持支付宝、微信等电子钱包</p>
                </div>
              </label>
            </div>

            <!-- 继续按钮 -->
            <div class="mt-8 pt-6 border-t border-gray-200">
              <button
                @click="handleProceedToPayment"
                class="w-full bg-orange-500 hover:bg-orange-600 text-white font-bold py-3 px-4 rounded-lg transition-colors"
              >
                继续支付
              </button>
            </div>
          </div>
        </div>

        <!-- 右侧订单汇总 -->
        <div class="lg:sticky lg:top-6 h-fit">
          <div class="bg-white rounded-lg shadow-sm p-6 space-y-4">
            <h3 class="text-lg font-bold text-gray-900">订单详情</h3>

            <div class="border-b border-gray-200 pb-4 max-h-48 overflow-y-auto space-y-2">
              <div v-for="item in cartItems" :key="item.product.id" class="flex justify-between text-sm">
                <span class="text-gray-600">{{ item.product.title }} × {{ item.quantity }}</span>
                <span class="font-medium">¥{{ (item.product.price * item.quantity).toFixed(2) }}</span>
              </div>
            </div>

            <div class="space-y-2 text-sm border-b border-gray-200 pb-4">
              <div class="flex justify-between text-gray-700">
                <span>商品小计:</span>
                <span>¥{{ cartTotal.toFixed(2) }}</span>
              </div>
              <div class="flex justify-between text-gray-700">
                <span>运费:</span>
                <span>免费</span>
              </div>
            </div>

            <div class="flex justify-between items-center text-lg font-bold">
              <span>应付金额:</span>
              <span class="text-orange-600 text-2xl">¥{{ cartTotal.toFixed(2) }}</span>
            </div>

            <div class="bg-blue-50 border border-blue-200 rounded-lg p-3 text-xs text-blue-700">
              <p class="font-semibold mb-1">ℹ️ 演示提示</p>
              <p>这是一个前端演示页面，不包含真实支付功能。点击"继续支付"按钮将进入模拟支付流程。</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useProductStore } from '../../stores/product'

const router = useRouter()
const productStore = useProductStore()
const showCart = ref(false)
const currentStep = ref(3)

const steps = ['购物车', '确认订单', '支付', '完成订单']
const paymentMethod = ref('card')

const cartItems = computed(() => productStore.cart)
const cartTotal = computed(() => productStore.cartTotal)

const handleProceedToPayment = () => {
  // 模拟支付过程，跳转到订单确认页面
  router.push('/checkout/order-confirmation')
}
</script>
