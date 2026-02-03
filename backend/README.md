# Spring Boot 电商后端 API

## 项目概述

基于 Spring Boot 4.0.2 + PostgreSQL 18 的多商家电商平台后端服务。

## 技术栈

- Java 25
- Spring Boot 4.0.2
- Spring Data JPA
- Spring Security + JWT
- PostgreSQL 18
- Lombok

## 快速开始

### 1. 环境要求

- Java 25
- PostgreSQL 18
- Gradle 8.x

### 2. 数据库配置

创建 PostgreSQL 数据库：

```sql
CREATE DATABASE shopping;
```

修改 `src/main/resources/application.properties` 中的数据库连接信息：

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/shopping
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. 运行项目

```bash
# Windows
.\gradlew.bat bootRun

# Linux/Mac
./gradlew bootRun
```

项目将在 `http://localhost:8080/api` 启动。

### 4. 测试账号

项目首次启动会自动创建测试数据：

- **管理员**: `admin / admin123`
- **商家**: `merchant1 / merchant123`

## API 文档

### 认证接口

#### 注册
```
POST /api/auth/register
Content-Type: application/json

{
  "username": "test",
  "password": "test123",
  "role": "USER"
}
```

#### 登录
```
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}

Response:
{
  "code": 200,
  "result": {
    "token": "eyJhbGc..."
  },
  "type": "success"
}
```

### 商品管理

#### 获取商品列表
```
GET /api/commerce/products?page=1&pageSize=10&keyword=iPhone
Authorization: Bearer {token}

Response:
{
  "code": 200,
  "result": {
    "page": 1,
    "pageSize": 10,
    "pageCount": 1,
    "itemCount": 5,
    "list": [...]
  }
}
```

#### 更新商品状态
```
POST /api/commerce/products/status
Authorization: Bearer {token}
Content-Type: application/json

{
  "id": "product-id",
  "status": "OFF_SALE"
}
```

### 订单管理

#### 获取订单列表
```
GET /api/commerce/orders?page=1&pageSize=10
Authorization: Bearer {token}
```

#### 更新订单状态
```
POST /api/commerce/orders/status
Authorization: Bearer {token}
Content-Type: application/json

{
  "id": "order-id",
  "status": "SHIPPED"
}
```

#### 更新售后状态
```
POST /api/commerce/orders/refund
Authorization: Bearer {token}
Content-Type: application/json

{
  "id": "order-id",
  "refundStatus": "APPROVED"
}
```

### 商家管理（仅管理员）

#### 获取商家列表
```
GET /api/identity/merchants?page=1&pageSize=10
Authorization: Bearer {admin_token}
```

#### 更新商家状态
```
POST /api/identity/merchants/status
Authorization: Bearer {admin_token}
Content-Type: application/json

{
  "id": "merchant-id",
  "status": "ACTIVE"
}
```

### 用户管理（仅管理员）

#### 获取用户列表
```
GET /api/identity/users?page=1&pageSize=10
Authorization: Bearer {admin_token}
```

#### 更新用户状态
```
POST /api/identity/users/status
Authorization: Bearer {admin_token}
Content-Type: application/json

{
  "id": "customer-id",
  "status": "BANNED"
}
```

## 数据库结构

### 核心表

- `users` - 用户认证表
- `merchants` - 商家信息表
- `customers` - 客户信息表
- `products` - 商品表
- `orders` - 订单表
- `order_items` - 订单明细表

### 枚举类型

- `ProductStatus`: ON_SALE, OFF_SALE, OUT_OF_STOCK
- `OrderStatus`: PENDING_PAYMENT, PENDING_SHIPMENT, SHIPPED, COMPLETED, CLOSED, AFTER_SALE
- `PayStatus`: UNPAID, PAID, REFUNDED
- `RefundStatus`: NONE, REQUESTED, APPROVED, REJECTED, REFUNDED
- `MerchantStatus`: PENDING, ACTIVE, REJECTED, SUSPENDED
- `CustomerStatus`: ACTIVE, BANNED
- `UserRole`: ADMIN, MERCHANT, USER

## 权限说明

- **ADMIN**: 可访问所有接口
- **MERCHANT**: 可访问商品和订单管理接口（仅限自己的数据）
- **USER**: 普通用户（前端使用）

## 数据隔离

商家（MERCHANT）角色通过 JWT token 中的 `merchantId` 字段实现数据隔离，只能查看和操作自己店铺的数据。

## 待实现功能

- [ ] Merchant 数据隔离过滤器
- [ ] 订单创建接口（前端用）
- [ ] 购物车接口
- [ ] 用户个人信息接口
- [ ] 文件上传（商品图片）
- [ ] 全局异常处理
- [ ] API 日志记录

## 注意事项

1. JWT secret 在生产环境中需要更换为强密钥
2. 首次运行会自动创建表结构和测试数据
3. CORS 配置默认允许 localhost:3000 和 localhost:5173
4. 所有 API 响应格式遵循统一标准

## 开发建议

1. 使用 Postman 或类似工具测试 API
2. 前端请求时需在 Header 中添加 `Authorization: Bearer {token}`
3. 枚举值使用大写加下划线，如 `ON_SALE`
4. 时间格式为 `yyyy-MM-dd HH:mm`
