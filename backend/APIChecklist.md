# API 实现清单

本文档汇总了前端项目（/frontend）与中后台项目（/middle）中待实现的所有 API，供后续真实后端开发参考。

当前采用 Mock 数据与 Alova 框架，开发完成后需替换为真实后端服务。

---

## 一、商品模块（Commerce - Products）

### 1.1 商品列表

**路径**: `GET /api/commerce/products`

**说明**: 服务于前端商品列表展示、价格与状态信息呈现

**请求参数**:
```typescript
{
  page?: number;              // 当前页码（默认 1）
  pageSize?: number;          // 每页数量（默认 10）
  keyword?: string;           // 搜索词（商品名称/ID）
  status?: ProductStatus;     // 商品状态（on_sale / off_sale / out_of_stock）
  category?: string;          // 商品类目
  shopId?: string;           // 所属店铺
}
```

**响应格式**:
```typescript
{
  code: 200,
  result: {
    page: number;
    pageSize: number;
    pageCount: number;          // 总页数
    itemCount: number;          // 总条数
    list: Product[];
  }
}

// Product 字段
{
  id: string;
  title: string;
  sku: string;
  price: number;              // 当前售价
  originalPrice: number;      // 原价/划线价
  stock: number;              // 库存数量
  status: ProductStatus;      // on_sale | off_sale | out_of_stock
  category: string;           // 类目
  shopId: string;             // 所属商店 ID
  shopName: string;           // 所属商店名称
  updatedAt: string;          // 更新时间（YYYY-MM-DD HH:mm）
}
```

**权限**: commerce_product_list

**中后台用于**: 商品列表页面

---

### 1.2 商品上下架状态更新

**路径**: `POST /api/commerce/products/status`

**说明**: 服务于前端商品状态展示与更新

**请求体**:
```typescript
{
  id: string;                 // 商品 ID
  status: ProductStatus;      // 新状态（on_sale / off_sale）
}
```

**响应格式**:
```typescript
{
  code: 200,
  result: {
    id: string;
    status: ProductStatus;
    updatedAt: string;        // 更新时间
  }
}
```

**权限**: commerce_product_update

**中后台用于**: 商品管理页面的上/下架操作

---

## 二、订单模块（Commerce - Orders）

### 2.1 订单列表

**路径**: `GET /api/commerce/orders`

**说明**: 服务于前端订单列表与状态/售后进度展示

**请求参数**:
```typescript
{
  page?: number;              // 当前页码（默认 1）
  pageSize?: number;          // 每页数量（默认 10）
  orderNo?: string;           // 订单号搜索
  status?: OrderStatus;       // 订单状态
  payStatus?: PayStatus;      // 支付状态（unpaid / paid / refunded）
  refundStatus?: RefundStatus;// 售后状态
  shopId?: string;            // 所属店铺
  buyerId?: string;           // 买家 ID
}
```

**响应格式**:
```typescript
{
  code: 200,
  result: {
    page: number;
    pageSize: number;
    pageCount: number;
    itemCount: number;
    list: Order[];
  }
}

// Order 字段
{
  id: string;
  orderNo: string;            // 订单号（唯一）
  status: OrderStatus;        // pending_payment | pending_shipment | shipped | completed | closed | after_sale
  payStatus: PayStatus;       // unpaid | paid | refunded
  refundStatus: RefundStatus; // none | requested | approved | rejected | refunded
  totalAmount: number;        // 订单总金额
  itemCount: number;          // 商品件数
  buyerName: string;          // 买家名称
  shopId: string;             // 所属商店 ID
  shopName: string;           // 所属商店名称
  createdAt: string;          // 下单时间
  items: OrderItem[];         // 订单商品列表
}

// OrderItem 字段
{
  productId: string;
  title: string;
  quantity: number;
  price: number;              // 下单时的价格
}
```

**权限**: commerce_order_list

**中后台用于**: 订单列表页面

---

### 2.2 订单状态更新

**路径**: `POST /api/commerce/orders/status`

**说明**: 服务于前端订单状态流转与进度展示

**请求体**:
```typescript
{
  id: string;                 // 订单 ID
  status: OrderStatus;        // 新状态
}
```

**响应格式**:
```typescript
{
  code: 200,
  result: {
    id: string;
    status: OrderStatus;
    updatedAt: string;
  }
}
```

**权限**: commerce_order_update

**中后台用于**: 订单管理页面的发货等操作

---

### 2.3 售后状态更新

**路径**: `POST /api/commerce/orders/refund`

**说明**: 服务于前端售后进度展示

**请求体**:
```typescript
{
  id: string;                 // 订单 ID
  refundStatus: RefundStatus; // 新的售后状态
}
```

**响应格式**:
```typescript
{
  code: 200,
  result: {
    id: string;
    refundStatus: RefundStatus;
    updatedAt: string;
  }
}
```

**权限**: commerce_order_update

**中后台用于**: 订单管理页面的售后处理

---

## 三、身份与权限模块（Identity）

### 3.1 商家列表

**路径**: `GET /api/identity/merchants`

**说明**: 服务于前端商家身份/资质状态影响商品展示与交易

**请求参数**:
```typescript
{
  page?: number;
  pageSize?: number;
  keyword?: string;           // 店铺名称或负责人搜索
  status?: MerchantStatus;    // pending | active | rejected | suspended
}
```

