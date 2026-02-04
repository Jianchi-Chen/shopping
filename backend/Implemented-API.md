# 已实现 API 文档

> **版本**: v2.0.0  
> **基础 URL**: `http://localhost:8080/api`  
> **更新时间**: 2026-02-03

本文档列出所有已实现的后端 API，供前端 `/frontend` 和中后台 `/middle` 项目对接使用。

---

## 📋 目录

- [1. 认证模块 (Auth)](#1-认证模块-auth)
- [2. 商品管理 (Commerce - Products)](#2-商品管理-commerce---products)
- [3. 订单管理 (Commerce - Orders)](#3-订单管理-commerce---orders)
- [4. 商家管理 (Identity - Merchants)](#4-商家管理-identity---merchants)
- [5. 用户管理 (Identity - Users)](#5-用户管理-identity---users)
- [6. 文件上传](#6-文件上传)
- [7. 商品类目](#7-商品类目)
- [8. 通用说明](#8-通用说明)

---

## 1. 认证模块 (Auth)

### 1.1 用户注册

**接口**: `POST /api/auth/register`

**权限**: 无需认证

**请求示例**:
```json
{
  "username": "testuser",
  "password": "test123",
  "role": "USER"
}
```

**请求字段**:
| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | string | 是 | 用户名 |
| password | string | 是 | 密码 |
| role | string | 否 | 角色：USER / MERCHANT / ADMIN（默认 USER） |

**响应示例**:
```json
{
  "code": 200,
  "result": {
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  },
  "message": "Success",
  "type": "success"
}
```

---

### 1.2 用户登录

**接口**: `POST /api/auth/login`

**权限**: 无需认证

**请求示例**:
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**请求字段**:
| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | string | 是 | 用户名 |
| password | string | 是 | 密码 |

**响应示例**:
```json
{
  "code": 200,
  "result": {
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  },
  "message": "Success",
  "type": "success"
}
```

**测试账号**:
- 管理员: `admin / admin123`
- 商家: `merchant1 / merchant123`

---

### 1.3 获取当前用户信息

**接口**: `GET /api/auth/me`

**权限**: 需要 JWT Token

**请求头**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**响应示例**:
```json
{
  "code": 200,
  "result": {
    "id": "user-uuid-001",
    "username": "testuser",
    "name": "测试用户",
    "email": "testuser@example.com",
    "phone": "13800138000",
    "avatar": "https://example.com/avatar.jpg",
    "role": "USER",
    "createdAt": "2026-02-01 10:00"
  },
  "message": "Success",
  "type": "success"
}
```

**响应字段说明**:
| 字段 | 类型 | 说明 |
|------|------|------|
| id | string | 用户 UUID |
| username | string | 用户名（登录用） |
| name | string | 用户姓名/昵称 |
| email | string | 用户邮箱 |
| phone | string | 手机号 |
| avatar | string | 头像 URL |
| role | string | 用户角色：USER / MERCHANT / ADMIN |
| createdAt | string | 注册时间 |

---

### 1.4 更新当前用户信息

**接口**: `PUT /api/auth/me`

**权限**: 需要 JWT Token

**请求头**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json
```

**请求示例**:
```json
{
  "name": "新的姓名",
  "phone": "13900139000",
  "avatar": "https://example.com/new-avatar.jpg"
}
```

**请求字段说明**:
| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | string | 否 | 用户姓名 |
| phone | string | 否 | 手机号 |
| avatar | string | 否 | 头像 URL |

**响应示例**:
```json
{
  "code": 200,
  "result": {
    "id": "user-uuid-001",
    "username": "testuser",
    "name": "新的姓名",
    "email": "testuser@example.com",
    "phone": "13900139000",
    "avatar": "https://example.com/new-avatar.jpg",
    "role": "USER",
    "updatedAt": "2026-02-03 18:00"
  },
  "message": "Success",
  "type": "success"
}
```

---

## 2. 商品管理 (Commerce - Products)

### 2.1 获取商品列表

**接口**: `GET /api/commerce/products`

**权限**: 需要 JWT Token（MERCHANT 或 ADMIN）

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | integer | 否 | 页码，默认 1 |
| pageSize | integer | 否 | 每页数量，默认 10 |
| keyword | string | 否 | 搜索关键词（商品名称/SKU） |
| status | string | 否 | 商品状态：ON_SALE / OFF_SALE / OUT_OF_STOCK |
| category | string | 否 | 商品类目 |
| shopId | string | 否 | 店铺 ID |

**请求示例**:
```bash
GET /api/commerce/products?page=1&pageSize=10&keyword=iPhone&status=ON_SALE
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**响应示例**:
```json
{
  "code": 200,
  "result": {
    "page": 1,
    "pageSize": 10,
    "pageCount": 1,
    "itemCount": 5,
    "list": [
      {
        "id": "550e8400-e29b-41d4-a716-446655440000",
        "title": "iPhone 15 Pro",
        "sku": "IP15P-001",
        "price": 7999.00,
        "originalPrice": 8999.00,
        "stock": 100,
        "status": "ON_SALE",
        "category": "电子产品",
        "shopId": "shop-001",
        "shopName": "测试商店1",
        "updatedAt": "2026-02-03 17:30"
      }
    ]
  },
  "message": "Success",
  "type": "success"
}
```

**响应字段说明**:
| 字段 | 类型 | 说明 |
|------|------|------|
| id | string | 商品 UUID |
| title | string | 商品标题 |
| sku | string | 商品 SKU |
| price | number | 当前售价 |
| originalPrice | number | 原价/划线价 |
| stock | integer | 库存数量 |
| status | string | 商品状态：ON_SALE（在售）/ OFF_SALE（下架）/ OUT_OF_STOCK（缺货） |
| category | string | 商品类目 |
| shopId | string | 所属店铺 ID |
| shopName | string | 所属店铺名称 |
| updatedAt | string | 更新时间（格式：yyyy-MM-dd HH:mm） |

---

### 2.2 更新商品状态

**接口**: `POST /api/commerce/products/status`

**权限**: 需要 JWT Token（MERCHANT 或 ADMIN）

**请求示例**:
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "status": "OFF_SALE"
}
```

**请求字段**:
| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | string | 是 | 商品 ID |
| status | string | 是 | 新状态：ON_SALE / OFF_SALE |

**响应示例**:
```json
{
  "code": 200,
  "result": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "title": "iPhone 15 Pro",
    "status": "OFF_SALE",
    "updatedAt": "2026-02-03 17:35"
  },
  "message": "Success",
  "type": "success"
}
```

---

### 2.3 获取商品详情

**接口**: `GET /api/commerce/products/{id}`

**权限**: 无需认证（公开接口）

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | string | 是 | 商品 UUID（路径参数） |

**请求示例**:
```bash
GET /api/commerce/products/550e8400-e29b-41d4-a716-446655440000
```

**响应示例**:
```json
{
  "code": 200,
  "result": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "title": "iPhone 15 Pro",
    "sku": "IP15P-001",
    "price": 7999.00,
    "originalPrice": 8999.00,
    "stock": 100,
    "status": "ON_SALE",
    "category": "电子产品",
    "shopId": "shop-001",
    "shopName": "测试商店1",
    "images": [
      "https://example.com/iphone-1.jpg",
      "https://example.com/iphone-2.jpg",
      "https://example.com/iphone-3.jpg"
    ],
    "description": "商品详细描述...",
    "specs": [
      {
        "name": "颜色",
        "options": [
          { "name": "太空黑", "value": "space-black", "available": true },
          { "name": "金色", "value": "gold", "available": true }
        ]
      },
      {
        "name": "内存",
        "options": [
          { "name": "256GB", "value": "256", "available": true },
          { "name": "512GB", "value": "512", "available": false }
        ]
      }
    ],
    "rating": 4.8,
    "reviewCount": 2543,
    "createdAt": "2026-02-01 10:00",
    "updatedAt": "2026-02-03 17:30"
  },
  "message": "Success",
  "type": "success"
}
```

**响应字段说明**:
| 字段 | 类型 | 说明 |
|------|------|------|
| id | string | 商品 UUID |
| title | string | 商品标题 |
| sku | string | 商品 SKU |
| price | number | 当前售价 |
| originalPrice | number | 原价 |
| stock | integer | 库存数量 |
| status | string | 商品状态 |
| category | string | 商品类目 |
| shopId | string | 店铺 ID |
| shopName | string | 店铺名称 |
| images | array | 商品图片 URL 列表 |
| description | string | 商品详细描述（富文本/HTML） |
| specs | array | 商品规格列表 |
| rating | number | 商品评分（0-5） |
| reviewCount | integer | 评论数量 |
| createdAt | string | 创建时间 |
| updatedAt | string | 更新时间 |

---

## 3. 订单管理 (Commerce - Orders)

### 3.1 获取订单列表

**接口**: `GET /api/commerce/orders`

**权限**: 需要 JWT Token（MERCHANT 或 ADMIN）

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | integer | 否 | 页码，默认 1 |
| pageSize | integer | 否 | 每页数量，默认 10 |
| orderNo | string | 否 | 订单号搜索 |
| status | string | 否 | 订单状态：PENDING_PAYMENT / PENDING_SHIPMENT / SHIPPED / COMPLETED / CLOSED / AFTER_SALE |
| payStatus | string | 否 | 支付状态：UNPAID / PAID / REFUNDED |
| refundStatus | string | 否 | 售后状态：NONE / REQUESTED / APPROVED / REJECTED / REFUNDED |
| shopId | string | 否 | 店铺 ID |
| buyerId | string | 否 | 买家 ID |

**请求示例**:
```bash
GET /api/commerce/orders?page=1&pageSize=10&status=PENDING_SHIPMENT
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**响应示例**:
```json
{
  "code": 200,
  "result": {
    "page": 1,
    "pageSize": 10,
    "pageCount": 1,
    "itemCount": 2,
    "list": [
      {
        "id": "660e8400-e29b-41d4-a716-446655440001",
        "orderNo": "ORD202602030001",
        "status": "PENDING_SHIPMENT",
        "payStatus": "PAID",
        "refundStatus": "NONE",
        "totalAmount": 7999.00,
        "itemCount": 1,
        "buyerName": "李四",
        "shopId": "shop-001",
        "shopName": "测试商店1",
        "createdAt": "2026-02-03 15:20",
        "items": [
          {
            "productId": "550e8400-e29b-41d4-a716-446655440000",
            "title": "iPhone 15 Pro",
            "quantity": 1,
            "price": 7999.00
          }
        ]
      }
    ]
  },
  "message": "Success",
  "type": "success"
}
```

**响应字段说明**:
| 字段 | 类型 | 说明 |
|------|------|------|
| id | string | 订单 UUID |
| orderNo | string | 订单号（唯一） |
| status | string | 订单状态 |
| payStatus | string | 支付状态 |
| refundStatus | string | 售后状态 |
| totalAmount | number | 订单总金额 |
| itemCount | integer | 商品件数 |
| buyerName | string | 买家姓名 |
| shopId | string | 店铺 ID |
| shopName | string | 店铺名称 |
| createdAt | string | 下单时间（格式：yyyy-MM-dd HH:mm） |
| items | array | 订单商品列表 |

**订单商品字段**:
| 字段 | 类型 | 说明 |
|------|------|------|
| productId | string | 商品 ID |
| title | string | 商品标题 |
| quantity | integer | 购买数量 |
| price | number | 下单时的价格 |

---

### 3.2 更新订单状态

**接口**: `POST /api/commerce/orders/status`

**权限**: 需要 JWT Token（MERCHANT 或 ADMIN）

**请求示例**:
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440001",
  "status": "SHIPPED"
}
```

**请求字段**:
| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | string | 是 | 订单 ID |
| status | string | 是 | 新状态：PENDING_PAYMENT / PENDING_SHIPMENT / SHIPPED / COMPLETED / CLOSED / AFTER_SALE |

**响应示例**:
```json
{
  "code": 200,
  "result": {
    "id": "660e8400-e29b-41d4-a716-446655440001",
    "orderNo": "ORD202602030001",
    "status": "SHIPPED",
    "updatedAt": "2026-02-03 17:40"
  },
  "message": "Success",
  "type": "success"
}
```

---

### 3.3 更新售后状态

**接口**: `POST /api/commerce/orders/refund`

**权限**: 需要 JWT Token（MERCHANT 或 ADMIN）

**请求示例**:
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440001",
  "refundStatus": "APPROVED"
}
```

**请求字段**:
| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | string | 是 | 订单 ID |
| refundStatus | string | 是 | 售后状态：NONE / REQUESTED / APPROVED / REJECTED / REFUNDED |

**响应示例**:
```json
{
  "code": 200,
  "result": {
    "id": "660e8400-e29b-41d4-a716-446655440001",
    "orderNo": "ORD202602030001",
    "refundStatus": "APPROVED",
    "updatedAt": "2026-02-03 17:45"
  },
  "message": "Success",
  "type": "success"
}
```

---

### 3.4 创建订单

**接口**: `POST /api/commerce/orders`

**权限**: 需要 JWT Token（USER 角色）

**请求头**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json
```

**请求示例**:
```json
{
  "items": [
    {
      "productId": "550e8400-e29b-41d4-a716-446655440000",
      "quantity": 2,
      "selectedSpecs": {
        "颜色": "太空黑",
        "内存": "256GB"
      }
    },
    {
      "productId": "550e8400-e29b-41d4-a716-446655440001",
      "quantity": 1,
      "selectedSpecs": {}
    }
  ],
  "shippingAddress": {
    "receiverName": "张三",
    "receiverPhone": "13800138000",
    "province": "广东省",
    "city": "深圳市",
    "district": "南山区",
    "detail": "科技园XX路XX号"
  },
  "remark": "请尽快发货"
}
```

**请求字段说明**:
| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| items | array | 是 | 订单商品列表 |
| items[].productId | string | 是 | 商品 ID |
| items[].quantity | integer | 是 | 购买数量 |
| items[].selectedSpecs | object | 否 | 选中的规格（如颜色、尺寸等） |
| shippingAddress | object | 是 | 收货地址 |
| shippingAddress.receiverName | string | 是 | 收货人姓名 |
| shippingAddress.receiverPhone | string | 是 | 收货人手机号 |
| shippingAddress.province | string | 是 | 省份 |
| shippingAddress.city | string | 是 | 城市 |
| shippingAddress.district | string | 是 | 区/县 |
| shippingAddress.detail | string | 是 | 详细地址 |
| remark | string | 否 | 订单备注 |

**响应示例**:
```json
{
  "code": 200,
  "result": {
    "id": "order-uuid-001",
    "orderNo": "ORD202602030001",
    "status": "PENDING_PAYMENT",
    "payStatus": "UNPAID",
    "totalAmount": 15998.00,
    "itemCount": 3,
    "createdAt": "2026-02-03 18:10"
  },
  "message": "订单创建成功",
  "type": "success"
}
```

---

### 3.5 获取当前用户订单列表

**接口**: `GET /api/commerce/orders/my`

**权限**: 需要 JWT Token（USER 角色）

**请求头**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | integer | 否 | 页码，默认 1 |
| pageSize | integer | 否 | 每页数量，默认 10 |
| status | string | 否 | 订单状态筛选 |

**请求示例**:
```bash
GET /api/commerce/orders/my?page=1&pageSize=10&status=PENDING_SHIPMENT
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**响应示例**:
```json
{
  "code": 200,
  "result": {
    "page": 1,
    "pageSize": 10,
    "pageCount": 1,
    "itemCount": 3,
    "list": [
      {
        "id": "order-uuid-001",
        "orderNo": "ORD202602030001",
        "status": "PENDING_SHIPMENT",
        "payStatus": "PAID",
        "refundStatus": "NONE",
        "totalAmount": 7999.00,
        "itemCount": 1,
        "shopName": "测试商店1",
        "createdAt": "2026-02-03 15:20",
        "items": [
          {
            "productId": "550e8400-e29b-41d4-a716-446655440000",
            "title": "iPhone 15 Pro",
            "image": "https://example.com/iphone.jpg",
            "quantity": 1,
            "price": 7999.00
          }
        ]
      }
    ]
  },
  "message": "Success",
  "type": "success"
}
```

**与 `/api/commerce/orders` 的区别**:
- `/api/commerce/orders`: 后台管理接口，MERCHANT/ADMIN 查看所有订单
- `/api/commerce/orders/my`: 用户接口，USER 只能查看自己的订单
- `/api/commerce/orders/my` 响应中包含商品缩略图 `image` 字段

---

## 4. 商家管理 (Identity - Merchants)

### 4.1 获取商家列表

**接口**: `GET /api/identity/merchants`

**权限**: 需要 JWT Token（仅 ADMIN）

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | integer | 否 | 页码，默认 1 |
| pageSize | integer | 否 | 每页数量，默认 10 |
| keyword | string | 否 | 搜索关键词（店铺名称/负责人） |
| status | string | 否 | 商家状态：PENDING / ACTIVE / REJECTED / SUSPENDED |

**请求示例**:
```bash
GET /api/identity/merchants?page=1&pageSize=10&status=ACTIVE
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**响应示例**:
```json
{
  "code": 200,
  "result": {
    "page": 1,
    "pageSize": 10,
    "pageCount": 1,
    "itemCount": 1,
    "list": [
      {
        "id": "770e8400-e29b-41d4-a716-446655440002",
        "shopId": "shop-001",
        "shopName": "测试商店1",
        "ownerName": "张三",
        "contactPhone": "13800138000",
        "status": "ACTIVE",
        "createdAt": "2026-02-01 10:00"
      }
    ]
  },
  "message": "Success",
  "type": "success"
}
```

**响应字段说明**:
| 字段 | 类型 | 说明 |
|------|------|------|
| id | string | 商家 UUID |
| shopId | string | 店铺 ID |
| shopName | string | 店铺名称 |
| ownerName | string | 负责人姓名 |
| contactPhone | string | 联系电话 |
| status | string | 商家状态：PENDING（待审核）/ ACTIVE（正常）/ REJECTED（拒绝）/ SUSPENDED（停用） |
| createdAt | string | 创建时间（格式：yyyy-MM-dd HH:mm） |

---

### 4.2 更新商家状态

**接口**: `POST /api/identity/merchants/status`

**权限**: 需要 JWT Token（仅 ADMIN）

**请求示例**:
```json
{
  "id": "770e8400-e29b-41d4-a716-446655440002",
  "status": "SUSPENDED"
}
```

**请求字段**:
| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | string | 是 | 商家 ID |
| status | string | 是 | 新状态：PENDING / ACTIVE / REJECTED / SUSPENDED |

**响应示例**:
```json
{
  "code": 200,
  "result": {
    "id": "770e8400-e29b-41d4-a716-446655440002",
    "shopName": "测试商店1",
    "status": "SUSPENDED",
    "updatedAt": "2026-02-03 17:50"
  },
  "message": "Success",
  "type": "success"
}
```

---

## 5. 用户管理 (Identity - Users)

### 5.1 获取用户列表

**接口**: `GET /api/identity/users`

**权限**: 需要 JWT Token（仅 ADMIN）

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | integer | 否 | 页码，默认 1 |
| pageSize | integer | 否 | 每页数量，默认 10 |
| keyword | string | 否 | 搜索关键词（姓名/手机号） |
| status | string | 否 | 用户状态：ACTIVE / BANNED |

**请求示例**:
```bash
GET /api/identity/users?page=1&pageSize=10&status=ACTIVE
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**响应示例**:
```json
{
  "code": 200,
  "result": {
    "page": 1,
    "pageSize": 10,
    "pageCount": 1,
    "itemCount": 2,
    "list": [
      {
        "id": "880e8400-e29b-41d4-a716-446655440003",
        "name": "李四",
        "phone": "13900139000",
        "email": "lisi@example.com",
        "status": "ACTIVE",
        "createdAt": "2026-02-01 12:00"
      }
    ]
  },
  "message": "Success",
  "type": "success"
}
```

**响应字段说明**:
| 字段 | 类型 | 说明 |
|------|------|------|
| id | string | 用户 UUID |
| name | string | 用户姓名 |
| phone | string | 手机号 |
| email | string | 邮箱 |
| status | string | 用户状态：ACTIVE（正常）/ BANNED（禁用） |
| createdAt | string | 注册时间（格式：yyyy-MM-dd HH:mm） |

---

### 5.2 更新用户状态

**接口**: `POST /api/identity/users/status`

**权限**: 需要 JWT Token（仅 ADMIN）

**请求示例**:
```json
{
  "id": "880e8400-e29b-41d4-a716-446655440003",
  "status": "BANNED"
}
```

**请求字段**:
| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | string | 是 | 用户 ID |
| status | string | 是 | 新状态：ACTIVE / BANNED |

**响应示例**:
```json
{
  "code": 200,
  "result": {
    "id": "880e8400-e29b-41d4-a716-446655440003",
    "name": "李四",
    "status": "BANNED",
    "updatedAt": "2026-02-03 17:55"
  },
  "message": "Success",
  "type": "success"
}
```

---

## 6. 文件上传

### 6.1 文件上传

**接口**: `POST /api/upload`

**权限**: 需要 JWT Token（所有已登录用户）

**请求头**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: multipart/form-data
```

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | File | 是 | 文件对象 |

**请求示例 (JavaScript)**:
```javascript
const formData = new FormData()
formData.append('file', fileObject)

fetch('http://localhost:8080/api/upload', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`
  },
  body: formData
})
```

**响应示例**:
```json
{
  "code": 200,
  "result": {
    "url": "https://example.com/uploads/2026/02/03/file-uuid.jpg",
    "filename": "avatar.jpg",
    "size": 102400,
    "mimeType": "image/jpeg"
  },
  "message": "上传成功",
  "type": "success"
}
```

**响应字段说明**:
| 字段 | 类型 | 说明 |
|------|------|------|
| url | string | 文件访问 URL（完整路径） |
| filename | string | 原始文件名 |
| size | integer | 文件大小（字节） |
| mimeType | string | 文件 MIME 类型 |

**限制说明**:
- 单个文件大小：最大 5MB
- 允许的文件类型：image/jpeg, image/png, image/gif, image/webp
- 文件存储：建议使用对象存储服务（如阿里云 OSS、七牛云等）

---

## 7. 商品类目

### 7.1 获取类目列表

**接口**: `GET /api/commerce/categories`

**权限**: 无需认证（公开接口）

**响应示例**:
```json
{
  "code": 200,
  "result": [
    {
      "id": "cat-001",
      "name": "电子产品",
      "icon": "📱",
      "productCount": 234,
      "children": [
        {
          "id": "cat-001-01",
          "name": "手机",
          "productCount": 156
        },
        {
          "id": "cat-001-02",
          "name": "电脑",
          "productCount": 78
        }
      ]
    },
    {
      "id": "cat-002",
      "name": "家居生活",
      "icon": "🏠",
      "productCount": 189
    }
  ],
  "message": "Success",
  "type": "success"
}
```

**响应字段说明**:
| 字段 | 类型 | 说明 |
|------|------|------|
| id | string | 类目 ID |
| name | string | 类目名称 |
| icon | string | 类目图标（emoji 或 URL） |
| productCount | integer | 该类目下的商品数量 |
| children | array | 子类目列表（可为空） |

---

## 8. 通用说明

### 8.1 统一响应格式

所有 API 遵循统一的响应格式：

**成功响应**:
```json
{
  "code": 200,
  "result": {},
  "message": "Success",
  "type": "success"
}
```

**错误响应**:
```json
{
  "code": 400,
  "result": null,
  "message": "错误详细信息",
  "type": "error"
}
```

---

### 8.2 HTTP 状态码

| 状态码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未认证（Token 无效或过期） |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

### 8.3 认证方式

除了登录和注册接口外，所有接口都需要在请求头中携带 JWT Token：

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Token 有效期**: 24 小时

---

### 8.4 权限说明

| 角色 | 说明 | 可访问模块 |
|------|------|-----------|
| USER | 普通用户 | 无后台权限 |
| MERCHANT | 商家 | 商品管理、订单管理（仅自己店铺） |
| ADMIN | 管理员 | 所有模块 |

---

### 8.5 枚举值说明

**商品状态 (ProductStatus)**:
- `ON_SALE` - 在售
- `OFF_SALE` - 下架
- `OUT_OF_STOCK` - 缺货

**订单状态 (OrderStatus)**:
- `PENDING_PAYMENT` - 待支付
- `PENDING_SHIPMENT` - 待发货
- `SHIPPED` - 已发货
- `COMPLETED` - 已完成
- `CLOSED` - 已关闭
- `AFTER_SALE` - 售后中

**支付状态 (PayStatus)**:
- `UNPAID` - 未支付
- `PAID` - 已支付
- `REFUNDED` - 已退款

**售后状态 (RefundStatus)**:
- `NONE` - 无售后
- `REQUESTED` - 已申请
- `APPROVED` - 已批准
- `REJECTED` - 已拒绝
- `REFUNDED` - 已退款

**商家状态 (MerchantStatus)**:
- `PENDING` - 待审核
- `ACTIVE` - 正常营业
- `REJECTED` - 审核拒绝
- `SUSPENDED` - 已停用

**用户状态 (CustomerStatus)**:
- `ACTIVE` - 正常
- `BANNED` - 已禁用

---

### 8.6 分页说明

所有列表接口都支持分页，响应格式如下：

```json
{
  "page": 1,           // 当前页码（从 1 开始）
  "pageSize": 10,      // 每页数量
  "pageCount": 5,      // 总页数
  "itemCount": 42,     // 总记录数
  "list": []           // 数据列表
}
```

---

### 8.7 时间格式

所有时间字段统一使用格式：`yyyy-MM-dd HH:mm`

示例：`2026-02-03 17:30`

---

### 8.8 前端集成示例

**JavaScript/TypeScript**:

```typescript
// 1. 登录获取 Token
const login = async (username: string, password: string) => {
  const response = await fetch('http://localhost:8080/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password })
  });
  const data = await response.json();
  if (data.code === 200) {
    localStorage.setItem('token', data.result.token);
  }
  return data;
};

// 2. 获取商品列表
const getProducts = async (page = 1, pageSize = 10) => {
  const token = localStorage.getItem('token');
  const response = await fetch(
    `http://localhost:8080/api/commerce/products?page=${page}&pageSize=${pageSize}`,
    {
      headers: { 
        'Authorization': `Bearer ${token}` 
      }
    }
  );
  return await response.json();
};

// 3. 更新商品状态
const updateProductStatus = async (id: string, status: string) => {
  const token = localStorage.getItem('token');
  const response = await fetch('http://localhost:8080/api/commerce/products/status', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({ id, status })
  });
  return await response.json();
};
```

---

### 8.9 CORS 配置

后端已配置 CORS，允许跨域请求。前端无需额外配置。

---

### 8.10 开发与调试

**查看日志**:
- 应用日志：控制台输出
- 错误日志：`backend/error.log`

**测试工具**:
- Postman
- Insomnia
- curl
- VS Code REST Client

**数据库查看**:
- pgAdmin
- DBeaver
- DataGrip

---

## 📝 待实现接口

以下接口在前端/中后台中使用，但后端**尚未实现**：

1. **订单创建接口** - 前端购物车结算需要
2. **获取当前用户信息** - 前端用户信息展示
3. **用户个人信息更新** - 前端个人中心
4. **商品详情查询** - 前端商品详情页
5. **文件上传接口** - 商品图片上传

---

## 🔗 相关文档

- [快速开始指南](QUICK_START.md)
- [API 测试指南](API_TEST.md)
- [数据库设计](DATABASE_SCHEMA.md)
- [项目结构说明](PROJECT_STRUCTURE.md)
- [API 需求清单](APIChecklist.md)

---

## 📞 技术支持

如有问题或建议，请参考以下资源：

- 查看控制台日志
- 检查 PostgreSQL 数据库连接
- 验证 JWT Token 是否有效
- 确认请求参数格式正确

**当前配置**:
- 服务地址: `http://localhost:8080/api`
- 数据库: PostgreSQL 18 (postgres)
- JWT 有效期: 24 小时

---

**最后更新**: 2026-02-03  
**维护者**: Backend Team
