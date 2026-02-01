<template>
  <div class="min-h-screen bg-gradient-to-br from-orange-50 to-orange-100 flex flex-col">
    <Header />

    <!-- 登录容器 -->
    <div class="flex-1 flex items-center justify-center px-4 py-12">
      <div class="w-full max-w-md">
        <!-- 标题 -->
        <div class="text-center mb-8">
          <h1 class="text-3xl font-bold text-gray-900 mb-2">登录账户</h1>
          <p class="text-gray-600">继续购物或管理订单</p>
        </div>

        <!-- 登录表单 -->
        <form @submit.prevent="handleLogin" class="bg-white rounded-lg shadow-md p-8 space-y-6">
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
              placeholder="请输入密码"
              required
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500 focus:border-transparent transition-all"
            />
            <p v-if="errors.password" class="text-red-500 text-xs mt-1">{{ errors.password }}</p>
          </div>

          <!-- 记住密码 -->
          <div class="flex items-center">
            <input v-model="rememberMe" type="checkbox" id="remember" class="rounded" />
            <label for="remember" class="ml-2 text-sm text-gray-600 cursor-pointer">记住我的账号</label>
          </div>

          <!-- 提交按钮 -->
          <button
            type="submit"
            :disabled="isLoading"
            class="w-full bg-orange-500 hover:bg-orange-600 disabled:opacity-50 disabled:cursor-not-allowed text-white font-bold py-2 px-4 rounded-lg transition-colors"
          >
            {{ isLoading ? '登录中...' : '登录' }}
          </button>

          <!-- 错误提示 -->
          <div v-if="errors.submit" class="bg-red-50 border border-red-200 rounded-lg p-3">
            <p class="text-red-700 text-sm">{{ errors.submit }}</p>
          </div>
        </form>

        <!-- 注册链接 -->
        <div class="text-center mt-6">
          <p class="text-gray-600">还没有账户？<NuxtLink to="/auth/register" class="text-orange-500 hover:underline font-medium">创建新账户</NuxtLink></p>
        </div>

        <!-- 演示账户 -->
        <div class="mt-8 bg-blue-50 border border-blue-200 rounded-lg p-4">
          <p class="text-sm text-blue-700 font-semibold mb-2">📝 演示账户</p>
          <p class="text-xs text-blue-600 mb-2">邮箱：<span class="font-mono">demo@example.com</span></p>
          <p class="text-xs text-blue-600">密码：<span class="font-mono">123456</span></p>
        </div>

        <!-- 底部链接 -->
        <div class="flex justify-center gap-4 mt-8 text-xs text-gray-500">
          <NuxtLink to="/" class="hover:text-gray-700">返回首页</NuxtLink>
          <span>|</span>
          <a href="#" class="hover:text-gray-700">忘记密码？</a>
        </div>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'

const router = useRouter()
const userStore = useUserStore()

const form = reactive({
  email: '',
  password: '',
})

const errors = reactive({
  email: '',
  password: '',
  submit: '',
})

const rememberMe = ref(false)
const isLoading = ref(false)

const validateForm = (): boolean => {
  errors.email = ''
  errors.password = ''
  errors.submit = ''

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

  return true
}

const handleLogin = async () => {
  if (!validateForm()) {
    return
  }

  isLoading.value = true

  // 模拟登录延迟
  await new Promise(resolve => setTimeout(resolve, 500))

  await userStore.login(form.email, form.password)

  // 登录成功，跳转到首页
  router.push('/')
}
</script>
