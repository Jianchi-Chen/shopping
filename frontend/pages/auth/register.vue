<template>
  <div class="min-h-screen bg-gradient-to-br from-orange-50 to-orange-100 flex flex-col">
    <Header />

    <!-- 注册容器 -->
    <div class="flex-1 flex items-center justify-center px-4 py-12">
      <div class="w-full max-w-md">
        <!-- 标题 -->
        <div class="text-center mb-8">
          <h1 class="text-3xl font-bold text-gray-900 mb-2">创建账户</h1>
          <p class="text-gray-600">加入 ShopHub，开始购物</p>
        </div>

        <!-- 注册表单 -->
        <form @submit.prevent="handleRegister" class="bg-white rounded-lg shadow-md p-8 space-y-5">
          <!-- 用户名输入 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">用户名</label>
            <input
              v-model="form.name"
              type="text"
              placeholder="请输入用户名"
              required
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500 focus:border-transparent transition-all"
            />
            <p v-if="errors.name" class="text-red-500 text-xs mt-1">{{ errors.name }}</p>
          </div>

          <!-- 邮箱输入 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">电子邮箱</label>
            <input
              v-model="form.email"
              type="email"
              placeholder="请输入邮箱地址"
              required
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500 focus:border-transparent transition-all"
            />
            <p v-if="errors.email" class="text-red-500 text-xs mt-1">{{ errors.email }}</p>
          </div>

          <!-- 密码输入 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">密码</label>
            <input
              v-model="form.password"
              type="password"
              placeholder="请输入密码（至少6个字符）"
              required
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500 focus:border-transparent transition-all"
            />
            <p v-if="errors.password" class="text-red-500 text-xs mt-1">{{ errors.password }}</p>
            <!-- 密码强度指示 -->
            <div class="mt-2 flex gap-1">
              <div
                v-for="i in 3"
                :key="i"
                class="h-1 flex-1 rounded"
                :class="passwordStrength >= i * 33 ? 'bg-orange-500' : 'bg-gray-300'"
              ></div>
            </div>
          </div>

          <!-- 确认密码 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">确认密码</label>
            <input
              v-model="form.confirmPassword"
              type="password"
              placeholder="请再次输入密码"
              required
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500 focus:border-transparent transition-all"
            />
            <p v-if="errors.confirmPassword" class="text-red-500 text-xs mt-1">{{ errors.confirmPassword }}</p>
          </div>

          <!-- 服务条款 -->
          <div class="flex items-start">
            <input v-model="agreeTerms" type="checkbox" id="terms" class="mt-1 rounded" />
            <label for="terms" class="ml-2 text-sm text-gray-600">
              我同意<a href="#" class="text-orange-500 hover:underline">服务条款</a>和<a href="#" class="text-orange-500 hover:underline">隐私政策</a>
            </label>
          </div>
          <p v-if="errors.terms" class="text-red-500 text-xs">{{ errors.terms }}</p>

          <!-- 提交按钮 -->
          <button
            type="submit"
            :disabled="isLoading"
            class="w-full bg-orange-500 hover:bg-orange-600 disabled:opacity-50 disabled:cursor-not-allowed text-white font-bold py-2 px-4 rounded-lg transition-colors"
          >
            {{ isLoading ? '创建中...' : '创建账户' }}
          </button>

          <!-- 错误提示 -->
          <div v-if="errors.submit" class="bg-red-50 border border-red-200 rounded-lg p-3">
            <p class="text-red-700 text-sm">{{ errors.submit }}</p>
          </div>
        </form>

        <!-- 登录链接 -->
        <div class="text-center mt-6">
          <p class="text-gray-600">已有账户？<NuxtLink to="/auth/login" class="text-orange-500 hover:underline font-medium">直接登录</NuxtLink></p>
        </div>

        <!-- 底部链接 -->
        <div class="flex justify-center gap-4 mt-8 text-xs text-gray-500">
          <NuxtLink to="/" class="hover:text-gray-700">返回首页</NuxtLink>
        </div>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'

const router = useRouter()
const userStore = useUserStore()

const form = reactive({
  name: '',
  email: '',
  password: '',
  confirmPassword: '',
})

const errors = reactive({
  name: '',
  email: '',
  password: '',
  confirmPassword: '',
  terms: '',
  submit: '',
})

const agreeTerms = ref(false)
const isLoading = ref(false)

// 密码强度计算
const passwordStrength = computed(() => {
  const pwd = form.password
  let strength = 0

  if (pwd.length >= 6) strength += 33
  if (/[a-z]/.test(pwd) && /[A-Z]/.test(pwd)) strength += 33
  if (/\d/.test(pwd) && /[!@#$%^&*]/.test(pwd)) strength += 34

  return strength
})

const validateForm = (): boolean => {
  errors.name = ''
  errors.email = ''
  errors.password = ''
  errors.confirmPassword = ''
  errors.terms = ''
  errors.submit = ''

  if (!form.name) {
    errors.name = '请输入用户名'
    return false
  }

  if (form.name.length < 2) {
    errors.name = '用户名至少2个字符'
    return false
  }

  if (!form.email) {
    errors.email = '请输入邮箱地址'
    return false
  }

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(form.email)) {
    errors.email = '邮箱格式不正确'
    return false
  }

  if (!form.password) {
    errors.password = '请输入密码'
    return false
  }

  if (form.password.length < 6) {
    errors.password = '密码至少6个字符'
    return false
  }

  if (form.password !== form.confirmPassword) {
    errors.confirmPassword = '两次输入的密码不一致'
    return false
  }

  if (!agreeTerms.value) {
    errors.terms = '请同意服务条款和隐私政策'
    return false
  }

  return true
}

const handleRegister = async () => {
  if (!validateForm()) {
    return
  }

  isLoading.value = true

  // 模拟注册延迟
  await new Promise(resolve => setTimeout(resolve, 500))

  await userStore.register(form.name, form.email, form.password)

  // 注册成功，跳转到首页
  router.push('/')
}
</script>
