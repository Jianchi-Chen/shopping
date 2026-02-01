# ShopHub 前端项目 - 架构设计文档

## 系统架构概览

```
┌─────────────────────────────────────────────────────────────┐
│                        Vue 3 + TypeScript UI Layer            │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Pages (Nuxt Auto-routing)                           │   │
│  │  ├─ / (首页)                                         │   │
│  │  ├─ /product/[id] (商品详情)                        │   │
│  │  ├─ /checkout/* (结账流程)                          │   │
│  │  ├─ /auth/* (登录/注册)                             │   │
│  │  └─ /user/* (个人中心)                              │   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Components (可复用组件)                             │   │
│  │  ├─ Header (导航 + 搜索 + 用户菜单)                 │   │
│  │  ├─ ProductCard (商品卡片)                           │   │
│  │  ├─ CartPanel (购物车面板)                           │   │
│  │  ├─ BannerCarousel (轮播 + 分类)                    │   │
│  │  ├─ FilterSidebar (分类过滤)                         │   │
│  │  └─ Footer (页脚)                                   │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                   Pinia State Management                      │
│  ┌──────────────────────┐      ┌──────────────────────┐    │
│  │  Product Store       │      │  User Store          │    │
│  │  ├─ products[]       │      │  ├─ user{}           │    │
│  │  ├─ cart[]           │      │  └─ orders[]         │    │
│  │  ├─ favorites{}      │      │                      │    │
│  │  └─ pagination{}     │      └──────────────────────┘    │
│  └──────────────────────┘                                    │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│               Local Storage Persistence                       │
│  ├─ cart (JSON)       - 购物车数据                          │
│  ├─ favorites (JSON)  - 收藏列表                            │
│  ├─ user (JSON)       - 用户信息                            │
│  └─ orders (JSON)     - 订单历史                            │
└─────────────────────────────────────────────────────────────┘
```

## 数据流向

### 场景 1: 浏览和搜索商品

```
用户在搜索框输入
        ↓
Header.vue 的 handleSearch()
        ↓
router.push({ path: '/', query: { search: term } })
        ↓
pages/index.vue 监听 route.query
        ↓
initializeFromQuery() 更新 searchTerm
        ↓
displayedProducts computed 过滤商品
        ↓
ProductCard 列表重新渲染
```

### 场景 2: 加入购物车

```
ProductCard 点击"加购"按钮
        ↓
emit('add-to-cart', product)
        ↓
pages/index.vue handleAddToCart()
        ↓
productStore.addToCart(product, quantity)
        ↓
cart.value 更新 (触发 watch)
        ↓
saveToLocalStorage() 自动执行
        ↓
localStorage 中的 cart 键被更新
        ↓
showMessage 提示用户
```

### 场景 3: 用户登录和订单

```
用户访问 /auth/login
        ↓
输入邮箱和密码，点击登录
        ↓
handleLogin() → userStore.login()
        ↓
user.value 设为已登录状态
        ↓
watch([user, orders]) 触发
        ↓
saveToLocalStorage()
        ↓
localStorage 中 user 和 orders 键被更新
        ↓
router.push('/') 返回首页
```

### 场景 4: 完整购物流程

```
1. 首页浏览和搜索
   └─ productStore.products
   
2. 点击"详情"进入商品详情页
   └─ /product/{id}
   
3. 点击"加入购物车"
   └─ productStore.addToCart()
   └─ productStore.cart (saved to localStorage)
   
4. 点击购物车图标
   └─ /checkout/cart
   └─ 显示 productStore.cart
   
5. 点击"前往结账"
   └─ /checkout/checkout
   └─ 表单填充
   
6. 点击"下一步"
   └─ /checkout/payment
   └─ 选择支付方式
   
7. 点击"提交订单"
   └─ /checkout/order-confirmation
   └─ userStore.addOrder() 保存订单
   └─ productStore.clearCart() 清空购物车
   └─ 两个 store 自动保存到 localStorage
```

## 状态管理设计

### Product Store (`stores/product.ts`)

#### 状态
```typescript
// 商品相关
products: Product[] // 所有商品列表
cart: CartItem[]    // 购物车数据
favorites: Set<string> // 收藏 ID 集合

// 分页相关
displayedPage: number // 当前页码
itemsPerPage: 12 // 每页商品数

// 加载状态
isLoading: boolean
```

