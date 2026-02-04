<template>
  <div class="bg-white rounded-lg shadow-sm p-6">
    <h2 class="text-2xl font-bold text-gray-900 mb-6">我的收藏</h2>

    <div v-if="favoriteProducts.length > 0" class="grid grid-cols-2 md:grid-cols-4 gap-4">
      <div
        v-for="product in favoriteProducts"
        :key="product.id"
        class="border border-gray-200 rounded-lg overflow-hidden hover:shadow-md transition-shadow"
      >
        <NuxtLink :to="`/product/${product.id}`">
          <div class="aspect-square bg-gray-200 flex items-center justify-center overflow-hidden">
            <img
              :src="product.image"
              :alt="product.title"
              class="w-full h-full object-cover hover:scale-110 transition-transform cursor-pointer"
            />
          </div>
        </NuxtLink>
        <div class="p-3">
          <p class="text-sm font-medium text-gray-900 truncate">{{ product.title }}</p>
          <p class="text-orange-600 font-semibold">¥{{ product.price.toFixed(2) }}</p>
          <button
            @click="emit('toggle', product.id)"
            class="mt-2 w-full text-xs text-red-500 hover:bg-red-50 py-1 rounded transition-colors"
          >
            移除收藏
          </button>
        </div>
      </div>
    </div>

    <div v-else class="text-center py-12">
      <p class="text-gray-500 text-lg mb-4">暂无收藏商品</p>
      <NuxtLink to="/" class="text-orange-500 hover:underline">去购物 →</NuxtLink>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Product } from '../../types/product'

defineProps<{ favoriteProducts: Product[] }>()

const emit = defineEmits<{
  (e: 'toggle', productId: string): void
}>()
</script>
