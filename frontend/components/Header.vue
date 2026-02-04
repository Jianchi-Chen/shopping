<template>
  <header class="sticky top-0 z-40 bg-white border-b border-gray-200">
    <!-- 顶部导航栏 -->
    <div class="bg-gray-50 border-b border-gray-200 py-3 px-4 md:px-6">
      <div class="max-w-7xl mx-auto flex items-center justify-between">
        <!-- Logo -->
        <NuxtLink to="/" class="flex items-center gap-1 hover:opacity-80 transition-opacity" @click="scrollToTop">
          <span class="text-2xl font-bold text-orange-600">ShopHub</span>
        </NuxtLink>

        <!-- 搜索栏（桌面端） -->
        <div class="hidden md:flex flex-1 mx-6 max-w-xl">
          <input
      v-model="searchTerm"
      type="text"
      placeholder="搜索商品..."
      @keyup.enter="handleSearch"
      class="flex-1 px-4 py-2 border border-gray-300 rounded-l-md focus:outline-none focus:ring-2 focus:ring-orange-500"
    />
    <button
      @click="handleSearch"
          </button>
        </div>

        <!-- 右侧功能 -->
        <div class="flex items-center gap-4 md:gap-6">
          <!-- 用户账户 -->
          <div class="relative">
            <button
              v-if="userStore.isLoggedIn"
              @click.stop="showUserMenu = !showUserMenu"
              class="flex items-center gap-2 text-gray-700 hover:text-orange-500 transition-colors focus:outline-none"
            >
              <div v-if="userStore.user?.avatar" class="w-8 h-8 rounded-full overflow-hidden border-2 border-gray-200 hover:border-orange-500 transition-colors">
                <img :src="userStore.user.avatar" alt="Avatar" class="w-full h-full object-cover" />
              </div>
              <span v-else class="text-2xl">👤</span>
              <span class="hidden sm:inline text-sm font-medium">{{ userStore.user?.name || userStore.user?.username }}</span>
            </button>
            <NuxtLink
              v-else
              to="/auth/login"
              class="flex items-center gap-2 text-gray-700 hover:text-orange-500 transition-colors"
            >
              <span class="text-2xl">👤</span>
              <span class="hidden sm:inline text-sm font-medium">登录</span>
            </NuxtLink>

            <!-- 用户菜单下拉框 -->
            <div
              v-if="showUserMenu"
              @click.stop
              class="absolute right-0 mt-2 w-48 bg-white border border-gray-200 rounded-lg shadow-lg z-50"
            >
              <div v-if="userStore.isLoggedIn" class="p-4 border-b border-gray-200">
                <p class="font-semibold text-gray-900">{{ userStore.user?.name || userStore.user?.username }}</p>
                <p class="text-sm text-gray-500">{{ userStore.user?.email }}</p>
              </div>

              <nav class="divide-y divide-gray-100">
                <NuxtLink
                  v-if="!userStore.isLoggedIn"
                  to="/auth/login"
                  class="block w-full text-left px-4 py-3 text-gray-700 hover:bg-gray-50 transition-colors text-sm font-medium"
                  @click="showUserMenu = false"
                >
                  登录
                </NuxtLink>

                <NuxtLink
                  v-if="!userStore.isLoggedIn"
                  to="/auth/register"
                  class="block w-full text-left px-4 py-3 text-gray-700 hover:bg-gray-50 transition-colors text-sm font-medium"
                  @click="showUserMenu = false"
                >
                  注册
                </NuxtLink>

                <template v-if="userStore.isLoggedIn">
                  <NuxtLink
                    to="/user/profile"
                    class="block w-full text-left px-4 py-3 text-gray-700 hover:bg-gray-50 transition-colors text-sm"
                    @click="showUserMenu = false"
                  >
                    我的账户
                  </NuxtLink>

                  <NuxtLink
                    to="/user/orders"
                    class="block w-full text-left px-4 py-3 text-gray-700 hover:bg-gray-50 transition-colors text-sm"
                    @click="showUserMenu = false"
                  >
                    订单历史
                  </NuxtLink>

                  <button
                    @click="handleLogout"
                    class="w-full text-left px-4 py-3 text-red-600 hover:bg-red-50 transition-colors text-sm font-medium"
                  >
                    退出登录
                  </button>
                </template>
              </nav>
            </div>
          </div>

          <!-- 购物车 -->
          <button
            @click="$emit('cart-click')"
            class="relative flex items-center gap-1 text-gray-700 hover:text-orange-500 transition-colors"
          >
            <span class="text-xl">🛒</span>
            <span class="hidden sm:inline text-sm font-medium">购物车</span>
            <span
              v-if="cartCount > 0"
              class="absolute -top-2 -right-2 bg-red-500 text-white text-xs font-bold w-5 h-5 rounded-full flex items-center justify-center"
            >
              {{ cartCount }}
            </span>
          </button>

          <!-- 菜单（移动端） -->
          <button class="md:hidden text-gray-700 hover:text-orange-500 text-2xl">
            ☰
          </button>
        </div>
      </div>
    </div>

    <!-- 搜索栏（移动端） -->
    <div class="md:hidden px-4 py-3 border-b border-gray-200 bg-white">
      <input
        v-model="searchTerm"
        type="text"
        placeholder="搜索商品..."
        @keyup.enter="handleSearch"
        class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-orange-500 text-sm"
      />
    </div>
  </header>
</template>

<script setup lang="ts">
import { computed, ref, onMounted } from 'vue'
import { useProductStore } from '../stores/product'
import { useUserStore } from '../stores/user'
import { useRouter } from 'vue-router'

const productStore = useProductStore()
const userStore = useUserStore()
const router = useRouter()
const searchTerm = ref('')
const showUserMenu = ref(false)

const cartCount = computed(() => productStore.cartCount)

defineEmits<{
  'cart-click': []
}>()

// 页面点击时关闭菜单
onMounted(() => {
  document.addEventListener('click', () => {
    showUserMenu.value = false
  })
})

// 处理搜索
const handleSearch = () => {
  if (searchTerm.value.trim()) {
    // 导航回首页并传递搜索参数
    router.push({
      path: '/',
      query: { search: searchTerm.value }
    })
    showUserMenu.value = false
  }
}

// 处理退出登录
const handleLogout = () => {
  userStore.logout()
  showUserMenu.value = false
  router.push('/')
}

// 滚动到顶部
const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}
</script>
