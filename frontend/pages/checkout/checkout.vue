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
        <span class="text-gray-600 text-sm">确认订单</span>
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
        <!-- 左侧表单 -->
        <div class="lg:col-span-2 space-y-6">
          <!-- 收货地址 -->
          <div class="bg-white rounded-lg shadow-sm p-6">
            <h2 class="text-lg font-bold text-gray-900 mb-4">收货地址</h2>
            
            <!-- 预设地址选择 -->
            <div v-if="savedAddresses.length > 0" class="mb-6">
              <p class="text-sm font-medium text-gray-700 mb-3">选择预设地址</p>
              <div class="space-y-2">
                <label 
                  v-for="addr in savedAddresses"
                  :key="addr.id"
                  class="flex items-start p-3 border border-gray-300 rounded-lg hover:border-orange-500 cursor-pointer transition-colors"
                  :class="selectedAddressId === addr.id ? 'border-orange-500 bg-orange-50' : ''"
                >
                  <input 
                    type="radio" 
                    :value="addr.id" 
                    v-model="selectedAddressId"
                    @change="applySelectedAddress"
                    class="mt-1 mr-3"
                  />
                  <div class="flex-1">
                    <p class="font-medium text-gray-900">{{ addr.receiverName }} {{ addr.receiverPhone }}</p>
                    <p class="text-sm text-gray-600">{{ addr.province }} {{ addr.city }} {{ addr.district }} {{ addr.addressDetail }}</p>
                    <p v-if="addr.postalCode" class="text-xs text-gray-500">邮编: {{ addr.postalCode }}</p>
                    <span v-if="addr.isDefault" class="inline-block mt-1 px-2 py-0.5 bg-orange-100 text-orange-700 text-xs rounded">默认地址</span>
                  </div>
                </label>
              </div>
              <button
                @click="clearAddressForm"
                class="mt-3 text-sm text-gray-600 hover:text-gray-900 underline"
              >
                输入新地址
              </button>
            </div>

            <!-- 地址表单 -->
            <form class="space-y-4">
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <input
                  v-model="form.name"
                  type="text"
                  placeholder="收货人姓名"
                  class="px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500"
                />
                <input
                  v-model="form.phone"
                  type="tel"
                  placeholder="手机号码"
                  class="px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500"
                />
              </div>
              <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                <input
                  v-model="form.province"
                  type="text"
                  placeholder="省份"
                  class="px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500"
                />
                <input
                  v-model="form.city"
                  type="text"
                  placeholder="城市"
                  class="px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500"
                />
                <input
                  v-model="form.district"
                  type="text"
                  placeholder="区县"
                  class="px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500"
                />
              </div>
              <input
                v-model="form.address"
                type="text"
                placeholder="详细地址（如：街道名、房间号等）"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500"
              />
              <input
                v-model="form.zipCode"
                type="text"
                placeholder="邮编"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500"
              />
              
              <!-- 保存地址按钮（仅在输入新地址时显示） -->
              <div v-if="isInputtingNewAddress" class="flex gap-3 pt-2">
                <button
                  type="button"
                  @click="handleSaveAddress"
                  class="flex-1 bg-blue-500 hover:bg-blue-600 text-white text-sm font-medium py-2 rounded-lg transition-colors"
                >
                  保存地址
                </button>
                <button
                  type="button"
                  @click="clearAddressForm"
                  class="flex-1 bg-gray-100 hover:bg-gray-200 text-gray-700 text-sm font-medium py-2 rounded-lg transition-colors"
                >
                  清空
                </button>
              </div>
            </form>
          </div>

          <!-- 配送方式 -->
          <div class="bg-white rounded-lg shadow-sm p-6">
            <h2 class="text-lg font-bold text-gray-900 mb-4">配送方式</h2>
            <div class="space-y-3">
              <label class="flex items-center p-3 border border-gray-300 rounded-lg hover:border-orange-500 cursor-pointer"
                :class="form.shippingMethod === 'standard' ? 'border-orange-500 bg-orange-50' : ''">
                <input v-model="form.shippingMethod" type="radio" value="standard" class="mr-3" />
                <div class="flex-1">
                  <p class="font-medium">标准配送（5-7个工作日）</p>
                  <p class="text-sm text-gray-500">免运费</p>
                </div>
              </label>
              <label class="flex items-center p-3 border border-gray-300 rounded-lg hover:border-orange-500 cursor-pointer"
                :class="form.shippingMethod === 'express' ? 'border-orange-500 bg-orange-50' : ''">
                <input v-model="form.shippingMethod" type="radio" value="express" class="mr-3" />
                <div class="flex-1">
                  <p class="font-medium">快速配送（1-2个工作日）</p>
                  <p class="text-sm text-gray-500">运费 ¥10</p>
                </div>
              </label>
            </div>
          </div>

          <!-- 支付方式 -->
          <div class="bg-white rounded-lg shadow-sm p-6">
            <h2 class="text-lg font-bold text-gray-900 mb-4">支付方式</h2>
            <div class="space-y-3">
              <label class="flex items-center p-3 border border-gray-300 rounded-lg hover:border-orange-500 cursor-pointer"
                :class="form.paymentMethod === 'card' ? 'border-orange-500 bg-orange-50' : ''">
                <input v-model="form.paymentMethod" type="radio" value="card" class="mr-3" />
                <span>信用卡 / 借记卡</span>
              </label>
              <label class="flex items-center p-3 border border-gray-300 rounded-lg hover:border-orange-500 cursor-pointer"
                :class="form.paymentMethod === 'transfer' ? 'border-orange-500 bg-orange-50' : ''">
                <input v-model="form.paymentMethod" type="radio" value="transfer" class="mr-3" />
                <span>银行转账</span>
              </label>
              <label class="flex items-center p-3 border border-gray-300 rounded-lg hover:border-orange-500 cursor-pointer"
                :class="form.paymentMethod === 'wallet' ? 'border-orange-500 bg-orange-50' : ''">
                <input v-model="form.paymentMethod" type="radio" value="wallet" class="mr-3" />
                <span>电子钱包</span>
              </label>
            </div>
          </div>
        </div>

        <!-- 右侧订单汇总 -->
        <div class="lg:sticky lg:top-6 h-fit">
          <div class="bg-white rounded-lg shadow-sm p-6 space-y-4">
            <h3 class="text-lg font-bold text-gray-900">订单汇总</h3>

            <!-- 商品列表 -->
            <div class="border-b border-gray-200 pb-4 max-h-48 overflow-y-auto space-y-2">
              <div v-for="item in cartItems" :key="item.product.id" class="flex justify-between text-sm">
                <span class="text-gray-600">{{ item.product.title }} × {{ item.quantity }}</span>
                <span class="font-medium">¥{{ (item.product.price * item.quantity).toFixed(2) }}</span>
              </div>
            </div>

            <!-- 费用计算 -->
            <div class="space-y-2 text-sm border-b border-gray-200 pb-4">
              <div class="flex justify-between text-gray-700">
                <span>商品小计:</span>
                <span>¥{{ cartTotal.toFixed(2) }}</span>
              </div>
              <div class="flex justify-between text-gray-700">
                <span>运费:</span>
                <span>{{ form.shippingMethod === 'express' ? '¥10' : '免费' }}</span>
              </div>
            </div>

            <!-- 合计 -->
            <div class="flex justify-between items-center text-lg font-bold">
              <span>应付总额:</span>
              <span class="text-orange-600">
                ¥{{ (cartTotal + (form.shippingMethod === 'express' ? 10 : 0)).toFixed(2) }}
              </span>
            </div>

            <!-- 行动按钮 -->
            <button
              @click="handleConfirmOrder"
              class="w-full bg-orange-500 hover:bg-orange-600 text-white font-bold py-3 px-4 rounded-lg transition-colors"
            >
              确认订单
            </button>

            <NuxtLink
              to="/checkout/cart"
              class="block w-full bg-gray-100 hover:bg-gray-200 text-gray-800 font-medium py-2 px-4 rounded-lg text-center transition-colors"
            >
              返回购物车
            </NuxtLink>
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
import { useMessage } from '../../composables/useMessage'
import { useAddressApi } from '../../composables/useAddressApi'

