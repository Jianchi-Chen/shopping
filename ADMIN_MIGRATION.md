# 中后台端口与部署配置指南

## 工作模式

采用 **开发与生产分离方案** 以避免端口冲突，同时保证生产环境的统一访问结构。

### 开发环境（Development）

各应用独立端口运行：

| 应用 | 端口 | 访问地址 | 功能 |
|-----|------|---------|------|
| **前端（Nuxt）** | 3000 | `http://localhost:3000/` | 用户商城 |
| **中后台（Vite）** | 8001 | `http://localhost:8001/` | 管理后台 |
| **后端（Spring Boot）** | 8080 | `http://localhost:8080/api` | API 服务 |

**优点**：
- 避免端口冲突
- 开发调试独立
- 热更新互不影响

### 生产环境（Production）

统一通过反向代理在同一域名下：

| 应用 | 访问路径 | 部署位置 |
|-----|---------|---------|
| **前端** | `http://domain/` | `/path/to/frontend/dist/` |
| **中后台** | `http://domain/admin/` | `/path/to/middle/dist/` |
| **API** | `http://domain/api/` | 反向代理到后端 |

**优点**：
- 避免端口漂移
- 同源访问（无 CORS 问题）
- 易于扩展和维护

### 配置分布

**开发环境配置** (`.env.development`)：
- `VITE_PORT = 8001` - 中后台独立端口
- `VITE_PUBLIC_PATH = /` - 根路径
- `VITE_GLOB_API_URL = http://localhost:8080` - 直接指向后端

**生产环境配置** (`.env.production`)：
- `VITE_PUBLIC_PATH = /admin/` - 子路径部署
- `VITE_GLOB_API_URL = /api` - 相对路径（由反向代理转发）
- `VITE_USE_MOCK = false` - 关闭 mock

## 启动方式

### 本地开发启动

**前提**：后端服务已运行在 `http://localhost:8080`

```bash
# 终端 1：启动后端（Spring Boot）
cd backend
./start.bat  # Windows
# 或
./start.sh   # Linux/Mac

# 终端 2：启动前端（Nuxt）
cd frontend
pnpm dev     # 自动运行在 http://localhost:3000

# 终端 3：启动中后台（Vite）
cd middle
pnpm dev     # 自动运行在 http://localhost:8001
```

**访问地址**：
- 前端商城：http://localhost:3000/
- 中后台：http://localhost:8001/
- 后端 API：http://localhost:8080/api

### 构建用于生产

```bash
# 构建前端
cd frontend
pnpm build

# 构建中后台（生成 /admin/ 路径的文件）
cd middle
pnpm build
```

构建后的目录结构：
```
frontend/dist/        # 对应 /
middle/dist/          # 对应 /admin/
```

## 生产部署（Nginx 配置示例）

假设前端和中后台的构建文件分别部署到 `/opt/shopping/frontend/dist/` 和 `/opt/shopping/middle/dist/`。

```nginx
server {
    listen 80;
    server_name yourdomain.com;

    # 前端应用（根路径）
    location / {
        root /opt/shopping/frontend;
        try_files $uri $uri/ /index.html;
    }

    # 中后台应用（/admin/ 路径）
    location /admin/ {
        alias /opt/shopping/middle/dist/;
        # SPA 路由配置：所有 404 重定向到 /admin/index.html
        try_files $uri $uri/ /admin/index.html;
    }

    # 后端 API 代理
    location /api/ {
        proxy_pass http://backend-server:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # 静态文件缓存优化
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 7d;
        add_header Cache-Control "public, immutable";
    }
}
```

### 部署步骤

1. **构建前端和中后台**
   ```bash
   cd frontend && pnpm build
   cd middle && pnpm build
   ```

2. **上传构建文件到服务器**
   ```bash
   scp -r frontend/dist/* user@server:/opt/shopping/frontend/
   scp -r middle/dist/* user@server:/opt/shopping/middle/
   ```

3. **重启 Nginx**
   ```bash
   sudo systemctl restart nginx
   ```

4. **验证**
   - 访问 http://yourdomain.com/ 应该看到前端商城
   - 访问 http://yourdomain.com/admin/ 应该看到中后台
   - API 调用会通过 /api/ 代理到后端

## 要点总结

| 阶段 | 前端 | 中后台 | 后端 | API |
|-----|-----|-------|------|-----|
| **开发** | http://localhost:3000/ | http://localhost:8001/ | http://localhost:8080/api | 直连后端 |
| **生产** | http://domain/ | http://domain/admin/ | http://domain/api/ | 反向代理 |

## 常见问题

### Q1：两个应用如何共享 localStorage？
**A**：开发时它们是不同域（localhost:3000 vs localhost:8001），无法直接共享。  
生产时在同源（domain），可自动共享。若需开发时共享，可使用 PostMessage 跨窗口通信。

### Q2：生产部署时中后台的路由路径如何处理？
**A**：中后台的所有页面都会在访问时自动加上 `/admin/` 前缀。  
因为 `.env.production` 中 `VITE_PUBLIC_PATH = /admin/`，构建时所有资源、路由都会自动加前缀。

### Q3：中后台能否直接在 3000 和前端共用同一端口？
**A**：不能在开发时做到。开发时两个 Vite/Nuxt 服务器无法同时占用同一个端口。  
如必须这样做，需要用外部代理（如 Nginx 本地反向代理），增加配置复杂度，不推荐。

### Q4：生产环境中 API 收不到请求？
**A**：检查 Nginx 配置中 `/api/` 代理是否正确指向后端地址和端口。  
建议在 `/api/health` 端点上测试，确认代理正常。

## 文件变更记录

**仅在生产构建配置中改动**：
- `.env.production`: `VITE_PUBLIC_PATH = /admin/`，`VITE_GLOB_API_URL = /api`

**开发环境保持原样**：
- `.env.development`: `VITE_PORT = 8001`，`VITE_PUBLIC_PATH = /`