**响应格式**:
```typescript
{
  code: 200,
  result: {
    page: number;
    pageSize: number;
    pageCount: number;
    itemCount: number;
    list: Merchant[];
  }
}

// Merchant 字段
{
  id: string;
  shopId: string;             // 店铺 ID
  shopName: string;
  ownerName: string;          // 负责人/拥有者名称
  contactPhone: string;       // 联系电话
  status: MerchantStatus;     // pending | active | rejected | suspended
  createdAt: string;          // 创建时间
}
```

**权限**: identity_merchant_list

**中后台用于**: 商家管理列表页面

---

### 3.2 商家状态更新

**路径**: `POST /api/identity/merchants/status`

**说明**: 服务于前端商家资质审核/禁用

**请求体**:
```typescript
{
  id: string;                 // 商家 ID
  status: MerchantStatus;     // 新状态
}
```

**响应格式**:
```typescript
{
  code: 200,
  result: {
    id: string;
    status: MerchantStatus;
    updatedAt: string;
  }
}
```

**权限**: identity_merchant_update

**中后台用于**: 商家管理页面的审核与启用/停用

---

### 3.3 用户列表

**路径**: `GET /api/identity/users`

**说明**: 服务于前端用户登录与订单查询

**请求参数**:
```typescript
{
  page?: number;
  pageSize?: number;
  keyword?: string;           // 姓名或手机号搜索
  status?: CustomerStatus;    // active | banned
}
```

**响应格式**:
```typescript
{
  code: 200,
  result: {
    page: number;
    pageSize: number;
    pageCount: number;
    itemCount: number;
    list: Customer[];
  }
}

// Customer 字段
{
  id: string;
  name: string;
  phone: string;
  status: CustomerStatus;     // active | banned
  orderCount: number;         // 订单总数
  totalSpent: number;         // 累计消费金额
  createdAt: string;          // 注册时间
}
```

**权限**: identity_user_list

**中后台用于**: 用户管理列表页面

---

### 3.4 用户状态更新

**路径**: `POST /api/identity/users/status`

**说明**: 服务于前端用户登录与下单权限控制

**请求体**:
```typescript
{
  id: string;                 // 用户 ID
  status: CustomerStatus;     // active | banned
}
```

**响应格式**:
```typescript
{
  code: 200,
  result: {
    id: string;
    status: CustomerStatus;
    updatedAt: string;
  }
}
```

**权限**: identity_user_update

**中后台用于**: 用户管理页面的启用/禁用

---

## 四、前端接口（需前端另行实现）

### 4.1 用户认证

**说明**: 前端页面当前使用 localStorage + Mock，需对接真实后端

**涉及功能**:
- 用户登录
- 用户注册
- 用户登出
- 获取当前用户信息

**相关文件**:
- frontend/stores/user.ts
- frontend/pages/auth/login.vue
- frontend/pages/auth/register.vue

---

### 4.2 订单创建与支付

**说明**: 前端购物车与结算流程需对接订单服务

**涉及功能**:
- 创建订单
- 获取订单列表
- 获取订单详情
- 取消订单
- 申请售后

**相关文件**:
- frontend/pages/checkout/checkout.vue
- frontend/pages/checkout/payment.vue
- frontend/pages/checkout/order-confirmation.vue
- frontend/pages/user/orders.vue

---

## 五、实现建议

### 数据库设计要点

#### Products 表
- id (主键)
- title, sku, category
- price, originalPrice
- stock (库存)
- status (上下架状态)
- shopId (关联商店)
- updatedAt

#### Orders 表
- id (主键)
- orderNo (唯一订单号)
- buyerId (买家)
- shopId (商店)
- status, payStatus, refundStatus
- totalAmount, itemCount
- createdAt

#### OrderItems 表
- id
- orderId
- productId, title, quantity, price (下单时价格)

#### Merchants 表
- id (主键)
- shopId, shopName
- ownerName, contactPhone
- status (审核状态)
- createdAt

#### Customers 表（或 Users 表）
- id (主键)
- name, phone, email
- status (active / banned)
- orderCount, totalSpent
- createdAt

---

## 六、通用响应格式

所有 API 应遵循以下响应格式：

```typescript
{
  code: 200;                  // HTTP 状态码
  result: any;               // 实际数据
  message?: string;          // 提示信息
  type?: 'success' | 'error';
}
```

**错误响应示例**:
```typescript
{
  code: 400,
  result: null,
  message: "商品不存在",
  type: "error"
}
```

---

## 七、权限清单

| 权限字符串 | 说明 | 角色 |
|---|---|---|
| commerce_product_list | 商品列表查看 | Admin, Merchant |
| commerce_product_update | 商品维护（上下架、改价等） | Admin, Merchant |
| commerce_order_list | 订单列表查看 | Admin, Merchant |
| commerce_order_update | 订单处理（发货、售后等） | Admin, Merchant |
| identity_merchant_list | 商家列表查看 | Admin |
| identity_merchant_update | 商家管理（审核、停用等） | Admin |
| identity_user_list | 用户列表查看 | Admin |
| identity_user_update | 用户管理（禁用、启用等） | Admin |

---

## 八、后续扩展 API（可选）

基于当前功能扩展的建议：

- 商品审核与违规处理
- 发票与税务相关
- 物流与配送管理
- 评价与反馈系统
- 营销与优惠券管理
- 数据分析与报表

---

## 九、参考

- 中后台项目路由: /middle/src/router/modules/
- API 定义: /middle/src/api/
- Mock 示例: /middle/mock/
- 前端类型定义: /frontend/types/product.ts
- 中后台类型定义: /middle/src/types/
