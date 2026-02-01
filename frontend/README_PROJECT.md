# ShopHub 在线购物网站前端

一个基于 Nuxt 4 + TypeScript + Tailwind CSS 的在线购物网站前端展示项目，参考亚马逊首页设计。

## 项目特点

- ✅ **前端展示**：仅做前端展示，不包含真实后端请求
- ✅ **Mock 数据**：使用 mock 数据模拟商品列表
- ✅ **响应式设计**：完美适配移动端和桌面端
- ✅ **现代技术栈**：Nuxt 4 + Vue 3 + TypeScript
- ✅ **样式框架**：Tailwind CSS 实现高效样式
- ✅ **状态管理**：Pinia 状态管理库

## 项目结构

```
frontend/
├── app/                          # Nuxt 应用入口
│   └── app.vue                   # 根组件
├── pages/                        # 页面级组件（自动路由）
│   └── index.vue                 # 首页
├── components/                   # 可复用组件
│   ├── Header.vue               # 顶部导航栏
│   ├── BannerCarousel.vue        # 轮播图
│   ├── FilterSidebar.vue         # 过滤侧边栏
│   ├── ProductCard.vue           # 商品卡片
│   ├── CartPanel.vue             # 购物车面板
│   └── Footer.vue                # 页脚
├── stores/                       # Pinia 状态管理
│   └── product.ts                # 商品 store
├── types/                        # TypeScript 类型定义
│   └── product.ts                # 商品相关类型
├── utils/                        # 工具函数
│   ├── mockData.ts               # Mock 数据生成
│   └── helpers.ts                # 通用辅助函数
├── assets/                       # 静态资源
│   └── css/
│       └── main.css              # 全局样式
├── public/                       # 公共文件
├── nuxt.config.ts                # Nuxt 配置文件
├── tailwind.config.ts            # Tailwind 配置
├── postcss.config.js             # PostCSS 配置
├── tsconfig.json                 # TypeScript 配置
├── package.json                  # 项目依赖
└── README.md                     # 项目说明

```

## 安装和运行

### 安装依赖

```bash
# 使用 pnpm（推荐）
pnpm install

# 或使用 npm
npm install

# 或使用 yarn
yarn install
```

### 开发模式

```bash
pnpm dev
```

访问 http://localhost:3000 查看应用。

### 构建生产版本

```bash
pnpm build
```

### 预览生产构建

```bash
pnpm preview
```

## 核心功能

### 1. 商品列表展示

- 商品卡片展示（图片、标题、价格、评分、评论数）
- 响应式网格布局（1 列移动端，2 列平板，3-4 列桌面端）
- 商品角标（新品、热销等）

### 2. 搜索和过滤

- **搜索**：按商品标题搜索
- **分类过滤**：按商品分类过滤
- **排序**：相关性、价格高低、评分最高

### 3. 购物车功能

- 添加/删除购物车项目
- 修改数量
- 计算总价
- 购物车提示

### 4. 收藏功能

- 收藏/取消收藏商品
- 收藏状态持久化（仅本会话）

### 5. 轮播和推荐

- 自动轮播横幅图（5秒更新）
- 分类快捷导航
- 商品推荐展示

## 技术栈详情

### 框架和库

- **Nuxt 4**：Vue 3 框架，提供文件路由、SSR 支持
- **Vue 3**：渐进式 JavaScript 框架
- **TypeScript**：静态类型检查
- **Pinia**：状态管理（替代 Vuex）
- **Tailwind CSS**：原子化 CSS 框架
- **Naive UI**：Vue 3 UI 组件库（可选，用于复杂交互组件）

### 开发工具

- **PostCSS**：CSS 处理工具
- **Autoprefixer**：自动添加浏览器前缀

## 设计参考

本项目参考了亚马逊首页的设计：

1. **信息密度**：高效的信息布局，尽可能多地展示商品
2. **搜索优先**：顶部突出搜索框
3. **分类导航**：左侧分类快速导航
4. **轮播推荐**：顶部轮播 banner
5. **购物车入口**：右上角购物车快速访问

