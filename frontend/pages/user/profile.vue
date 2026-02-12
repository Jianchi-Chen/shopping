<template>
  <div class="min-h-screen bg-gray-100 flex flex-col">
    <Header @cart-click="showCart = true" />
    <CartPanel v-if="showCart" @close="showCart = false" />

    <div class="bg-white border-b border-gray-200 px-4 md:px-6 py-3">
      <div class="max-w-7xl mx-auto text-sm text-gray-600">
        <NuxtLink to="/" class="hover:text-orange-500">首页</NuxtLink>
        <span class="mx-2">/</span>
        <span class="text-gray-900">我的账户</span>
      </div>
    </div>

    <div class="flex-1 flex flex-col md:flex-row">
      <div class="w-full md:w-48 bg-white border-r border-gray-200">
        <nav class="divide-y divide-gray-200">
          <button
            v-for="item in menuItems"
            :key="item.id"
            @click="handleMenuClick(item)"
            :class="[
              'w-full text-left px-4 py-3 transition-colors',
              activeTab === item.id
                ? 'bg-orange-50 border-l-4 border-orange-500 text-orange-600 font-medium'
                : item.type === 'logout'
                ? 'text-red-600 hover:bg-red-50'
                : 'text-gray-700 hover:bg-gray-50',
            ]"
          >
            {{ item.label }}
          </button>
        </nav>
      </div>

      <div class="flex-1 p-4 md:p-6">
        <div class="max-w-4xl mx-auto">
          <!-- 个人资料标签 -->
          <div v-if="activeTab === 'profile'" class="bg-white rounded-lg shadow-sm p-6">
            <h2 class="text-2xl font-bold text-gray-900 mb-6">个人资料</h2>

            <div class="flex items-center gap-6 mb-8 pb-8 border-b border-gray-200">
              <div class="relative">
                <div
                  class="w-24 h-24 rounded-full overflow-hidden bg-gray-200 cursor-pointer"
                  @click="showAvatarPreview = true"
                >
                  <img
                    v-if="currentUser?.avatar"
                    :src="currentUser.avatar"
                    alt="Avatar"
                    class="w-full h-full object-cover"
                  />
                  <div v-else class="w-full h-full flex items-center justify-center text-3xl text-gray-500">
                    👤
                  </div>
                </div>
              </div>
              <div>
                <p class="font-medium text-gray-900 mb-3">{{ currentUser?.name || currentUser?.username }}</p>
                <div class="flex items-center gap-3">
                  <label class="px-4 py-2 bg-orange-500 hover:bg-orange-600 text-white text-sm rounded-lg cursor-pointer transition-colors">
                    更换头像
                    <input
                      type="file"
                      accept="image/jpeg,image/png"
                      class="hidden"
                      @change="handleAvatarUpload"
                    />
                  </label>
                  <button
                    v-if="currentUser?.avatar"
                    @click="removeAvatar"
                    class="px-4 py-2 bg-gray-100 hover:bg-gray-200 text-gray-700 text-sm rounded-lg transition-colors"
                  >
                    移除头像
                  </button>
                </div>
              </div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8 pb-8 border-b border-gray-200">
              <div>
                <p class="text-sm text-gray-500 mb-1">用户名</p>
                <p class="text-lg font-medium text-gray-900">{{ currentUser?.username }}</p>
              </div>
              <div>
                <p class="text-sm text-gray-500 mb-1">姓名</p>
                <p class="text-lg font-medium text-gray-900">{{ currentUser?.name || '-' }}</p>
              </div>
              <div>
                <p class="text-sm text-gray-500 mb-1">邮箱地址</p>
                <p class="text-lg font-medium text-gray-900">{{ currentUser?.email || '-' }}</p>
              </div>
              <div>
                <p class="text-sm text-gray-500 mb-1">手机号</p>
                <p class="text-lg font-medium text-gray-900">{{ currentUser?.phone || '-' }}</p>
              </div>
            </div>

            <div class="flex gap-3 mb-8 pb-8 border-b border-gray-200">
              <button
                v-if="!isEditing"
                @click="startEdit"
                class="px-6 py-2 bg-orange-500 hover:bg-orange-600 text-white font-medium rounded-lg transition-colors"
              >
                编辑资料
              </button>
              <div v-else class="space-y-4 w-full max-w-md">
                <input
                  v-model="editForm.name"
                  type="text"
                  placeholder="姓名"
                  class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500"
                />
                <input
                  v-model="editForm.phone"
                  type="tel"
                  placeholder="手机号"
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

            <div>
              <h3 class="text-lg font-semibold text-gray-900 mb-4">账户安全</h3>
              <button
                v-if="!isChangingPassword"
                @click="isChangingPassword = true"
                class="px-6 py-2 border border-gray-300 text-gray-700 font-medium rounded-lg hover:bg-gray-50 transition-colors"
              >
                修改密码
              </button>
              <div v-else class="space-y-4 w-full max-w-md">
                <input
                  v-model="passwordForm.oldPassword"
                  type="password"
                  placeholder="原密码"
                  class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500"
                />
                <input
                  v-model="passwordForm.newPassword"
                  type="password"
                  placeholder="新密码"
                  class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500"
                />
                <input
                  v-model="passwordForm.confirmPassword"
                  type="password"
                  placeholder="确认新密码"
                  class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500"
                />
                <div class="flex gap-3">
                  <button
                    @click="changePassword"
                    class="px-6 py-2 bg-green-500 hover:bg-green-600 text-white font-medium rounded-lg transition-colors"
                  >
                    确认修改
                  </button>
                  <button
                    @click="cancelPasswordChange"
                    class="px-6 py-2 bg-gray-300 hover:bg-gray-400 text-gray-800 font-medium rounded-lg transition-colors"
                  >
                    取消
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- 收货地址标签 -->
          <div v-if="activeTab === 'address'" class="bg-white rounded-lg shadow-sm p-6">
            <div class="flex justify-between items-center mb-6">
              <h2 class="text-2xl font-bold text-gray-900">收货地址</h2>
              <button
                @click="showAddressForm()"
                class="px-4 py-2 bg-orange-500 hover:bg-orange-600 text-white font-medium rounded-lg transition-colors"
              >
                + 添加地址
              </button>
            </div>

            <div v-if="addresses.length > 0" class="space-y-4">
              <div
                v-for="addr in addresses"
                :key="addr.id"
                class="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow"
              >
                <div class="flex justify-between items-start mb-3">
                  <div>
                    <p class="font-medium text-gray-900">{{ addr.receiverName }} {{ addr.receiverPhone }}</p>
                    <p class="text-sm text-gray-600 mt-2">
                      {{ addr.province }} {{ addr.city }} {{ addr.district }} {{ addr.addressDetail }}
                    </p>
                    <p v-if="addr.postalCode" class="text-xs text-gray-500 mt-1">邮编: {{ addr.postalCode }}</p>
                  </div>
                  <span
                    v-if="addr.isDefault"
                    class="px-3 py-1 bg-green-100 text-green-700 text-xs font-medium rounded"
                  >
                    默认
                  </span>
                </div>
                <div class="flex gap-3">
                  <button @click="showAddressForm(addr)" class="text-orange-500 text-sm hover:underline">
                    编辑
                  </button>
                  <button
                    v-if="!addr.isDefault"
                    @click="setDefaultAddress(addr.id)"
                    class="text-blue-500 text-sm hover:underline"
                  >
                    设为默认
                  </button>
                  <button @click="deleteAddress(addr.id)" class="text-red-500 text-sm hover:underline">
                    删除
                  </button>
                </div>
              </div>
            </div>

            <div v-else class="text-center py-12">
              <p class="text-gray-500 text-lg mb-4">暂无收货地址</p>
              <button
                @click="showAddressForm()"
                class="text-orange-500 hover:underline"
              >
                添加地址 →
              </button>
            </div>
          </div>

          <!-- 我的收藏标签 -->
          <FavoritesGrid
            v-if="activeTab === 'favorites'"
            :favorite-products="favoriteProducts"
            @toggle="toggleFavorite"
          />

          <!-- 我的订单标签 -->
          <div v-if="activeTab === 'orders'" class="bg-white rounded-lg shadow-sm p-6">
            <h2 class="text-2xl font-bold text-gray-900 mb-3">我的订单</h2>
            <p class="text-gray-600 mb-6">已统一到订单中心页面展示。</p>
            <NuxtLink
              to="/account/orders"
              class="inline-flex items-center px-4 py-2 bg-orange-500 hover:bg-orange-600 text-white rounded-lg font-medium transition-colors"
            >
              前往订单中心
            </NuxtLink>
          </div>
        </div>
      </div>
    </div>

    <!-- 地址表单弹窗 -->
    <div
      v-if="showAddressModal"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4"
      @click.self="showAddressModal = false"
    >
      <div class="bg-white rounded-lg p-6 max-w-md w-full max-h-[90vh] overflow-y-auto">
        <h3 class="text-xl font-bold mb-4">{{ addressForm.id ? '编辑地址' : '添加地址' }}</h3>
        <div class="space-y-4">
          <input
            v-model="addressForm.receiverName"
            type="text"
            placeholder="收货人姓名"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500"
          />
          <input
            v-model="addressForm.receiverPhone"
            type="tel"
            placeholder="手机号"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500"
          />
          <input
            v-model="addressForm.province"
            type="text"
            placeholder="省"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500"
          />
          <input
            v-model="addressForm.city"
            type="text"
            placeholder="市"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500"
          />
          <input
            v-model="addressForm.district"
            type="text"
            placeholder="区"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500"
          />
          <textarea
            v-model="addressForm.addressDetail"
            placeholder="详细地址"
            rows="3"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500"
          ></textarea>
          <input
            v-model="addressForm.postalCode"
            type="text"
            placeholder="邮编（选填）"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500"
          />
          <label class="flex items-center gap-2">
            <input v-model="addressForm.isDefault" type="checkbox" class="w-4 h-4" />
            <span class="text-sm text-gray-700">设为默认地址</span>
          </label>
          <div class="flex gap-3 pt-4">
            <button
              @click="saveAddress"
              class="flex-1 px-6 py-2 bg-orange-500 hover:bg-orange-600 text-white font-medium rounded-lg transition-colors"
            >
              保存
            </button>
            <button
              @click="showAddressModal = false"
              class="flex-1 px-6 py-2 bg-gray-300 hover:bg-gray-400 text-gray-800 font-medium rounded-lg transition-colors"
            >
              取消
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 头像预览弹窗 -->
    <div
      v-if="showAvatarPreview && currentUser?.avatar"
      class="fixed inset-0 bg-black bg-opacity-75 flex items-center justify-center z-50 p-4"
      @click="showAvatarPreview = false"
    >
      <div class="max-w-2xl max-h-[90vh]">
        <img
          :src="currentUser.avatar"
          alt="Avatar Preview"
          class="max-w-full max-h-full object-contain rounded-lg"
        />
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useProductStore } from '../../stores/product'
import { useUserStore } from '../../stores/user'
import { useAuth } from '../../composables/useAuth'
import { useAddressApi } from '../../composables/useAddressApi'
import { useOrderApi } from '../../composables/useOrderApi'
import { useMessage } from '../../composables/useMessage'

