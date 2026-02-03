# 数据库安装与配置指南

## PostgreSQL 18 安装

### Windows

1. 下载 PostgreSQL 18
   - 访问: https://www.postgresql.org/download/windows/
   - 下载并运行安装程序

2. 安装配置
   - 端口: 5432 (默认)
   - 密码: 设置 postgres 用户密码
   - Locale: Chinese, China

3. 创建数据库
```sql
-- 使用 pgAdmin 或 psql 命令行
CREATE DATABASE shopping;
```

### macOS

```bash
# 使用 Homebrew
brew install postgresql@18
brew services start postgresql@18

# 创建数据库
createdb shopping
```

### Linux (Ubuntu/Debian)

```bash
# 添加 PostgreSQL 源
sudo sh -c 'echo "deb http://apt.postgresql.org/pub/repos/apt $(lsb_release -cs)-pgdg main" > /etc/apt/sources.list.d/pgdg.list'
wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | sudo apt-key add -
sudo apt-get update
sudo apt-get install postgresql-18

# 创建数据库
sudo -u postgres createdb shopping
```

## 配置 application.properties

修改 `src/main/resources/application.properties`:

```properties
# 根据你的实际配置修改
spring.datasource.url=jdbc:postgresql://localhost:5432/shopping
spring.datasource.username=postgres
spring.datasource.password=your_password
```

## 验证连接

```bash
# 使用 psql 测试连接
psql -h localhost -U postgres -d shopping

# 或在 Spring Boot 启动时查看日志
# 如果看到表创建日志，说明连接成功
```

## 常见问题

### 1. 连接被拒绝

检查 PostgreSQL 是否运行:
```bash
# Windows
services.msc  # 查找 postgresql-x64-18

# macOS/Linux
sudo systemctl status postgresql
```

### 2. 密码错误

重置 postgres 密码:
```bash
sudo -u postgres psql
ALTER USER postgres PASSWORD 'new_password';
```

### 3. 数据库不存在

```sql
-- 登录 PostgreSQL
psql -U postgres

-- 查看所有数据库
\l

-- 创建数据库
CREATE DATABASE shopping;

-- 切换数据库
\c shopping
```

## Docker 方式（推荐）

如果你使用 Docker:

```bash
docker run --name postgres-shopping \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=shopping \
  -p 5432:5432 \
  -d postgres:18

# 查看日志
docker logs postgres-shopping
```

使用 docker-compose:

```yaml
version: '3.8'
services:
  postgres:
    image: postgres:18
    container_name: postgres-shopping
    environment:
      POSTGRES_DB: shopping
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

启动:
```bash
docker-compose up -d
```

## 数据库管理工具

推荐使用以下工具管理数据库:

1. **pgAdmin** - PostgreSQL 官方 GUI 工具
2. **DBeaver** - 通用数据库管理工具
3. **DataGrip** - JetBrains 出品（付费）
4. **psql** - 命令行工具

## 初始化数据

项目首次启动会自动:
1. 创建所有表结构（通过 Hibernate）
2. 插入测试数据（通过 DataInitializer）

如果需要手动清空重建:
```sql
-- 删除所有表
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;

-- 重新启动应用，自动重建
```
