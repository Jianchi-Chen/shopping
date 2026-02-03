# 使用示例与集成指南

## 前端集成示例

### Vue 3 / Nuxt 集成

#### 1. API 配置

```typescript
// composables/useApi.ts
export const useApi = () => {
  const config = useRuntimeConfig()
  const baseURL = config.public.apiBase || 'http://localhost:8080/api'
  
  const getToken = () => {
    return localStorage.getItem('token')
  }
  
  const request = async <T>(url: string, options: RequestInit = {}): Promise<T> => {
    const token = getToken()
    
    const response = await fetch(`${baseURL}${url}`, {
      ...options,
      headers: {
        'Content-Type': 'application/json',
        ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
        ...options.headers,
      },
    })
    
    const data = await response.json()
    
    if (data.code !== 200) {
      throw new Error(data.message || 'Request failed')
    }
    
    return data.result
  }
  
  return {
    get: <T>(url: string) => request<T>(url, { method: 'GET' }),
    post: <T>(url: string, body: any) => 
      request<T>(url, { method: 'POST', body: JSON.stringify(body) }),
  }
}
```

#### 2. 登录示例

```vue
<!-- pages/auth/login.vue -->
<script setup lang="ts">
const api = useApi()
const router = useRouter()

const form = reactive({
  username: 'admin',
  password: 'admin123'
})

const handleLogin = async () => {
  try {
    const result = await api.post<{ token: string }>('/auth/login', form)
    localStorage.setItem('token', result.token)
    
    // 解析 token 获取用户信息（可选）
    const payload = JSON.parse(atob(result.token.split('.')[1]))
    localStorage.setItem('role', payload.role)
    
    router.push('/dashboard')
  } catch (error) {
    console.error('Login failed:', error)
  }
}
</script>

<template>
  <form @submit.prevent="handleLogin">
    <input v-model="form.username" placeholder="Username" />
    <input v-model="form.password" type="password" placeholder="Password" />
    <button type="submit">Login</button>
  </form>
</template>
```

#### 3. 商品列表示例

```vue
<!-- pages/products/index.vue -->
<script setup lang="ts">
interface Product {
  id: string
  title: string
  price: number
  stock: number
  status: string
}

interface PageResponse<T> {
  page: number
  pageSize: number
  pageCount: number
  itemCount: number
  list: T[]
}

const api = useApi()
const products = ref<Product[]>([])
const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const fetchProducts = async () => {
  const result = await api.get<PageResponse<Product>>(
    `/commerce/products?page=${pagination.page}&pageSize=${pagination.pageSize}`
  )
  
  products.value = result.list
  pagination.total = result.itemCount
}

const updateStatus = async (id: string, status: string) => {
  await api.post('/commerce/products/status', { id, status })
  await fetchProducts()
}

onMounted(() => {
  fetchProducts()
})
</script>

<template>
  <div>
    <h1>Products</h1>
    <table>
      <thead>
        <tr>
          <th>Title</th>
          <th>Price</th>
          <th>Stock</th>
          <th>Status</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="product in products" :key="product.id">
          <td>{{ product.title }}</td>
          <td>{{ product.price }}</td>
          <td>{{ product.stock }}</td>
          <td>{{ product.status }}</td>
          <td>
            <button @click="updateStatus(product.id, 'OFF_SALE')">
              下架
            </button>
          </td>
        </tr>
      </tbody>
    </table>
    
    <!-- 分页组件 -->
    <div>
      <button @click="pagination.page--" :disabled="pagination.page === 1">
        Previous
      </button>
      <span>{{ pagination.page }} / {{ Math.ceil(pagination.total / pagination.pageSize) }}</span>
      <button @click="pagination.page++" :disabled="pagination.page * pagination.pageSize >= pagination.total">
        Next
      </button>
    </div>
  </div>
</template>
```

### React / Next.js 集成

#### 1. API 工具类