const router = useRouter()
const productStore = useProductStore()
const userStore = useUserStore()
const { warning, success } = useMessage()
const { getAddresses, createAddress } = useAddressApi()
const showCart = ref(false)
const currentStep = ref(2) // 确认订单页面

const steps = ['购物车', '确认订单', '支付', '完成订单']

const form = ref({
  name: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  address: '',
  zipCode: '',
  shippingMethod: 'standard' as 'standard' | 'express',
  paymentMethod: 'card' as 'card' | 'transfer' | 'wallet',
})

const savedAddresses = ref<any[]>([])
const selectedAddressId = ref<string | null>(null)
const isInputtingNewAddress = ref(false)

const cartItems = computed(() => productStore.cart)
const cartTotal = computed(() => productStore.cartTotal)

// 加载保存的地址
const loadAddresses = async () => {
  try {
    const result = await getAddresses()
    // 后端返回的是数组，直接使用
    savedAddresses.value = Array.isArray(result) ? result : (result.data || result.list || [])
    // 自动选择默认地址
    const defaultAddr = savedAddresses.value.find((a: any) => a.isDefault)
    if (defaultAddr) {
      selectedAddressId.value = defaultAddr.id
      applySelectedAddress()
    }
  } catch (error) {
    console.error('加载地址失败:', error)
  }
}

// 应用选中的地址
const applySelectedAddress = () => {
  if (!selectedAddressId.value) return
  const addr = savedAddresses.value.find((a: any) => a.id === selectedAddressId.value)
  if (addr) {
    form.value.name = addr.receiverName
    form.value.phone = addr.receiverPhone
    form.value.province = addr.province
    form.value.city = addr.city
    form.value.district = addr.district
    form.value.address = addr.addressDetail
    form.value.zipCode = addr.postalCode
    isInputtingNewAddress.value = false
  }
}

// 清空地址表单
const clearAddressForm = () => {
  selectedAddressId.value = null
  form.value.name = ''
  form.value.phone = ''
  form.value.province = ''
  form.value.city = ''
  form.value.district = ''
  form.value.address = ''
  form.value.zipCode = ''
  isInputtingNewAddress.value = true
}

// 保存新地址
const handleSaveAddress = async () => {
  if (!form.value.name || !form.value.phone || !form.value.address) {
    warning('请填写完整的地址信息')
    return
  }

  try {
    const newAddress = await createAddress({
      receiverName: form.value.name,
      receiverPhone: form.value.phone,
      province: form.value.province,
      city: form.value.city,
      district: form.value.district,
      addressDetail: form.value.address,
      postalCode: form.value.zipCode,
      isDefault: false,
    })
    success('地址保存成功')
    isInputtingNewAddress.value = false
    await loadAddresses()
  } catch (error: any) {
    warning('保存地址失败: ' + error.message)
  }
}

const handleConfirmOrder = () => {
  // 验证表单
  if (!form.value.name || !form.value.phone || !form.value.address) {
    warning('请填写完整的收货信息')
    return
  }

  if (typeof window !== 'undefined') {
    sessionStorage.setItem('checkoutShipping', JSON.stringify(form.value))
  }

  // 导航到支付页面
  router.push('/checkout/payment')
}

onMounted(() => {
  loadAddresses()
})
</script>
