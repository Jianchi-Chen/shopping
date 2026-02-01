<template>
  <div
    class="bg-white rounded-lg shadow-sm hover:shadow-md transition-shadow overflow-hidden h-full flex flex-col"
  >
    <!-- 图片容器 -->
    <NuxtLink :to="`/product/${product.id}`" class="relative w-full h-48 bg-gray-100 overflow-hidden group block">
      <img
        :src="product.image"
        :alt="product.title"
        class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
      />

      <!-- 角标 -->
      <div v-if="product.badge" class="absolute top-2 left-2">
        <span class="inline-block bg-red-500 text-white text-xs font-bold px-2 py-1 rounded">
          {{ product.badge }}
        </span>
      </div>

      <!-- 收藏按钮 -->
      <button
        @click.prevent="handleToggleFavorite"
        class="absolute top-2 right-2 w-8 h-8 rounded-full bg-white shadow-md hover:bg-gray-100 transition-colors flex items-center justify-center"
      >
        <span v-if="product.isFavorite" class="text-red-500">♥</span>
        <span v-else class="text-gray-400">♡</span>
      </button>
    </NuxtLink>

    <!-- 内容区 -->
    <div class="p-3 flex-1 flex flex-col">
      <!-- 商品标题 -->
      <h3 class="text-sm font-medium text-gray-800 line-clamp-2 mb-2 flex-1">
        {{ product.title }}
      </h3>

      <!-- 评分 -->
      <div class="flex items-center gap-1 mb-3">
        <div class="flex items-center">
          <span v-for="i in 5" :key="i" class="text-xs">
            <span v-if="i <= Math.floor(product.rating)" class="text-yellow-400">★</span>
            <span v-else class="text-gray-300">★</span>
          </span>
        </div>
        <span class="text-xs text-gray-600">
          ({{ product.reviewCount }})
        </span>
      </div>

      <!-- 价格 -->
      <div class="mb-3">
        <div class="flex items-baseline gap-2">
          <span class="text-lg font-bold text-orange-600">¥{{ product.price }}</span>
          <span v-if="product.originalPrice" class="text-sm text-gray-500 line-through">
            ¥{{ product.originalPrice }}
          </span>
        </div>
      </div>

      <!-- 规格选择 -->
      <div v-if="product.specs && product.specs.length > 0" class="mb-3 space-y-2 max-h-24 overflow-y-auto">
        <div v-for="spec in product.specs" :key="spec.name" class="text-xs">
          <p class="text-gray-600 font-medium mb-1">{{ spec.name }}:</p>
          <div class="flex flex-wrap gap-1">
            <button
              v-for="option in spec.options"
              :key="option.value"
              class="px-2 py-1 border border-gray-300 rounded text-xs hover:border-orange-500 hover:text-orange-500 transition-colors"
            >
              {{ option.name }}
            </button>
          </div>
        </div>
      </div>

      <!-- 按钮区 -->
      <div class="flex gap-2 mt-auto">
        <button
          @click="handleAddToCart"
          class="flex-1 bg-orange-500 hover:bg-orange-600 text-white font-medium py-2 px-3 rounded text-sm transition-colors"
        >
          加购
        </button>
        <NuxtLink
          :to="`/product/${product.id}`"
          class="flex-1 border border-gray-300 hover:border-orange-500 text-gray-800 hover:text-orange-500 font-medium py-2 px-3 rounded text-sm transition-colors text-center"
        >
          详情
        </NuxtLink>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Product } from '../types/product'

const props = defineProps<{
  product: Product
}>()

const emit = defineEmits<{
  'add-to-cart': [product: Product]
  'toggle-favorite': [productId: string]
}>()

const handleAddToCart = () => {
  emit('add-to-cart', props.product)
}

const handleToggleFavorite = () => {
  emit('toggle-favorite', props.product.id)
}
</script>