const router = useRouter()
const productStore = useProductStore()
const userStore = useUserStore()
const { success, warning, error: errorMessage } = useMessage()
const { logout: logoutApi } = useAuth()

const activeTab = ref('profile')
const showCart = ref(false)
const isEditing = ref(false)
const isChangingPassword = ref(false)
const showAddressModal = ref(false)
const showAvatarPreview = ref(false)
const loading = ref(false)
const orderStatusFilter = ref<string | null>(null)

const editForm = ref({
  name: '',
  phone: '',
})

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})

const addressForm = ref<any>({
  id: null,
  receiverName: '',
  receiverPhone: '',
  province: '',
  city: '',
  district: '',
  addressDetail: '',
  postalCode: '',
  isDefault: false,
})

const addresses = ref<any[]>([])
const orders = ref<any[]>([])

const menuItems = [
  { id: 'profile', label: '个人资料' },
  { id: 'address', label: '收货地址' },
  { id: 'favorites', label: '我的收藏' },
  { id: 'orders', label: '我的订单' },
  { id: 'logout', label: '退出登录', type: 'logout' },
]

const orderStatusFilters = [
  { value: null, label: '全部订单' },
  { value: 'PENDING_PAYMENT', label: '待支付' },
  { value: 'PENDING_SHIPMENT', label: '待发货' },
  { value: 'SHIPPED', label: '已发货' },
  { value: 'COMPLETED', label: '已完成' },
  { value: 'AFTER_SALE', label: '售后' },
]