#### 方法

**查询方法**
- `getProductById(id)` - 获取单个商品
- `totalPages` (computed) - 总页数
- `cartTotal` (computed) - 购物车总价
- `cartCount` (computed) - 购物车商品数量
- `currentPage` (computed) - 当前页码（用于模板）

**操作方法**
- `addToCart(product, quantity)` - 添加到购物车
- `removeFromCart(productId)` - 从购物车删除
- `updateCartQuantity(productId, quantity)` - 更新数量
- `toggleFavorite(productId)` - 切换收藏
- `clearCart()` - 清空购物车
- `goToPage(page)` - 跳转到指定页

**数据持久化**
- `saveToLocalStorage()` - 保存到本地存储
- `loadFromLocalStorage()` - 从本地存储恢复

### User Store (`stores/user.ts`)

#### 状态
```typescript
user: User | null    // 当前登录用户
orders: Order[]      // 用户订单列表
isLoading: boolean   // 加载状态
```

#### 方法

**认证方法**
- `login(email, password)` - 登录（异步）
- `register(name, email, password)` - 注册（异步）
- `logout()` - 登出

**订单管理**
- `addOrder(order)` - 添加订单
- `updateProfile(name, phone)` - 更新用户信息

**计算属性**
- `isLoggedIn` (computed) - 是否已登录

**数据持久化**
- `saveToLocalStorage()` - 保存到本地存储
- `loadFromLocalStorage()` - 从本地存储恢复

## 组件通信模式

### Props + Emits 模式

```vue
<!-- 父组件 -->
<ProductCard
  :product="product"
  @add-to-cart="handleAddToCart"
  @toggle-favorite="handleToggleFavorite"
/>

<!-- 子组件 -->
<script setup>
defineProps<{ product: Product }>()
defineEmits<{
  'add-to-cart': [product: Product]
  'toggle-favorite': [productId: string]
}>()
</script>
```

### Pinia Store 模式

```typescript
// 在组件中直接使用
const productStore = useProductStore()
const cart = computed(() => productStore.cart)

// 调用方法
productStore.addToCart(product, quantity)
```

### 路由参数模式

```typescript
// 通过 URL 查询参数传递过滤条件
router.push({
  path: '/',
  query: { search: term, category: categoryId }
})

// 在接收页面读取参数
const route = useRoute()
const searchTerm = ref(route.query.search)
```

## localStorage 持久化策略

### 数据结构

```javascript
// cart - 仅存储 productId 和 quantity
{
  "cart": [
    { "productId": "1", "quantity": 2 },
    { "productId": "3", "quantity": 1 }
  ]
}

// favorites - 字符串数组
{
  "favorites": ["1", "3", "5"]
}

// user - 用户对象
{
  "user": {
    "id": "user_xxx",
    "name": "张三",
    "email": "zhangsan@example.com",
    "isLoggedIn": true
  }
}

// orders - 订单数组
{
  "orders": [
    {
      "id": "ORD-xxx",
      "items": [{ "product": {...}, "quantity": 2 }],
      "total": 299.99,
      "status": "confirmed"
    }
  ]
}
```

### 保存触发机制

```typescript
// 使用 watch 自动保存
watch([cart, favorites], () => {
  saveToLocalStorage()
}, { deep: true })  // 深度监听检测嵌套变化

watch([user, orders], () => {
  saveToLocalStorage()
}, { deep: true })
```

### 恢复时机

```typescript
// 1. 页面加载时
onMounted(() => {
  productStore.initializeProducts()
  loadFromLocalStorage()
})

// 2. 每次 store 创建时都会调用 loadFromLocalStorage
// （在 initializeProducts 中调用）
```

## 类型系统设计

### 核心接口 (`types/product.ts`)

