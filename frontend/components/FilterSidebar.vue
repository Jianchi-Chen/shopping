<template>
  <aside class="hidden md:block w-48 bg-white border-r border-gray-200">
    <div class="p-4 space-y-6">
      <!-- 分类过滤 -->
      <div>
        <h3 class="text-sm font-bold text-gray-900 mb-3 uppercase">分类</h3>
        <div class="space-y-2">
          <button
            v-for="category in categories"
            :key="category.id"
            @click="$emit('category-select', category.name)"
            class="block w-full text-left text-sm text-gray-600 hover:text-orange-500 py-2 transition-colors"
          >
            {{ category.name }}
            <span class="text-gray-400 text-xs float-right">({{ category.productCount }})</span>
          </button>
        </div>
      </div>

      <!-- 价格范围 -->
      <div>
        <h3 class="text-sm font-bold text-gray-900 mb-3 uppercase">价格范围</h3>
        <div class="space-y-2">
          <label class="flex items-center gap-2 text-sm cursor-pointer">
            <input type="radio" name="price" value="all" :checked="priceRange === 'all'" @change="handlePriceChange('all')" class="w-4 h-4" />
            <span class="text-gray-600">全部价格</span>
          </label>
          <label class="flex items-center gap-2 text-sm cursor-pointer">
            <input type="radio" name="price" value="low" :checked="priceRange === 'low'" @change="handlePriceChange('low')" class="w-4 h-4" />
            <span class="text-gray-600">¥0 - ¥500</span>
          </label>
          <label class="flex items-center gap-2 text-sm cursor-pointer">
            <input type="radio" name="price" value="mid" :checked="priceRange === 'mid'" @change="handlePriceChange('mid')" class="w-4 h-4" />
            <span class="text-gray-600">¥500 - ¥5000</span>
          </label>
          <label class="flex items-center gap-2 text-sm cursor-pointer">
            <input type="radio" name="price" value="high" :checked="priceRange === 'high'" @change="handlePriceChange('high')" class="w-4 h-4" />
            <span class="text-gray-600">¥5000+</span>
          </label>
          <!-- 自定义价格范围 -->
          <div class="border-t border-gray-200 pt-2 mt-2">
            <p class="text-xs text-gray-600 mb-2">自定义范围：</p>
            <div class="flex gap-2">
              <input
                v-model.number="customMinPrice"
                type="number"
                placeholder="最低"
                class="w-1/2 px-2 py-1 border border-gray-300 rounded text-xs focus:outline-none focus:ring-1 focus:ring-orange-500"
              />
              <input
                v-model.number="customMaxPrice"
                type="number"
                placeholder="最高"
                class="w-1/2 px-2 py-1 border border-gray-300 rounded text-xs focus:outline-none focus:ring-1 focus:ring-orange-500"
              />
            </div>
            <button
              @click="applyCustomPrice"
              class="w-full mt-2 bg-orange-100 hover:bg-orange-200 text-orange-700 text-xs font-medium py-1 rounded transition-colors"
            >
              应用
            </button>
          </div>
        </div>
      </div>

      <!-- 评分过滤 -->
      <div>
        <h3 class="text-sm font-bold text-gray-900 mb-3 uppercase">评分</h3>
        <div class="space-y-2">
          <label class="flex items-center gap-2 text-sm cursor-pointer">
            <input type="checkbox" class="w-4 h-4" />
            <span class="text-gray-600">4★ 以上</span>
          </label>
          <label class="flex items-center gap-2 text-sm cursor-pointer">
            <input type="checkbox" class="w-4 h-4" />
            <span class="text-gray-600">3★ 以上</span>
          </label>
        </div>
      </div>

      <!-- 清除过滤 -->
      <button
        @click="handleResetFilters"
        class="w-full bg-gray-100 hover:bg-gray-200 text-gray-800 text-sm font-medium py-2 rounded transition-colors"
      >
        清除过滤
      </button>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { Category } from '../types/product'

const categories = ref<Category[]>([])
const priceRange = ref('all')
const customMinPrice = ref<number | null>(null)
const customMaxPrice = ref<number | null>(null)

const emit = defineEmits<{
  'category-select': [categoryId: string]
  'price-filter': [range: string, min?: number, max?: number]
  'reset-filters': []
}>()

const loadCategories = async () => {
  try {
    const categoryApi = useCategoryApi()
    const data = await categoryApi.getCategories()
    categories.value = data.map(item => ({
      id: item.id,
      name: item.name,
      image: item.icon || '',
      productCount: item.productCount ?? 0,
    }))
  } catch (error) {
    console.error('Failed to load categories:', error)
    categories.value = []
  }
}

onMounted(() => {
  loadCategories()
})

// 处理预设价格范围变化
const handlePriceChange = (range: string) => {
  priceRange.value = range
  customMinPrice.value = null
  customMaxPrice.value = null
  
  if (range === 'low') {
    emit('price-filter', range, 0, 500)
  } else if (range === 'mid') {
    emit('price-filter', range, 500, 5000)
  } else if (range === 'high') {
    emit('price-filter', range, 5000, Infinity)
  } else {
    emit('price-filter', range)
  }
}

// 应用自定义价格范围
const applyCustomPrice = () => {
  if (customMinPrice.value !== null || customMaxPrice.value !== null) {
    const min = customMinPrice.value ?? 0
    const max = customMaxPrice.value ?? Infinity
    priceRange.value = 'custom'
    emit('price-filter', 'custom', min, max)
  }
}

// 处理清除过滤 - 重置价格状态并发送事件
const handleResetFilters = () => {
  priceRange.value = 'all'
  customMinPrice.value = null
  customMaxPrice.value = null
  emit('reset-filters')
}
</script>