## Mock 数据说明

项目使用 `utils/mockData.ts` 提供 mock 数据：

```typescript
// 获取商品列表
const products = getMockProducts()

// 获取分类列表
const categories = getMockCategories()

// 搜索商品
const results = searchProducts(products, searchTerm)

// 排序商品
const sorted = sortProducts(products, 'price-low')
```

### 数据结构

```typescript
// 商品
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
}

// 分类
interface Category {
  id: string
  name: string
  image: string
  productCount: number
}
```

## 状态管理（Pinia）

### Product Store

```typescript
export const useProductStore = defineStore('product', () => {
  // 状态
  const products = ref<Product[]>([])
  const cart = ref<CartItem[]>([])
  const favorites = ref<Set<string>>(new Set())

  // 方法
  const addToCart = (product: Product, quantity: number) => {}
  const removeFromCart = (productId: string) => {}
  const toggleFavorite = (productId: string) => {}

  // 计算属性
  const cartTotal = computed(() => {})
  const cartCount = computed(() => {})

  return {
    products,
    cart,
    favorites,
    cartTotal,
    cartCount,
    addToCart,
    removeFromCart,
    toggleFavorite,
  }
})
```

## 组件说明

### Header（顶部导航）

- Logo 和品牌名称
- 搜索框（响应式，移动端隐藏）
- 账户和购物车入口
- 购物车数量徽章

### BannerCarousel（轮播图）

- 自动轮播（5秒）
- 分类快捷链接
- 手动导航指示器

### FilterSidebar（过滤侧边栏）

- 分类过滤
- 价格范围
- 评分过滤
- 清除过滤按钮

### ProductCard（商品卡片）

- 商品图片和 hover 效果
- 标题、价格、评分
- 添加购物车和查看详情按钮
- 收藏按钮

### CartPanel（购物车）

- 侧滑购物车面板
- 商品列表、数量控制、删除
- 总价计算
- 结算和清空购物车按钮

### Footer（页脚）

- 公司信息
- 帮助链接
- 服务条款

## 响应式设计

项目使用 Tailwind CSS 的响应式前缀实现自适应：

```html
<!-- 移动端 1 列，平板 2 列，桌面 3 列，超大屏 4 列 -->
<div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
  <!-- 内容 -->
</div>

<!-- 隐藏侧边栏在移动端 -->
<aside class="hidden md:block">
  <!-- 侧边栏内容 -->
</aside>
```

## 代码规范

- 使用 TypeScript 进行类型检查
- 组件采用 `<script setup>` 语法
- 样式使用 Tailwind CSS 工具类
- 关键逻辑添加注释说明设计意图
- 文件命名采用 PascalCase（组件）和 camelCase（工具函数）

## 扩展建议

1. **连接真实 API**：将 mock 数据替换为真实 API 请求
2. **用户认证**：添加登录/注册功能
3. **购物流程**：完整的结算、支付流程
4. **商品详情页**：详情页面和评论系统
5. **收藏列表**：收藏商品专属页面
6. **订单管理**：用户订单历史查看
7. **推荐系统**：个性化商品推荐

## 浏览器兼容性

- Chrome（最新版）
- Firefox（最新版）
- Safari（最新版）
- Edge（最新版）
- 移动浏览器（iOS Safari，Android Chrome）

## 性能优化

- 图片使用外部 CDN（Unsplash）
- 组件懒加载
- 虚拟列表（大列表时）
- 样式按需生成（Tailwind）

## 许可证

MIT

## 作者

ShopHub Team

---

**开发提示**：

- 修改 mock 数据：编辑 `utils/mockData.ts`
- 自定义样式：编辑 `tailwind.config.ts` 或 `assets/css/main.css`
- 添加新页面：在 `pages/` 目录创建 `.vue` 文件（自动路由）
- 添加新组件：在 `components/` 目录创建 `.vue` 文件（自动注册）

