# ShopHub 前端项目开发指南

## 项目概览

**ShopHub** 是一个基于 **Nuxt 4 + Vue 3 + TypeScript** 的在线购物前端展示项目（仅前端，无真实后端）。使用 Tailwind CSS 实现响应式设计，Pinia 管理全局状态，Mock 数据模拟商品列表。

## 核心架构

### 技术栈
- **框架**: Nuxt 4.3.0 (SPA 模式, `ssr: false`)
- **UI**: Vue 3.5.27 + TypeScript
- **样式**: Tailwind CSS 3.4.0 + PostCSS
- **状态管理**: Pinia 2.2.0 (Composition API 风格)
- **组件UI库**: Naive UI 2.35.0 (已安装但未大量使用)

### 项目结构与数据流

```
pages/index.vue (主页面)
  ├─ Header (顶部导航 + 购物车计数)
  ├─ CartPanel (购物车面板 - 条件渲染)
  ├─ BannerCarousel (轮播图)
  ├─ FilterSidebar (分类过滤)
  ├─ 主区域
  │  ├─ 搜索框 + 排序下拉框
  │  └─ ProductCard 列表 (响应式网格)
  └─ Footer

状态流: useProductStore (Pinia) ← products, cart, favorites
       ↓
       displayedProducts (computed: 搜索→分类过滤→排序)
```

### 关键数据模型

**Product** (types/product.ts):
- `id`, `title`, `price`, `originalPrice?`
- `rating`, `reviewCount`, `image`, `category`
- `badge?` (如"热销"、"新品"), `isFavorite`

**CartItem**: `{ product: Product, quantity: number }`

**ProductListQuery**: 用于搜索/过滤参数（当前未全量使用）

## 开发工作流

### 启动项目
```bash
pnpm install        # 首次安装依赖
pnpm dev           # 开发模式 → http://localhost:3000
pnpm build         # 生产构建
pnpm preview       # 预览生产构建
pnpm generate      # 静态生成（目前不常用）
```

### Nuxt 配置细节
- **自动路由**: `pages/` 中的 `.vue` 文件自动生成路由
- **全局组件注册**: `components/` 中的组件无需 import 即可使用
- **Pinia 存储目录**: `stores/` (通过 `@pinia/nuxt` 模块)
- **启用 DevTools**: `devtools: { enabled: true }`
- **Tailwind 样式**: 全局导入 `assets/css/main.css`

## 设计模式与约定

### 1. 状态管理 (Pinia Store)

**stores/product.ts** 使用 Composition API 风格:
```typescript
export const useProductStore = defineStore('product', () => {
  // 状态
  const products = ref<Product[]>([])
  const cart = ref<CartItem[]>([])
  const favorites = ref<Set<string>>(new Set())
  
  // Action (同步修改状态)
  const addToCart = (product: Product, quantity: number = 1) => { ... }
  const toggleFavorite = (productId: string) => { ... }
  
  // Getter (computed)
  const cartTotal = computed(() => { ... })
  
  return { products, cart, favorites, addToCart, ... }
})
```

**关键点**:
- 不用 mutations，直接在 actions 中修改 ref
- `favorites` 使用 `Set<string>` 存储 ID，同时在 Product 对象中维护 `isFavorite`
- 状态初始化: `initializeProducts()` 模拟 500ms API 延迟

### 2. 组件通信模式

**父 → 子**: Props (类型安全)
```typescript
// ProductCard.vue
const props = defineProps<{ product: Product }>()
```

**子 → 父**: Emits
```typescript
// Header.vue
defineEmits<{ 'cart-click': [] }>()

// ProductCard.vue
emit('add-to-cart', product)
emit('toggle-favorite')
```

**全局状态**: 直接导入 `useProductStore()`（首选）

### 3. 搜索与排序逻辑

**实现位置**: `utils/mockData.ts`
- `searchProducts(products, term)` - 按 title 模糊匹配
- `sortProducts(products, sortBy)` - 按相关性/价格/评分排序

