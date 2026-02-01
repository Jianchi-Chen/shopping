<template>
  <div
    class="fixed inset-0 bg-black/50 z-50 flex items-center justify-end md:items-start md:pt-20"
    @click="$emit('close')"
  >
    <!-- 购物车面板 -->
    <div
      class="bg-white w-full md:w-96 h-full md:h-auto max-h-96 md:rounded-lg shadow-2xl overflow-y-auto"
      @click.stop
    >
      <!-- 标题 -->
      <div class="sticky top-0 bg-white border-b border-gray-200 px-6 py-4 flex items-center justify-between">
        <h2 class="text-lg font-bold">购物车</h2>
        <button
          @click="$emit('close')"
          class="text-gray-500 hover:text-gray-700 text-2xl leading-none"
        >
          ×
        </button>
      </div>

      <!-- 购物车内容 -->
      <div class="p-6 space-y-4">
        <!-- 空购物车提示 -->
        <div v-if="cartItems.length === 0" class="text-center py-8">
          <p class="text-gray-500 text-base mb-4">购物车为空</p>
          <button
            @click="$emit('close')"
            class="bg-orange-500 hover:bg-orange-600 text-white font-medium py-2 px-6 rounded transition-colors"
          >
            继续购物
          </button>
        </div>

        <!-- 购物车商品 -->
        <div v-else class="space-y-3">
          <div
            v-for="item in cartItems"
            :key="item.product.id"
            class="flex gap-3 pb-3 border-b border-gray-100"
          >
            <!-- 商品图片 -->
            <img
              :src="item.product.image"
              :alt="item.product.title"
              class="w-16 h-16 object-cover rounded"
            />

            <!-- 商品信息 -->
            <div class="flex-1">
              <p class="text-sm font-medium text-gray-900 line-clamp-2">{{ item.product.title }}</p>
              <p class="text-sm font-bold text-orange-600 my-1">¥{{ item.product.price }}</p>

              <!-- 数量控制 -->
              <div class="flex items-center gap-2">
                <button
                  @click="updateQuantity(item.product.id, item.quantity - 1)"
                  class="w-6 h-6 border border-gray-300 rounded text-gray-600 hover:border-orange-500 hover:text-orange-500 transition-colors flex items-center justify-center text-sm"
                >
                  −
                </button>
                <span class="text-sm text-gray-700 w-6 text-center">{{ item.quantity }}</span>
                <button
                  @click="updateQuantity(item.product.id, item.quantity + 1)"
                  class="w-6 h-6 border border-gray-300 rounded text-gray-600 hover:border-orange-500 hover:text-orange-500 transition-colors flex items-center justify-center text-sm"
                >
                  +
                </button>
                <button
                  @click="removeFromCart(item.product.id)"
                  class="ml-auto text-xs text-red-500 hover:text-red-700 transition-colors"
                >
                  删除
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 底部结算 -->
      <div v-if="cartItems.length > 0" class="sticky bottom-0 bg-white border-t border-gray-200 px-6 py-4 space-y-3">
        <!-- 总价 -->
        <div class="flex justify-between items-center text-lg">
          <span class="font-bold">总计：</span>
          <span class="font-bold text-orange-600">¥{{ cartTotal.toFixed(2) }}</span>
        </div>

        <!-- 按钮 -->
        <button
          @click="handleCheckout"
          class="w-full bg-orange-500 hover:bg-orange-600 text-white font-bold py-3 rounded transition-colors"
        >
          去结算
        </button>
        <button
          @click="clearCart"
          class="w-full border border-gray-300 hover:border-red-500 text-gray-800 hover:text-red-500 font-medium py-2 rounded transition-colors"
        >
          清空购物车
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useProductStore } from '../stores/product'

const router = useRouter()
const productStore = useProductStore()

const cartItems = computed(() => productStore.cart)
const cartTotal = computed(() => productStore.cartTotal)

const updateQuantity = (productId: string, quantity: number) => {
  productStore.updateCartQuantity(productId, quantity)
}

const removeFromCart = (productId: string) => {
  productStore.removeFromCart(productId)
}

const clearCart = () => {
  productStore.clearCart()
}

// 处理去结算
const handleCheckout = () => {
  router.push('/checkout/cart')
}

defineEmits<{
  close: []
}>()
</script>
