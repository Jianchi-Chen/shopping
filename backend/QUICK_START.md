# 🚀 快速开始指南

## 5 分钟运行项目

### 步骤 1: 确保 PostgreSQL 运行

**当前配置使用系统 postgres 数据库**（无需单独创建数据库）

**使用 Docker（推荐）**：
```bash
docker run --name postgres-shopping -e POSTGRES_PASSWORD=dcncloud -p 5432:5432 -d postgres:18
```

**或者手动安装**：参考 [DATABASE_SETUP.md](DATABASE_SETUP.md)

### 步骤 2: 修改配置（如需要）

当前配置在 `src/main/resources/application.properties`：
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=dcncloud  # 改为你的密码
```

### 步骤 3: 启动项目

**方式1: 使用启动脚本（推荐）**
```bash
# Windows
.\start.bat

# Linux/Mac
chmod +x start.sh
./start.sh
```

**方式2: 使用已构建的 JAR**
```bash
# 先构建
.\gradlew.bat build -x test

# 再运行
java -jar build\libs\backend-0.0.1-SNAPSHOT.jar
```

**方式3: 使用 Gradle bootRun**
```bash
# Windows（注意：需要先重新启用 DataInitializer）
.\gradlew.bat bootRun

# Linux/Mac
./gradlew bootRun
```

### 步骤 4: 测试接口

项目启动后，访问 `http://localhost:8080/api`

**测试登录**：
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

获取到 token 后，测试商品列表：
```bash
curl -X GET "http://localhost:8080/api/commerce/products?page=1&pageSize=10" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## 📖 测试账号

- **管理员**: `admin / admin123`
- **商家**: `merchant1 / merchant123`

## 🔥 已实现功能

✅ 用户认证（注册/登录）+ JWT  
✅ 商品管理（列表/状态更新）  
✅ 订单管理（列表/状态更新/售后）  
✅ 商家管理（列表/状态更新）- 仅管理员  
✅ 用户管理（列表/状态更新）- 仅管理员  
✅ 权限控制（Admin/Merchant/User）  
✅ 数据隔离（Merchant 只能查看自己的数据）  
✅ 分页查询 + 多条件筛选  
✅ 自动初始化测试数据  

## 🎯 API 端点一览

### 认证
- `POST /api/auth/register` - 注册
- `POST /api/auth/login` - 登录

### 商品管理（需要 MERCHANT 或 ADMIN 权限）
- `GET /api/commerce/products` - 商品列表
- `POST /api/commerce/products/status` - 更新商品状态

### 订单管理（需要 MERCHANT 或 ADMIN 权限）
- `GET /api/commerce/orders` - 订单列表
- `POST /api/commerce/orders/status` - 更新订单状态
- `POST /api/commerce/orders/refund` - 更新售后状态

### 商家管理（仅 ADMIN）
- `GET /api/identity/merchants` - 商家列表
- `POST /api/identity/merchants/status` - 更新商家状态

### 用户管理（仅 ADMIN）
- `GET /api/identity/users` - 用户列表
- `POST /api/identity/users/status` - 更新用户状态

## 📚 文档说明

- [README.md](README.md) - 完整项目文档
- [API_TEST.md](API_TEST.md) - API 测试指南
- [DATABASE_SETUP.md](DATABASE_SETUP.md) - 数据库配置
- [PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md) - 项目结构说明
- [APIChecklist.md](APIChecklist.md) - API 实现清单

## ⚡ 常见问题

### 1. 端口被占用
修改 `application.properties` 中的 `server.port=8080`

### 2. 数据库连接失败
- 确保 PostgreSQL 正在运行（检查：`tasklist | findstr postgres`）
- 检查用户名密码是否正确（当前: postgres/dcncloud）
- 当前使用 `postgres` 系统数据库，无需单独创建

### 3. bootRun 启动后自动关闭
- 这可能是 DataInitializer 导致的，建议使用 JAR 方式启动
- 或者检查 `src/main/java/io/cjc/backend/config/DataInitializer.java` 的 `@Component` 注解

### 3. Gradle 下载慢
使用国内镜像，在 `build.gradle` 中修改：
```groovy
repositories {
    maven { url 'https://maven.aliyun.com/repository/public/' }
    mavenCentral()
}
```

### 4. JWT Token 过期
Token 有效期为 24 小时，过期后需要重新登录

## 🔧 开发建议

1. 使用 **Postman** 或 **Insomnia** 测试 API
2. 查看控制台日志了解 SQL 执行情况
3. 使用 **pgAdmin** 或 **DBeaver** 查看数据库
4. 前端集成时注意 CORS 配置

## 📦 前端对接

前端项目需要修改 API 基础路径：
```javascript
// 前端配置
const API_BASE_URL = 'http://localhost:8080/api';
```

在请求 Header 中添加：
```javascript
headers: {
  'Authorization': `Bearer ${token}`,
  'Content-Type': 'application/json'
}
```

## 🚀 下一步

1. 根据前端 mock 数据调整 API 响应格式
2. 实现 Merchant 数据隔离过滤
3. 添加订单创建接口（前端购物车用）
4. 实现文件上传（商品图片）
5. 添加更详细的日志记录

## 💡 提示

- 项目启动时会自动创建表结构
- 首次启动会插入测试数据
- 所有密码都经过 BCrypt 加密
- 时间格式统一为 `yyyy-MM-dd HH:mm`

需要帮助？查看控制台日志或提交 Issue！
