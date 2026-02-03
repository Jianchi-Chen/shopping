-- 创建数据库（如果不存在）
-- 请在 PostgreSQL 命令行或工具中执行
-- CREATE DATABASE shopping;

-- 用户表会由 Hibernate 自动创建，以下是参考结构和初始数据

-- 插入测试管理员账号（密码：admin123）
INSERT INTO users (id, username, password, role, created_at, updated_at) 
VALUES (
    gen_random_uuid()::text, 
    'admin', 
    '$2a$10$YQv7c0YqYJoqYb7I5oNpwOX0PZGqHdqZqx8X7xW7z8C1Z8Q0X0X0X', 
    'ADMIN', 
    NOW(), 
    NOW()
) ON CONFLICT DO NOTHING;

-- 插入测试商家账号（密码：merchant123）
INSERT INTO users (id, username, password, role, merchant_id, created_at, updated_at) 
VALUES (
    gen_random_uuid()::text, 
    'merchant1', 
    '$2a$10$YQv7c0YqYJoqYb7I5oNpwOX0PZGqHdqZqx8X7xW7z8C1Z8Q0X0X0X', 
    'MERCHANT', 
    'shop-001',
    NOW(), 
    NOW()
) ON CONFLICT DO NOTHING;

-- 插入测试商家信息
INSERT INTO merchants (id, shop_id, shop_name, owner_name, contact_phone, status, created_at, updated_at)
VALUES (
    gen_random_uuid()::text,
    'shop-001',
    '测试商店1',
    '张三',
    '13800138000',
    'ACTIVE',
    NOW(),
    NOW()
) ON CONFLICT DO NOTHING;

-- 插入测试商品
INSERT INTO products (id, title, sku, price, original_price, stock, status, category, shop_id, shop_name, created_at, updated_at)
VALUES 
(gen_random_uuid()::text, 'iPhone 15 Pro', 'IP15P-001', 7999.00, 8999.00, 100, 'ON_SALE', '电子产品', 'shop-001', '测试商店1', NOW(), NOW()),
(gen_random_uuid()::text, 'MacBook Pro', 'MBP-001', 12999.00, 14999.00, 50, 'ON_SALE', '电子产品', 'shop-001', '测试商店1', NOW(), NOW()),
(gen_random_uuid()::text, 'AirPods Pro', 'APP-001', 1899.00, 1999.00, 200, 'ON_SALE', '电子产品', 'shop-001', '测试商店1', NOW(), NOW());

-- 插入测试客户
INSERT INTO customers (id, name, phone, email, status, order_count, total_spent, created_at, updated_at)
VALUES 
(gen_random_uuid()::text, '李四', '13900139000', 'lisi@example.com', 'ACTIVE', 0, 0, NOW(), NOW()),
(gen_random_uuid()::text, '王五', '13900139001', 'wangwu@example.com', 'ACTIVE', 0, 0, NOW(), NOW());