```typescript
// lib/api.ts
const API_BASE = process.env.NEXT_PUBLIC_API_BASE || 'http://localhost:8080/api'

interface ApiResponse<T> {
  code: number
  result: T
  message?: string
  type?: string
}

class ApiClient {
  private getToken(): string | null {
    return localStorage.getItem('token')
  }

  private async request<T>(url: string, options: RequestInit = {}): Promise<T> {
    const token = this.getToken()
    
    const response = await fetch(`${API_BASE}${url}`, {
      ...options,
      headers: {
        'Content-Type': 'application/json',
        ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
        ...options.headers,
      },
    })
    
    const data: ApiResponse<T> = await response.json()
    
    if (data.code !== 200) {
      throw new Error(data.message || 'Request failed')
    }
    
    return data.result
  }

  async get<T>(url: string): Promise<T> {
    return this.request<T>(url, { method: 'GET' })
  }

  async post<T>(url: string, body: any): Promise<T> {
    return this.request<T>(url, {
      method: 'POST',
      body: JSON.stringify(body),
    })
  }
}

export const api = new ApiClient()
```

#### 2. 使用 SWR 获取数据

```typescript
// hooks/useProducts.ts
import useSWR from 'swr'
import { api } from '@/lib/api'

interface Product {
  id: string
  title: string
  price: number
  stock: number
  status: string
}

interface PageResponse<T> {
  list: T[]
  page: number
  pageSize: number
  itemCount: number
}

export function useProducts(page: number = 1, pageSize: number = 10) {
  const { data, error, mutate } = useSWR<PageResponse<Product>>(
    `/commerce/products?page=${page}&pageSize=${pageSize}`,
    (url) => api.get(url)
  )

  return {
    products: data?.list || [],
    pagination: {
      page: data?.page || 1,
      pageSize: data?.pageSize || 10,
      total: data?.itemCount || 0,
    },
    isLoading: !error && !data,
    isError: error,
    mutate,
  }
}
```

#### 3. 组件使用

```tsx
// app/products/page.tsx
'use client'

import { useProducts } from '@/hooks/useProducts'
import { api } from '@/lib/api'

export default function ProductsPage() {
  const { products, pagination, isLoading, mutate } = useProducts()

  const handleUpdateStatus = async (id: string, status: string) => {
    try {
      await api.post('/commerce/products/status', { id, status })
      mutate() // 重新获取数据
    } catch (error) {
      console.error('Update failed:', error)
    }
  }

  if (isLoading) return <div>Loading...</div>

  return (
    <div>
      <h1>Products</h1>
      <table>
        <thead>
          <tr>
            <th>Title</th>
            <th>Price</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {products.map((product) => (
            <tr key={product.id}>
              <td>{product.title}</td>
              <td>${product.price}</td>
              <td>{product.status}</td>
              <td>
                <button onClick={() => handleUpdateStatus(product.id, 'OFF_SALE')}>
                  下架
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
```

## Axios 集成

```typescript
// api/client.ts
import axios from 'axios'

const client = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000,
})

// 请求拦截器
client.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器
client.interceptors.response.use(
  (response) => {
    const { data } = response
    if (data.code !== 200) {
      return Promise.reject(new Error(data.message))
    }
    return data.result
  },
  (error) => {
    if (error.response?.status === 401) {
      // Token 过期，跳转到登录页
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default client

// 使用示例
import client from './client'

// 获取商品列表
const products = await client.get('/commerce/products', {
  params: { page: 1, pageSize: 10 }
})

// 更新状态
await client.post('/commerce/products/status', {
  id: 'xxx',
  status: 'OFF_SALE'
})
```

## Postman 使用

### 1. 环境变量设置

创建环境变量：
- `base_url`: `http://localhost:8080/api`
- `token`: (登录后自动设置)

### 2. Pre-request Script（登录）

```javascript
// 在登录接口的 Tests 标签页添加：
if (pm.response.code === 200) {
    const jsonData = pm.response.json();
    pm.environment.set("token", jsonData.result.token);
}
```

### 3. Authorization

在其他接口的 Authorization 标签中：
- Type: Bearer Token
- Token: `{{token}}`

## 前端路由守卫示例

### Vue Router

```typescript
// router/index.ts
import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', component: () => import('@/pages/auth/login.vue') },
    {
      path: '/dashboard',
      component: () => import('@/pages/dashboard.vue'),
      meta: { requiresAuth: true }
    },
  ],
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
```

