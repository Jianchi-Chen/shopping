-- 清理示例客户数据并补齐C端测试用户

-- 启用 pgcrypto 以便使用 crypt/gen_random_uuid
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- 如不存在则创建 admin@example.com 用户（密码：admin123）
INSERT INTO users (id, username, password, role, merchant_id, name, email, phone, avatar, created_at, updated_at)
SELECT gen_random_uuid()::text,
       'admin',
       crypt('admin123', gen_salt('bf')),
       'USER',
       NULL,
       'admin',
       'admin@example.com',
       NULL,
       NULL,
       NOW(),
       NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@example.com');

-- 将 admin@example.com 设为普通用户角色（C端）
UPDATE users
SET role = 'USER'
WHERE email = 'admin@example.com';

-- 为 admin@example.com 创建客户记录（若不存在）
INSERT INTO customers (id, name, phone, email, status, order_count, total_spent, user_id, created_at, updated_at)
SELECT gen_random_uuid()::text,
       u.name,
       u.phone,
       u.email,
       'ACTIVE',
       0,
       0,
       u.id,
       NOW(),
       NOW()
FROM users u
WHERE u.email = 'admin@example.com'
  AND NOT EXISTS (SELECT 1 FROM customers c WHERE c.email = u.email);

-- 删除示例客户数据
DELETE FROM customers
WHERE email IN ('lisi@example.com', 'wangwu@example.com');
