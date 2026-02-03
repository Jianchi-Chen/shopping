# API 测试指南

## 测试环境

- 基础 URL: `http://localhost:8080/api`
- 测试账号:
  - 管理员: `admin / admin123`
  - 商家: `merchant1 / merchant123`

## 测试步骤

### 1. 登录获取 Token

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

响应：
```json
{
  "code": 200,
  "result": {
    "token": "eyJhbGc..."
  },
  "message": "Success",
  "type": "success"
}
```

### 2. 测试商品列表（需要 Token）

```bash
curl -X GET "http://localhost:8080/api/commerce/products?page=1&pageSize=10" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 3. 测试商品状态更新

```bash
curl -X POST http://localhost:8080/api/commerce/products/status \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"id":"PRODUCT_ID","status":"OFF_SALE"}'
```

### 4. 测试订单列表

```bash
curl -X GET "http://localhost:8080/api/commerce/orders?page=1&pageSize=10" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 5. 测试商家列表（仅管理员）

```bash
curl -X GET "http://localhost:8080/api/identity/merchants?page=1&pageSize=10" \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

### 6. 测试用户列表（仅管理员）

```bash
curl -X GET "http://localhost:8080/api/identity/users?page=1&pageSize=10" \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

## 常见错误码

- `200`: 成功
- `400`: 请求参数错误
- `401`: 未认证
- `403`: 无权限
- `500`: 服务器错误

## 前端集成示例

### JavaScript/TypeScript

```typescript
// 登录
const login = async (username: string, password: string) => {
  const response = await fetch('http://localhost:8080/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password })
  });
  const data = await response.json();
  localStorage.setItem('token', data.result.token);
  return data;
};

// 获取商品列表
const getProducts = async (page = 1, pageSize = 10) => {
  const token = localStorage.getItem('token');
  const response = await fetch(
    `http://localhost:8080/api/commerce/products?page=${page}&pageSize=${pageSize}`,
    {
      headers: { 'Authorization': `Bearer ${token}` }
    }
  );
  return response.json();
};
```

## Postman 导入

可以使用以下格式创建 Postman Collection：

1. 创建环境变量:
   - `base_url`: `http://localhost:8080/api`
   - `token`: (登录后获取)

2. 在 Headers 中添加:
   - `Authorization`: `Bearer {{token}}`

## 注意事项

1. 所有需要认证的接口都需要在 Header 中添加 `Authorization: Bearer {token}`
2. Token 有效期为 24 小时
3. 枚举值必须使用大写加下划线格式，如 `ON_SALE`
4. 时间格式统一为 `yyyy-MM-dd HH:mm`
