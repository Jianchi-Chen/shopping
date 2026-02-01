<template>
  <div class="min-h-screen bg-gray-100 flex flex-col">
    <Header @cart-click="showCart = true" />
    <CartPanel v-if="showCart" @close="showCart = false" />

    <!-- 面包屑 -->
    <div class="bg-white py-3 px-4 md:px-6 border-b border-gray-200">
      <div class="max-w-7xl mx-auto">
        <NuxtLink to="/" class="text-orange-500 hover:underline text-sm">首页</NuxtLink>
        <span class="text-gray-400 mx-2">/</span>
        <span class="text-gray-600 text-sm">购物车</span>
      </div>
    </div>

    <!-- 主容器 -->
    <div class="flex-1 max-w-7xl mx-auto w-full px-4 md:px-6 py-6">
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <!-- 购物车列表 -->
        <div class="lg:col-span-2">
          <div v-if="cartItems.length === 0" class="bg-white rounded-lg shadow-sm p-6 text-center">
            <p class="text-gray-500 mb-4">购物车为空</p>
            <NuxtLink to="/" class="text-orange-500 hover:underline">
              继续购物
            </NuxtLink>
          </div>

          <div v-else class="space-y-4">
            <div class="bg-white rounded-lg shadow-sm overflow-hidden">
              <!-- 表头 -->
              <div class="hidden md:grid grid-cols-12 gap-4 bg-gray-50 p-4 text-sm font-semibold text-gray-700 border-b border-gray-200">
                <div class="col-span-5">商品</div>
                <div class="col-span-2 text-center">单价</div>
                <div class="col-span-2 text-center">数量</div>
                <div class="col-span-2 text-right">小计</div>
                <div class="col-span-1"></div>
              </div>

              <!-- 购物车项目 -->
              <div v-for="item in cartItems" :key="item.product.id" class="border-b border-gray-200 last:border-0">
                <div class="p-4 grid grid-cols-1 md:grid-cols-12 md:gap-4 md:items-center">
                  <!-- 商品信息 -->
                  <div class="md:col-span-5 flex gap-3 mb-4 md:mb-0">
                    <img
                      :src="item.product.image"
                      :alt="item.product.title"
                      class="w-20 h-20 object-cover rounded"
                    />
                    <div class="flex-1">
                      <NuxtLink
                        :to="`/product/${item.product.id}`"
                        class="text-gray-900 font-medium hover:text-orange-500 truncate block"
                      >
                        {{ item.product.title }}
                      </NuxtLink>
                      <p class="text-xs text-gray-500 mt-1">{{ item.product.category }}</p>
                    </div>
                  </div>

                  <!-- 单价 -->
                  <div class="md:col-span-2 text-center md:text-center">
                    <span class="md:hidden text-gray-600 text-sm">单价: </span>
                    <span class="font-medium">¥{{ item.product.price }}</span>
                  </div>

                  <!-- 数量调整 -->
                  <div class="md:col-span-2 flex items-center justify-center">
                    <div class="flex items-center border border-gray-300 rounded w-fit">
                      <button
                        @click="updateQuantity(item.product.id, item.quantity - 1)"
                        class="px-2 py-1 text-gray-600 hover:bg-gray-100"
                      >
                        −
                      </button>
                      <input
                        :value="item.quantity"
                        type="number"
                        @change="e => updateQuantity(item.product.id, parseInt((e.target as HTMLInputElement).value))"
                        class="w-10 text-center border-0 focus:outline-none"
                      />
                      <button
                        @click="updateQuantity(item.product.id, item.quantity + 1)"
                        class="px-2 py-1 text-gray-600 hover:bg-gray-100"
                      >
                        +
                      </button>
                    </div>
                  </div>

                  <!-- 小计 -->
                  <div class="md:col-span-2 text-right font-semibold text-orange-600">
                    ¥{{ (item.product.price * item.quantity).toFixed(2) }}
                  </div>

                  <!-- 删除按钮 -->
                  <div class="md:col-span-1 flex justify-end">
                    <button
                      @click="removeFromCart(item.product.id)"
                      class="text-red-500 hover:text-red-700 text-sm"
                    >
                      删除
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 结算栏 -->
        <div v-if="cartItems.length > 0" class="lg:sticky lg:top-6 h-fit">
          <div class="bg-white rounded-lg shadow-sm p-6 space-y-4">
            <h3 class="text-lg font-bold text-gray-900">订单汇总</h3>

            <div class="space-y-2 text-sm border-b border-gray-200 pb-4">
              <div class="flex justify-between text-gray-700">
                <span>商品总数:</span>
                <span>{{ totalQuantity }} 件</span>
              </div>
              <div class="flex justify-between text-gray-700">
                <span>商品总价:</span>
                <span>¥{{ cartTotal.toFixed(2) }}</span>
              </div>
              <div class="flex justify-between text-gray-700">
                <span>运费:</span>
                <span class="text-green-600">免费</span>
              </div>
            </div>

            <div class="flex justify-between items-center text-lg font-bold">
              <span>应付总额:</span>
              <span class="text-orange-600">¥{{ cartTotal.toFixed(2) }}</span>
            </div>

            <NuxtLink
              to="checkout"
              class="block w-full bg-orange-500 hover:bg-orange-600 text-white font-bold py-3 px-4 rounded-lg text-center transition-colors"
            >
              结算
            </NuxtLink>

            <NuxtLink
              to="/"
              class="block w-full bg-gray-100 hover:bg-gray-200 text-gray-800 font-medium py-2 px-4 rounded-lg text-center transition-colors"
            >
              继续购物
            </NuxtLink>
          </div>
        </div>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ref } from 'vue'
import { useProductStore } from '../../stores/product'

const productStore = useProductStore()
const showCart = ref(false)

const cartItems = computed(() => productStore.cart)
const cartTotal = computed(() => productStore.cartTotal)
const totalQuantity = computed(() => productStore.cartCount)

const updateQuantity = (productId: string, quantity: number) => {
  if (quantity > 0) {
    productStore.updateCartQuantity(productId, quantity)
  }
}

const removeFromCart = (productId: string) => {
  productStore.removeFromCart(productId)
}
</script>