const currentUser = computed(() => userStore.user)

const favoriteProducts = computed(() => {
  return productStore.products.filter(p => productStore.favorites.has(p.id))
})

onMounted(async () => {
  if (!userStore.isLoggedIn) {
    try {
      await userStore.loadCurrentUser()
    } catch {
      router.push('/auth/login')
      return
    }
  }

  if (currentUser.value) {
    editForm.value.name = currentUser.value.name || ''
    editForm.value.phone = currentUser.value.phone || ''
  }

  loadAddresses()
})

const startEdit = () => {
  if (currentUser.value) {
    editForm.value.name = currentUser.value.name || ''
    editForm.value.phone = currentUser.value.phone || ''
  }
  isEditing.value = true
}

const saveProfile = async () => {
  try {
    await userStore.updateProfile(editForm.value.name, editForm.value.phone)
    success('资料更新成功')
    isEditing.value = false
  } catch (error: any) {
    errorMessage('更新失败: ' + (error.message || '未知错误'))
  }
}

const changePassword = async () => {
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    warning('两次输入的新密码不一致')
    return
  }

  if (passwordForm.value.newPassword.length < 6) {
    warning('新密码至少6个字符')
    return
  }

  try {
    const { changePassword: changePasswordApi } = useAuth()
    await changePasswordApi(passwordForm.value.oldPassword, passwordForm.value.newPassword)
    success('密码修改成功')
    cancelPasswordChange()
  } catch (error: any) {
    errorMessage('修改失败: ' + (error.message || '未知错误'))
  }
}

