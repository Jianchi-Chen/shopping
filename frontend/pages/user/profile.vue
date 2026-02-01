<template>
  <div class="min-h-screen bg-gray-100 flex flex-col">
    <Header @cart-click="showCart = true" />
    <CartPanel v-if="showCart" @close="showCart = false" />

    <!-- 面包屑导航 -->
    <div class="bg-white border-b border-gray-200 px-4 md:px-6 py-3">
      <div class="max-w-7xl mx-auto text-sm text-gray-600">
        <NuxtLink to="/" class="hover:text-orange-500">首页</NuxtLink>
        <span class="mx-2">/</span>
        <span class="text-gray-900">我的账户</span>
      </div>
    </div>

    <!-- 主容器 -->
    <div class="flex-1 flex">
      <!-- 侧边栏菜单 -->
      <div class="w-full md:w-48 bg-white border-r border-gray-200">
        <nav class="divide-y divide-gray-200">
          <button
            v-for="item in menuItems"
            :key="item.id"
            @click="activeTab = item.id"
            :class="[
              'w-full text-left px-4 py-3 transition-colors',
              activeTab === item.id
                ? 'bg-orange-50 border-l-4 border-orange-500 text-orange-600 font-medium'
                : 'text-gray-700 hover:bg-gray-50',
            ]"
          >
            {{ item.label }}
          </button>
        </nav>
      </div>

      <!-- 内容区域 -->
      <div class="flex-1 p-4 md:p-6">
        <div class="max-w-4xl">
          <!-- 个人资料标签 -->
          <div v-if="activeTab === 'profile'" class="bg-white rounded-lg shadow-sm p-6">
            <h2 class="text-2xl font-bold text-gray-900 mb-6">个人资料</h2>

            <!-- 用户信息展示 -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8 pb-8 border-b border-gray-200">
              <div>
                <p class="text-sm text-gray-500 mb-1">账户名</p>
                <p class="text-lg font-medium text-gray-900">{{ currentUser?.name }}</p>
              </div>
              <div>
                <p class="text-sm text-gray-500 mb-1">邮箱地址</p>
                <p class="text-lg font-medium text-gray-900">{{ currentUser?.email }}</p>
              </div>
              <div>
                <p class="text-sm text-gray-500 mb-1">账户状态</p>
                <p class="text-lg font-medium text-green-600">✓ 已验证</p>
              </div>
              <div>
                <p class="text-sm text-gray-500 mb-1">账户等级</p>
                <p class="text-lg font-medium text-gray-900">普通会员</p>
              </div>
            </div>

            <!-- 编辑按钮 -->
            <div class="flex gap-3">
              <button
                v-if="!isEditing"
                @click="isEditing = true"
                class="px-6 py-2 bg-orange-500 hover:bg-orange-600 text-white font-medium rounded-lg transition-colors"
              >
                编辑资料
              </button>
              <div v-else class="space-y-4 w-full max-w-md">
                <input
                  v-model="editForm.name"
                  type="text"
                  placeholder="用户名"
                  class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500"
                />
                <input
                  v-model="editForm.email"
                  type="email"
                  placeholder="邮箱"
                  class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500"
                />
                <div class="flex gap-3">
                  <button
                    @click="saveProfile"
                    class="px-6 py-2 bg-green-500 hover:bg-green-600 text-white font-medium rounded-lg transition-colors"
                  >
                    保存
                  </button>
                  <button
                    @click="isEditing = false"
                    class="px-6 py-2 bg-gray-300 hover:bg-gray-400 text-gray-800 font-medium rounded-lg transition-colors"
                  >
                    取消
                  </button>
                </div>
              </div>
            </div>

            <!-- 安全区域 -->
            <div class="mt-8 pt-8 border-t border-gray-200">
              <h3 class="text-lg font-semibold text-gray-900 mb-4">账户安全</h3>
              <button class="px-6 py-2 border border-gray-300 text-gray-700 font-medium rounded-lg hover:bg-gray-50 transition-colors">
                修改密码
              </button>
            </div>
          </div>

          <!-- 收货地址标签 -->
          <div v-if="activeTab === 'address'" class="bg-white rounded-lg shadow-sm p-6">
            <div class="flex justify-between items-center mb-6">
              <h2 class="text-2xl font-bold text-gray-900">收货地址</h2>
              <button class="px-4 py-2 bg-orange-500 hover:bg-orange-600 text-white font-medium rounded-lg transition-colors">
                + 添加地址
              </button>
            </div>

            <div class="space-y-4">
              <div class="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow">
                <div class="flex justify-between items-start mb-3">
                  <div>
                    <p class="font-medium text-gray-900">默认收货地址</p>
                    <p class="text-sm text-gray-600 mt-2">北京市朝阳区建国路1号 邮编100000</p>
                  </div>
                  <span class="px-3 py-1 bg-green-100 text-green-700 text-xs font-medium rounded">默认</span>
                </div>
                <div class="flex gap-3">
                  <button class="text-orange-500 text-sm hover:underline">编辑</button>
                  <button class="text-red-500 text-sm hover:underline">删除</button>
                </div>
              </div>
            </div>
          </div>

          <!-- 收藏列表标签 -->
          <div v-if="activeTab === 'favorites'" class="bg-white rounded-lg shadow-sm p-6">
            <h2 class="text-2xl font-bold text-gray-900 mb-6">我的收藏</h2>

            <div v-if="favoriteProducts.length > 0" class="grid grid-cols-2 md:grid-cols-4 gap-4">
              <div v-for="product in favoriteProducts" :key="product.id" class="border border-gray-200 rounded-lg overflow-hidden hover:shadow-md transition-shadow">
                <div class="aspect-square bg-gray-200 flex items-center justify-center overflow-hidden">
                  <img :src="product.image" :alt="product.title" class="w-full h-full object-cover hover:scale-110 transition-transform" />
                </div>
                <div class="p-3">
                  <p class="text-sm font-medium text-gray-900 truncate">{{ product.title }}</p>
                  <p class="text-orange-600 font-semibold">¥{{ product.price.toFixed(2) }}</p>
                  <button
                    @click="toggleFavorite(product.id)"
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

const router = useRouter()
const productStore = useProductStore()
const userStore = useUserStore()

const activeTab = ref('profile')
const showCart = ref(false)
const isEditing = ref(false)

const editForm = ref({
  name: '',
  email: '',
})

const menuItems = [
  { id: 'profile', label: '个人资料' },
  { id: 'address', label: '收货地址' },
  { id: 'favorites', label: '我的收藏' },
]

const currentUser = computed(() => userStore.user)

const favoriteProducts = computed(() => {
  return productStore.products.filter(p => productStore.favorites.has(p.id))
})

onMounted(() => {
  // 检查登录状态
  if (!userStore.isLoggedIn) {
    router.push('/auth/login')
    return
  }

  // 初始化编辑表单
  if (currentUser.value) {
    editForm.value.name = currentUser.value.name
    editForm.value.email = currentUser.value.email
  }
})

const saveProfile = () => {
  if (currentUser.value) {
    userStore.updateProfile(editForm.value.name, editForm.value.email)
    isEditing.value = false
  }
}

const toggleFavorite = (productId: string) => {
  productStore.toggleFavorite(productId)
}
</script>
