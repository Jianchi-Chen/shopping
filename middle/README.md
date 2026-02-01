## 项目说明

本目录为在线购物平台的中后台项目，基于 Vue 3 + Vite + TypeScript + Naive UI（naive-ui-admin）构建，面向平台管理员与商家两类角色。

当前已实现的中后台模块（与前端功能一一对应）：

- 商品管理（对应前端商品列表/详情/价格/状态）
- 订单管理（对应前端订单状态/售后进度）
- 商家管理（对应前端商品可售资格与店铺状态）
- 用户管理（对应前端登录与下单权限）

所有页面均使用 Mock 数据，接口形态贴近真实后端，便于后续无缝替换为真实服务。

---

## 运行方式

1) 安装依赖

- 在 middle 目录执行
- 使用 pnpm

2) 启动开发服务器

- 运行 dev 命令
- 默认启用 Mock 接口

3) 构建

- 运行 build 命令

提示：Mock 接口由 Alova + @alova/mock 驱动，开关可在本地配置中切换。

---

## 角色与示例账号

以下账号为示例用途（Mock 数据）：

- 平台管理员（Admin）
  - 用户名：admin
  - 密码：123456
  - 具备商品/订单/商家/用户的全部管理权限

- 商家（Merchant）
  - 用户名：merchant
  - 密码：123456
  - 仅具备商品与订单相关权限（示例）

---

## 模块用法指南

### 1. 商品管理

对应前端：商品列表展示、价格/库存/状态呈现

- 列表接口：/api/commerce/products
- 状态更新接口：/api/commerce/products/status
- 功能：筛选、查看、上下架、改价（Mock）
- 权限：commerce_product_list / commerce_product_update

入口页面：
- 商品管理页面：/commerce/product

### 2. 订单管理

对应前端：订单列表、状态流转、售后进度

- 列表接口：/api/commerce/orders
- 状态更新接口：/api/commerce/orders/status
- 售后更新接口：/api/commerce/orders/refund
- 功能：筛选、发货、同意退款（Mock）
- 权限：commerce_order_list / commerce_order_update

入口页面：
- 订单管理页面：/commerce/order

### 3. 商家管理

对应前端：店铺资质与商品可售资格

- 列表接口：/api/identity/merchants
- 状态更新接口：/api/identity/merchants/status
- 功能：审核通过/拒绝、启用/停用
- 权限：identity_merchant_list / identity_merchant_update

入口页面：
- 商家管理页面：/identity/merchant

### 4. 用户管理

对应前端：用户登录与下单权限

- 列表接口：/api/identity/users
- 状态更新接口：/api/identity/users/status
- 功能：启用/禁用账号
- 权限：identity_user_list / identity_user_update

入口页面：
- 用户管理页面：/identity/user

---

## Mock 数据位置

- 商品：mock/commerce/products.ts
- 订单：mock/commerce/orders.ts
- 商家：mock/identity/merchants.ts
- 用户：mock/identity/users.ts

---

## 备注

如果需要对接真实后端：

1) 保持接口路径与字段一致
2) 关闭 Mock 开关
3) 配置真实 API 地址