**应用位置**: `pages/index.vue` 的 `displayedProducts` computed:
```typescript
const displayedProducts = computed(() => {
  let result = productStore.products
  if (selectedCategory.value) {
    result = result.filter(p => p.category === selectedCategory.value)
  }
  if (searchTerm.value.trim()) {
    result = searchProducts(result, searchTerm.value)
  }
  return sortProducts(result, sortBy.value)
})
```

### 4. 样式约定

- **颜色主题**: 橙色为主（`orange-500`, `orange-600`）
- **间距**: 使用 Tailwind 的标准间距类（`px-4`, `py-2`, `gap-4`）
- **响应式**: 
  - 移动端优先（默认 1 列）
  - `sm:` (640px) → 2 列
  - `lg:` (1024px) → 3 列
  - `xl:` (1280px) → 4 列
- **影子效果**: `shadow-sm`, `shadow-md`, `hover:shadow-md`
- **动画**: `transition-*`, `hover:scale-105`, `group-hover:*`

## 关键文件与职责

| 文件 | 目的 |
|------|------|
| [pages/index.vue](pages/index.vue) | 主页面：页面布局、状态管理调用、搜索/排序逻辑应用 |
| [stores/product.ts](stores/product.ts) | Pinia store：全局商品、购物车、收藏状态 |
| [components/Header.vue](components/Header.vue) | 顶部导航：logo、搜索框（未实现）、购物车按钮 |
| [components/ProductCard.vue](components/ProductCard.vue) | 商品卡片：图片、评分、价格、收藏/加购按钮 |
| [components/CartPanel.vue](components/CartPanel.vue) | 购物车面板：列表、数量调整、删除、结算 |
| [components/FilterSidebar.vue](components/FilterSidebar.vue) | 分类过滤侧边栏 |
| [components/BannerCarousel.vue](components/BannerCarousel.vue) | 轮播图 |
| [utils/mockData.ts](utils/mockData.ts) | Mock 数据：`getMockProducts()`, `searchProducts()`, `sortProducts()` |
| [utils/helpers.ts](utils/helpers.ts) | 工具函数：`formatPrice()`, `debounce()`, `throttle()` 等 |
| [types/product.ts](types/product.ts) | 类型定义：`Product`, `CartItem`, `Category`, `ProductListQuery` |

## 常见开发任务

### 添加新商品字段
1. 更新 [types/product.ts](types/product.ts) 的 `Product` 接口
2. 更新 [utils/mockData.ts](utils/mockData.ts) 中的 mock 数据
3. 在相关组件中使用（如 `ProductCard.vue`）

### 修改购物车逻辑
- 修改 [stores/product.ts](stores/product.ts) 的 `addToCart()`, `removeFromCart()` 等方法
- 更新 [components/CartPanel.vue](components/CartPanel.vue) 的显示

### 调整响应式布局
- 编辑相关 `.vue` 文件的 template 中的 Tailwind 类
- 常见断点: `sm:`, `md:`, `lg:`, `xl:`

### 添加新功能（如筛选、排序）
1. 在 [pages/index.vue](pages/index.vue) 中添加状态 (`ref`)
2. 在 `displayedProducts` computed 中应用逻辑
3. 在 UI 中添加交互控件

## 注意事项

1. **无真实后端**: 所有数据来自 [utils/mockData.ts](utils/mockData.ts)，API 调用只是模拟延迟
2. **组件自动注册**: 无需手动 import `components/` 中的组件，直接使用标签名即可
3. **图片来源**: 使用 Unsplash 动态 URL，生产环境需替换为本地/CDN 资源
4. **收藏状态双轨**: `favorites` Set 和 `Product.isFavorite` 需同步更新
5. **性能**: 未使用虚拟滚动，目前 mock 数据量小，大数据量时需优化
6. **浏览器兼容性**: 支持现代浏览器（ES2020+）

## 扩展建议

- [ ] 连接真实 API（替换 mock 数据）
- [ ] 本地存储（localStorage）持久化购物车/收藏
- [ ] 分页或无限滚动
- [ ] 路由详情页 `/product/[id]`
- [ ] 搜索历史、热搜词
- [ ] 用户账户系统集成