### Next.js Middleware

```typescript
// middleware.ts
import { NextResponse } from 'next/server'
import type { NextRequest } from 'next/server'

export function middleware(request: NextRequest) {
  const token = request.cookies.get('token')?.value
  
  if (!token) {
    return NextResponse.redirect(new URL('/login', request.url))
  }
  
  return NextResponse.next()
}

export const config = {
  matcher: ['/dashboard/:path*', '/products/:path*'],
}
```

## 错误处理最佳实践

```typescript
// utils/errorHandler.ts
export class ApiError extends Error {
  constructor(public code: number, message: string) {
    super(message)
    this.name = 'ApiError'
  }
}

export async function handleApiCall<T>(
  apiCall: () => Promise<T>,
  errorCallback?: (error: ApiError) => void
): Promise<T | null> {
  try {
    return await apiCall()
  } catch (error) {
    if (error instanceof ApiError) {
      console.error(`API Error [${error.code}]: ${error.message}`)
      errorCallback?.(error)
    } else {
      console.error('Unexpected error:', error)
    }
    return null
  }
}

// 使用示例
const products = await handleApiCall(
  () => api.get<Product[]>('/commerce/products'),
  (error) => {
    if (error.code === 401) {
      router.push('/login')
    } else {
      toast.error(error.message)
    }
  }
)
```

## Token 刷新策略

```typescript
// utils/tokenRefresh.ts
let refreshing = false
let refreshSubscribers: Array<(token: string) => void> = []

function subscribeTokenRefresh(callback: (token: string) => void) {
  refreshSubscribers.push(callback)
}

function onRefreshed(token: string) {
  refreshSubscribers.forEach((callback) => callback(token))
  refreshSubscribers = []
}

export async function refreshToken(): Promise<string> {
  if (refreshing) {
    return new Promise((resolve) => {
      subscribeTokenRefresh((token: string) => {
        resolve(token)
      })
    })
  }

  refreshing = true

  try {
    // 假设有 refresh token 接口
    const newToken = await api.post<string>('/auth/refresh', {
      refreshToken: localStorage.getItem('refreshToken'),
    })

    localStorage.setItem('token', newToken)
    onRefreshed(newToken)
    refreshing = false

    return newToken
  } catch (error) {
    refreshing = false
    // 刷新失败，跳转登录
    localStorage.removeItem('token')
    window.location.href = '/login'
    throw error
  }
}
```

## 测试示例

### Jest 单元测试

```typescript
// __tests__/api.test.ts
import { api } from '@/lib/api'

// Mock fetch
global.fetch = jest.fn()

describe('API Client', () => {
  beforeEach(() => {
    jest.clearAllMocks()
  })

  it('should login successfully', async () => {
    const mockResponse = {
      code: 200,
      result: { token: 'test-token' },
    }

    ;(fetch as jest.Mock).mockResolvedValueOnce({
      json: async () => mockResponse,
    })

    const result = await api.post('/auth/login', {
      username: 'admin',
      password: 'admin123',
    })

    expect(result).toEqual({ token: 'test-token' })
  })

  it('should handle API errors', async () => {
    const mockResponse = {
      code: 400,
      result: null,
      message: 'Bad request',
    }

    ;(fetch as jest.Mock).mockResolvedValueOnce({
      json: async () => mockResponse,
    })

    await expect(
      api.post('/auth/login', { username: '', password: '' })
    ).rejects.toThrow('Bad request')
  })
})
```

## 性能优化建议

1. **缓存策略**: 使用 SWR 或 React Query 缓存数据
2. **防抖节流**: 搜索框使用防抖
3. **虚拟滚动**: 长列表使用虚拟滚动
4. **懒加载**: 图片和组件懒加载
5. **批量请求**: 合并多个请求

```typescript
// 防抖示例
import { debounce } from 'lodash-es'

const searchProducts = debounce(async (keyword: string) => {
  const results = await api.get(`/commerce/products?keyword=${keyword}`)
  // 更新搜索结果
}, 300)
```

希望这些示例能帮助你快速集成后端 API！