```typescript
// 商品接口
interface Product {
  id: string
  title: string
  price: number
  originalPrice?: number
  rating: number
  reviewCount: number
  image: string
  category: string
  badge?: string
  isFavorite: boolean
  description?: string
  details?: string
  stock?: number
}

// 购物车项
interface CartItem {
  product: Product
  quantity: number
}

// 用户信息
interface User {
  id: string
  name: string
  email: string
  phone?: string
  avatar?: string
  isLoggedIn: boolean
}

// 订单相关
interface OrderItem {
  product: Product
  quantity: number
  subtotal: number
}

interface Order {
  id: string
  items: OrderItem[]
  total: number
  status: 'confirmed' | 'shipped' | 'delivered' | 'cancelled'
  createdAt: string
  shippingAddress: string
  paymentMethod: string
}
```

## 路由设计

### 路由层级结构

```
/ (首页)
├── /product/[id] (商品详情 - 动态路由)
├── /checkout (结账流程)
│   ├── /checkout/cart (购物车)
│   ├── /checkout/checkout (订单确认)
│   ├── /checkout/payment (支付选择)
│   └── /checkout/order-confirmation (订单成功)
├── /auth (认证)
│   ├── /auth/login (登录)
│   └── /auth/register (注册)
└── /user (用户中心)
    ├── /user/profile (个人资料)
    └── /user/orders (订单历史)
```

### 路由保护

```typescript
// 个人中心页面受保护
onMounted(() => {
  if (!userStore.isLoggedIn) {
    router.push('/auth/login')
  }
})
```

## 响应式设计

### Tailwind 断点

```
默认 (mobile):   < 640px
sm:   640px
md:   768px
lg:   1024px
xl:   1280px
2xl:  1536px
```

### 响应式栅格

```vue
<!-- 商品列表 -->
<div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
  <!-- 手机 1列, 平板 2列, 桌面 3-4列 -->
</div>
```

## 性能考虑

### 1. 分页而非无限滚动
- 每页 12 件商品，避免 DOM 树过大
- 分页 UI 清晰易用

### 2. 计算属性缓存
```typescript
// 只在依赖变化时重新计算
const displayedProducts = computed(() => {
  // 过滤和排序逻辑
})
```

### 3. 条件渲染 (v-if)
```vue
<!-- 购物车面板只在需要时渲染 -->
<CartPanel v-if="showCart" @close="showCart = false" />
```

### 4. 本地存储避免网络请求
- Mock 数据存储在内存中
- 用户状态在 localStorage 中
- 无需后端 API 调用

## 错误处理

### 类型检查
```typescript
// typeof window !== 'undefined' 检查浏览器环境
const saveToLocalStorage = () => {
  if (typeof window !== 'undefined') {
    localStorage.setItem('key', JSON.stringify(data))
  }
}
```

### 数据恢复
```typescript
// 解析失败时使用默认值
try {
  cart.value = JSON.parse(cartData)
} catch (e) {
  console.error('Failed to load cart', e)
  // cart 保持为空数组
}
```

### 表单验证
```typescript
// 提交前验证
const validateForm = (): boolean => {
  errors.email = ''
  if (!form.email) {
    errors.email = '请输入邮箱'
    return false
  }
  return true
}
```

## 代码组织原则

### 单一职责
- 每个组件专注于一个功能
- Store 分离：product vs user

### 可复用性
- 通用组件放在 `components/` 目录
- 工具函数放在 `utils/` 目录
- 共享类型放在 `types/` 目录

### 可维护性
- 清晰的命名约定
- 充分的注释和文档
- 一致的代码风格

## 扩展路径

### 1. 接入真实后端
```typescript
// 替换 getMockProducts() 为 API 调用
const initializeProducts = async () => {
  const response = await fetch('/api/products')
  products.value = await response.json()
}
```

### 2. 添加用户认证
```typescript
// 使用 JWT 令牌
const login = async (email: string, password: string) => {
  const token = await apiLogin(email, password)
  localStorage.setItem('token', token)
}
```

### 3. 实现真实支付
```typescript
// 集成支付网关 SDK
const submitOrder = async (order: Order) => {
  const paymentResult = await stripe.confirmPayment(order)
  if (paymentResult.success) {
    userStore.addOrder(order)
  }
}
```

### 4. 性能优化
- 图片懒加载
- 代码分割 (Nuxt 自动)
- 虚拟滚动（大列表）
- CDN 部署

---

**文档版本**: 1.0
**最后更新**: 2024
**架构设计者**: AI Assistant
