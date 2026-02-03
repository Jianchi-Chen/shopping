# 项目结构说明

```
backend/
├── src/
│   ├── main/
│   │   ├── java/io/cjc/backend/
│   │   │   ├── BackendApplication.java          # 应用入口
│   │   │   ├── common/                          # 通用类
│   │   │   │   ├── ApiResponse.java             # 统一响应格式
│   │   │   │   └── PageResponse.java            # 分页响应格式
│   │   │   ├── config/                          # 配置类
│   │   │   │   ├── DataInitializer.java         # 数据初始化
│   │   │   │   └── SecurityConfig.java          # 安全配置
│   │   │   ├── controller/                      # 控制器层
│   │   │   │   ├── AuthController.java          # 认证接口
│   │   │   │   ├── CustomerController.java      # 用户管理
│   │   │   │   ├── MerchantController.java      # 商家管理
│   │   │   │   ├── OrderController.java         # 订单管理
│   │   │   │   └── ProductController.java       # 商品管理
│   │   │   ├── dto/                             # 数据传输对象
│   │   │   │   ├── CustomerDTO.java
│   │   │   │   ├── MerchantDTO.java
│   │   │   │   ├── OrderDTO.java
│   │   │   │   ├── OrderItemDTO.java
│   │   │   │   └── ProductDTO.java
│   │   │   ├── entity/                          # 实体类（对应数据库表）
│   │   │   │   ├── Customer.java
│   │   │   │   ├── Merchant.java
│   │   │   │   ├── Order.java
│   │   │   │   ├── OrderItem.java
│   │   │   │   ├── Product.java
│   │   │   │   └── User.java
│   │   │   ├── enums/                           # 枚举类
│   │   │   │   ├── CustomerStatus.java
│   │   │   │   ├── MerchantStatus.java
│   │   │   │   ├── OrderStatus.java
│   │   │   │   ├── PayStatus.java
│   │   │   │   ├── ProductStatus.java
│   │   │   │   ├── RefundStatus.java
│   │   │   │   └── UserRole.java
│   │   │   ├── exception/                       # 异常处理
│   │   │   │   └── GlobalExceptionHandler.java  # 全局异常处理
│   │   │   ├── repository/                      # 数据访问层
│   │   │   │   ├── CustomerRepository.java
│   │   │   │   ├── MerchantRepository.java
│   │   │   │   ├── OrderRepository.java
│   │   │   │   ├── ProductRepository.java
│   │   │   │   └── UserRepository.java
│   │   │   ├── security/                        # 安全相关
│   │   │   │   ├── JwtAuthenticationFilter.java # JWT 过滤器
│   │   │   │   ├── JwtTokenProvider.java        # JWT 工具类
│   │   │   │   └── UserPrincipal.java           # 用户主体
│   │   │   └── service/                         # 业务逻辑层
│   │   │       ├── AuthService.java
│   │   │       ├── CustomerService.java
│   │   │       ├── MerchantService.java
│   │   │       ├── OrderService.java
│   │   │       └── ProductService.java
│   │   └── resources/
│   │       ├── application.properties            # 应用配置
│   │       └── init.sql                         # SQL 初始化脚本（参考）
│   └── test/
│       └── java/                                # 测试代码
├── build.gradle                                 # Gradle 构建配置
├── gradlew                                      # Gradle 包装器（Linux/Mac）
├── gradlew.bat                                  # Gradle 包装器（Windows）
├── settings.gradle                              # Gradle 设置
├── start.bat                                    # Windows 启动脚本
├── start.sh                                     # Linux/Mac 启动脚本
├── README.md                                    # 项目说明
├── API_TEST.md                                  # API 测试指南
└── DATABASE_SETUP.md                            # 数据库配置指南
```

## 层级说明

### 1. Controller 层（控制器）
- 职责：处理 HTTP 请求，参数验证，调用 Service
- 返回：统一的 ApiResponse 格式
- 示例：ProductController 处理商品相关的 HTTP 请求

### 2. Service 层（业务逻辑）
- 职责：业务逻辑处理，事务管理
- 调用：Repository 进行数据库操作
- 示例：ProductService 包含商品的业务逻辑

### 3. Repository 层（数据访问）
- 职责：数据库 CRUD 操作
- 继承：JpaRepository，提供基础增删改查
- 示例：ProductRepository 定义自定义查询

### 4. Entity 层（实体）
- 职责：对应数据库表结构
- 注解：使用 JPA 注解映射数据库
- 示例：Product 对应 products 表

### 5. DTO 层（数据传输对象）
- 职责：前后端数据传输格式
- 区别：与 Entity 相比，DTO 只包含需要传输的字段
- 示例：ProductDTO 用于 API 响应

## 数据流向

```
HTTP请求 → Controller → Service → Repository → Database
                ↓
            返回 DTO
                ↓
         ApiResponse<DTO>
                ↓
           JSON 响应
```

## 关键文件说明

### application.properties
- 数据库连接配置
- JWT 密钥配置
- 日志级别配置

### SecurityConfig.java
- Spring Security 配置
- CORS 跨域配置
- JWT 过滤器配置
- 权限路由配置

### DataInitializer.java
- 应用启动时自动执行
- 创建测试数据
- 仅在数据库为空时执行

### JwtTokenProvider.java
- 生成 JWT Token
- 验证 Token
- 解析 Token 信息

## 扩展指南

### 添加新接口

1. **创建 Controller 方法**
```java
@GetMapping("/new-endpoint")
public ApiResponse<DataType> newEndpoint() {
    return ApiResponse.success(service.getData());
}
```

2. **添加 Service 方法**
```java
public DataType getData() {
    // 业务逻辑
    return repository.findData();
}
```

3. **Repository 查询**（如需要）
```java
@Query("SELECT ... FROM ...")
DataType findData();
```

### 添加新实体

1. 创建 Entity 类（添加 JPA 注解）
2. 创建对应的 Repository 接口
3. 创建 Service 类
4. 创建 Controller 类
5. 创建 DTO 类（如需要）

### 添加权限控制

在 SecurityConfig.java 的 `securityFilterChain` 方法中添加：
```java
.requestMatchers("/your-path/**").hasRole("ADMIN")
```

## 注意事项

1. **命名规范**
   - Entity: 驼峰命名，对应表名（单数）
   - Repository: EntityName + Repository
   - Service: EntityName + Service
   - Controller: EntityName + Controller

2. **包结构**
   - 按功能模块分包（controller, service, repository）
   - 不按业务领域分包

3. **事务管理**
   - Service 层方法添加 `@Transactional`
   - 查询操作使用 `@Transactional(readOnly = true)`

4. **异常处理**
   - 统一在 GlobalExceptionHandler 处理
   - Service 层抛出业务异常

5. **安全考虑**
   - 敏感信息不要在 DTO 中暴露
   - 密码使用 BCrypt 加密
   - JWT Secret 在生产环境使用强密钥
