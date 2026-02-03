# 数据库表关系图

## ER 图（实体关系图）

```
┌─────────────────┐
│     users       │
│─────────────────│
│ id (PK)         │
│ username        │
│ password        │
│ role            │
│ merchant_id     │◄────┐
│ created_at      │     │
│ updated_at      │     │
└─────────────────┘     │
         │              │
         │              │
    ┌────┴─────┐        │
    │          │        │
    ▼          ▼        │
┌─────────────────┐  ┌─────────────────┐
│   customers     │  │   merchants     │
│─────────────────│  │─────────────────│
│ id (PK)         │  │ id (PK)         │
│ name            │  │ shop_id (UK)    │───┐
│ phone (UK)      │  │ shop_name       │   │
│ email (UK)      │  │ owner_name      │   │
│ status          │  │ contact_phone   │   │
│ order_count     │  │ status          │   │
│ total_spent     │  │ user_id (FK)    │   │
│ user_id (FK)    │  │ created_at      │   │
│ created_at      │  │ updated_at      │   │
│ updated_at      │  └─────────────────┘   │
└─────────────────┘                        │
         │                                 │
         │                                 │
         │                                 │
         ▼                                 │
┌─────────────────┐                        │
│     orders      │                        │
│─────────────────│                        │
│ id (PK)         │                        │
│ order_no (UK)   │                        │
│ status          │                        │
│ pay_status      │                        │
│ refund_status   │                        │
│ total_amount    │                        │
│ item_count      │                        │
│ buyer_id        │                        │
│ buyer_name      │                        │
│ shop_id         │◄───────────────────────┘
│ shop_name       │
│ created_at      │
│ updated_at      │
└─────────────────┘
         │
         │ 1:N
         ▼
┌─────────────────┐         ┌─────────────────┐
│  order_items    │         │    products     │
│─────────────────│         │─────────────────│
│ id (PK)         │         │ id (PK)         │
│ order_id (FK)   │         │ title           │
│ product_id      │────────►│ sku (UK)        │
│ title           │         │ price           │
│ quantity        │         │ original_price  │
│ price           │         │ stock           │
└─────────────────┘         │ status          │
                            │ category        │
                            │ shop_id         │◄──┐
                            │ shop_name       │   │
                            │ created_at      │   │
                            │ updated_at      │   │
                            └─────────────────┘   │
                                                  │
                                                  └──(关联商家)
```

## 表说明

### users（用户认证表）
- **主键**: id
- **唯一索引**: username
- **外键**: 无
- **关联**: 
  - 一对一关联 `customers` 或 `merchants`
  - `merchant_id` 字段用于快速识别商家

### merchants（商家信息表）
- **主键**: id
- **唯一索引**: shop_id
- **外键**: user_id → users.id
- **关联**: 
  - 一对多 `products`（通过 shop_id）
  - 一对多 `orders`（通过 shop_id）

### customers（客户信息表）
- **主键**: id
- **唯一索引**: phone, email
- **外键**: user_id → users.id
- **关联**: 
  - 一对多 `orders`（通过 buyer_id）

### products（商品表）
- **主键**: id
- **唯一索引**: sku
- **外键**: 无（通过 shop_id 关联）
- **关联**: 
  - 多对一 `merchants`（通过 shop_id）

### orders（订单主表）
- **主键**: id
- **唯一索引**: order_no
- **外键**: 无（通过 buyer_id, shop_id 关联）
- **关联**: 
  - 多对一 `customers`（通过 buyer_id）
  - 多对一 `merchants`（通过 shop_id）
  - 一对多 `order_items`

### order_items（订单明细表）
- **主键**: id
- **外键**: order_id → orders.id
- **关联**: 
  - 多对一 `orders`
  - 引用 `products`（通过 product_id，非外键）

## 索引建议

### 性能优化索引

```sql
-- 商品表
CREATE INDEX idx_products_shop_id ON products(shop_id);
CREATE INDEX idx_products_status ON products(status);
CREATE INDEX idx_products_category ON products(category);

-- 订单表
CREATE INDEX idx_orders_buyer_id ON orders(buyer_id);
CREATE INDEX idx_orders_shop_id ON orders(shop_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_created_at ON orders(created_at DESC);

-- 订单明细表
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_product_id ON order_items(product_id);

-- 客户表
CREATE INDEX idx_customers_status ON customers(status);

-- 商家表
CREATE INDEX idx_merchants_status ON merchants(status);
```

## 数据流向

### 用户注册流程
```
1. 创建 users 记录（username, password, role）
2. 根据 role 创建：
   - role=USER → 创建 customers 记录
   - role=MERCHANT → 创建 merchants 记录
   - role=ADMIN → 不创建额外记录
```

### 商品查询流程
```
1. 根据筛选条件查询 products
2. 通过 shop_id 获取商家名称（shop_name 冗余存储）
3. 返回商品列表
```

### 订单创建流程
```
1. 创建 orders 记录
   - buyer_id: 从 JWT 获取当前用户
   - shop_id: 从商品获取
   - order_no: 生成唯一订单号
2. 创建 order_items 记录（多条）
   - 记录下单时的价格（price）
   - 记录商品标题（title）冗余
3. 更新 customers.order_count 和 total_spent
```

## 数据冗余设计

为了提高查询性能，采用了部分数据冗余：

| 表 | 冗余字段 | 来源 | 原因 |
|---|----------|------|------|
| products | shop_name | merchants.shop_name | 减少联表查询 |
| orders | buyer_name | customers.name | 减少联表查询 |
| orders | shop_name | merchants.shop_name | 减少联表查询 |
| order_items | title | products.title | 记录下单时商品名 |
| order_items | price | products.price | 记录下单时价格 |

## 级联删除规则

```
orders (删除) → order_items (级联删除)
```

其他表删除时需要手动处理相关数据。

## 枚举字段说明

### ProductStatus
- ON_SALE: 在售
- OFF_SALE: 下架
- OUT_OF_STOCK: 缺货

### OrderStatus
- PENDING_PAYMENT: 待支付
- PENDING_SHIPMENT: 待发货
- SHIPPED: 已发货
- COMPLETED: 已完成
- CLOSED: 已关闭
- AFTER_SALE: 售后中

### PayStatus
- UNPAID: 未支付
- PAID: 已支付
- REFUNDED: 已退款

### RefundStatus
- NONE: 无售后
- REQUESTED: 已申请
- APPROVED: 已同意
- REJECTED: 已拒绝
- REFUNDED: 已退款

### MerchantStatus
- PENDING: 待审核
- ACTIVE: 已激活
- REJECTED: 已拒绝
- SUSPENDED: 已暂停

### CustomerStatus
- ACTIVE: 激活
- BANNED: 禁用

### UserRole
- ADMIN: 平台管理员
- MERCHANT: 商家
- USER: 普通用户
