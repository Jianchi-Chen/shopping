# 已实现 API（精简版）

> 版本: v2.1.0  
> 基础 URL: http://localhost:8080/api  
> 更新时间: 2026-02-04

本文件用于 frontend / middle 对接。内容已精简，但保留关键接口、请求字段与权限要求。

---

## 1) 认证 Auth

**Auth Header**: `Authorization: Bearer <JWT>`

- **POST /auth/register**（公开）
  - body: `username`, `password`, `role? (USER|MERCHANT|ADMIN)`
  - resp: `{ token }`

- **POST /auth/login**（公开）
  - body: `username`, `password`
  - resp: `{ token }`

- **GET /auth/me**（登录用户）
  - resp: 用户资料（id, username, name, email, phone, avatar, role, createdAt）

- **PUT /auth/me**（登录用户）
  - body: `name?`, `phone?`, `avatar?`
  - resp: 更新后的用户资料

- **POST /auth/change-password**（登录用户）
  - body: `oldPassword`, `newPassword`
  - resp: `null`

---

## 2) 商品 Products

- **GET /commerce/products**（MERCHANT/ADMIN）
  - query: `page?`, `pageSize?`, `keyword?`, `status?`, `category?`, `shopId?`

- **GET /commerce/products/{id}**（公开）
  - resp: 商品详情（含 images/specs/description）

- **POST /commerce/products/status**（MERCHANT/ADMIN）
  - body: `id`, `status (ON_SALE|OFF_SALE|OUT_OF_STOCK)`

---

## 3) 订单 Orders

- **GET /commerce/orders**（MERCHANT/ADMIN）
  - query: `page?`, `pageSize?`, `orderNo?`, `status?`, `payStatus?`, `refundStatus?`, `shopId?`, `buyerId?`

- **POST /commerce/orders/status**（MERCHANT/ADMIN）
  - body: `id`, `status (PENDING_PAYMENT|PENDING_SHIPMENT|SHIPPED|COMPLETED|CLOSED|AFTER_SALE)`

- **POST /commerce/orders/refund**（MERCHANT/ADMIN）
  - body: `id`, `refundStatus (NONE|REQUESTED|APPROVED|REJECTED|REFUNDED)`

- **POST /commerce/orders**（USER）
  - body:
    - `items[]: { productId, quantity, selectedSpecs? }`
    - `shippingAddress: { receiverName, receiverPhone, province, city, district, detail }`
    - `remark?`

- **GET /commerce/orders/my**（USER）
  - query: `page?`, `pageSize?`, `status?`
  - resp: 订单详情列表（含 items 与商品缩略图）

- **GET /user/orders**（USER，简化列表）
  - query: `page?`, `pageSize?`, `status?`
  - resp: 订单列表（OrderDTO）

---

## 4) 地址 Address（用户）

- **GET /user/addresses**（登录用户）
- **POST /user/addresses**（登录用户）
- **PUT /user/addresses/{id}**（登录用户）
- **DELETE /user/addresses/{id}**（登录用户）
- **POST /user/addresses/{id}/default**（登录用户）

地址字段：
`receiverName`, `receiverPhone`, `province`, `city`, `district`, `addressDetail`, `postalCode?`, `isDefault?`

---

## 5) 文件上传 Upload

- **POST /upload**（登录用户）
  - form-data: `file`
  - 限制: 5MB，`image/*`
  - resp: `{ url, filename, size, mimeType }`

- **POST /user/upload/avatar**（登录用户）
  - form-data: `file`
  - 限制: 1MB，仅 `image/jpeg` / `image/png`
  - resp: `{ url }`

- 静态访问: `/uploads/**`（公开）

---

## 6) 商品类目 Categories

- **GET /commerce/categories**（公开）
  - resp: `[{ id, name, icon?, productCount, children? }]`

---

## 7) 商家 Merchants（后台）

- **GET /identity/merchants**（ADMIN）
  - query: `page?`, `pageSize?`, `keyword?`, `status?`

- **POST /identity/merchants/status**（ADMIN）
  - body: `id`, `status (PENDING|ACTIVE|REJECTED|SUSPENDED)`

---

## 8) 用户 Users（后台）

- **GET /identity/users**（ADMIN）
  - query: `page?`, `pageSize?`, `keyword?`, `status?`

- **POST /identity/users/status**（ADMIN）
  - body: `id`, `status (ACTIVE|BANNED)`

---

## 9) Dashboard（后台）

- **GET /dashboard/statistics**（MERCHANT/ADMIN）
  - query: `shopId?`（商家自动使用自己的shopId）
  - resp: 统计数据
    ```json
    {
      "visits": 1234,
      "totalVisits": 56789,
      "sales": 12345,
      "totalSales": 678901,
      "orders": 234,
      "totalOrders": 5678,
      "revenue": 23456,
      "totalRevenue": 789012
    }
    ```

- **GET /dashboard/todos**（MERCHANT/ADMIN）
  - resp: 待办列表
    ```json
    [
      {
        "id": 1,
        "title": "处理待发货订单",
        "status": "PENDING|COMPLETED",
        "createTime": "2026-02-04 09:00"
      }
    ]
    ```

---

## 10) 通用说明

**统一响应格式**
```
{ "code": 200, "result": {}, "message": "Success", "type": "success" }
```

**分页格式**
```
{ "page": 1, "pageSize": 10, "pageCount": 5, "itemCount": 42, "list": [] }
```

**时间格式**: `yyyy-MM-dd HH:mm`

---

如需更完整的示例，请查看 backend/README.md 与 API_TEST.md。