const cancelPasswordChange = () => {
  isChangingPassword.value = false
  passwordForm.value = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: '',
  }
}

const handleAvatarUpload = async (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (!file) return

  if (file.size > 1024 * 1024) {
    warning('文件大小不能超过1MB')
    return
  }

  if (!['image/jpeg', 'image/png'].includes(file.type)) {
    warning('仅支持jpg和png格式')
    return
  }

  try {
    const { uploadAvatar } = useAuth()
    const url = await uploadAvatar(file)
    await userStore.updateProfile(currentUser.value?.name, currentUser.value?.phone, url)
    success('头像上传成功')
  } catch (error: any) {
    errorMessage('上传失败: ' + (error.message || '未知错误'))
  }
}

const removeAvatar = async () => {
  if (!confirm('确定要移除头像吗？')) return

  try {
    await userStore.updateProfile(currentUser.value?.name, currentUser.value?.phone, '')
    success('头像已移除')
  } catch (error: any) {
    errorMessage('操作失败: ' + (error.message || '未知错误'))
  }
}

const toggleFavorite = (productId: string) => {
  productStore.toggleFavorite(productId)
}

const loadAddresses = async () => {
  try {
    const { getAddresses } = useAddressApi()
    addresses.value = await getAddresses()
  } catch (error: any) {
    console.error('加载地址失败:', error)
  }
}

const showAddressForm = (addr?: any) => {
  if (addr) {
    addressForm.value = { ...addr }
  } else {
    addressForm.value = {
      id: null,
      receiverName: '',
      receiverPhone: '',
      province: '',
      city: '',
      district: '',
      addressDetail: '',
      postalCode: '',
      isDefault: false,
    }
  }
  showAddressModal.value = true
}

const saveAddress = async () => {
  try {
    const { createAddress, updateAddress } = useAddressApi()

    if (addressForm.value.id) {
      await updateAddress(addressForm.value.id, addressForm.value)
      success('地址更新成功')
    } else {
      await createAddress(addressForm.value)
      success('地址添加成功')
    }

    showAddressModal.value = false
    loadAddresses()
  } catch (error: any) {
    errorMessage('操作失败: ' + (error.message || '未知错误'))
  }
}

const deleteAddress = async (id: string) => {
  if (!confirm('确定要删除此地址吗？')) return

  try {
    const { deleteAddress: deleteAddressApi } = useAddressApi()
    await deleteAddressApi(id)
    success('地址已删除')
    loadAddresses()
  } catch (error: any) {
    errorMessage('删除失败: ' + (error.message || '未知错误'))
  }
}

const setDefaultAddress = async (id: string) => {
  try {
    const { setDefaultAddress: setDefaultAddressApi } = useAddressApi()
    await setDefaultAddressApi(id)
    success('默认地址已设置')
    loadAddresses()
  } catch (error: any) {
    errorMessage('设置失败: ' + (error.message || '未知错误'))
  }
}

watch(
  () => activeTab.value,
  (newTab) => {
    if (newTab === 'orders') {
      loadOrders()
    }
  }
)

watch(orderStatusFilter, () => {
  loadOrders()
})

const loadOrders = async () => {
  loading.value = true
  try {
    const { getUserOrders } = useOrderApi()
    const result = await getUserOrders({
      page: 1,
      pageSize: 20,
      status: orderStatusFilter.value || undefined,
    })
    orders.value = result.list || []
  } catch (error: any) {
    console.error('加载订单失败:', error)
    orders.value = []
  } finally {
    loading.value = false
  }
}

const viewOrderDetail = (orderId: string) => {
  warning('订单详情功能待实现，订单ID: ' + orderId)
}

const getOrderStatusText = (status: string) => {
  const map: Record<string, string> = {
    PENDING_PAYMENT: '待支付',
    PENDING_SHIPMENT: '待发货',
    SHIPPED: '已发货',
    COMPLETED: '已完成',
    CLOSED: '已关闭',
    AFTER_SALE: '售后中',
  }
  return map[status] || status
}

const getOrderStatusClass = (status: string) => {
  const map: Record<string, string> = {
    PENDING_PAYMENT: 'bg-yellow-100 text-yellow-700',
    PENDING_SHIPMENT: 'bg-blue-100 text-blue-700',
    SHIPPED: 'bg-purple-100 text-purple-700',
    COMPLETED: 'bg-green-100 text-green-700',
    CLOSED: 'bg-gray-100 text-gray-700',
    AFTER_SALE: 'bg-red-100 text-red-700',
  }
  return map[status] || 'bg-gray-100 text-gray-700'
}

const handleMenuClick = (item: any) => {
  if (item.type === 'logout') {
    logoutApi()
    userStore.logout()
    success('退出登录成功')
    router.push('/')
  } else if (item.id === 'orders') {
    router.push('/account/orders')
  } else {
    activeTab.value = item.id
  }
}
</script>
