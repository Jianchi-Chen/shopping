-- 数据库迁移脚本 V2
-- 添加新字段以支持新接口功能
-- 创建时间: 2026-02-03

-- 1. 修改 users 表，添加用户详细信息字段
ALTER TABLE users ADD COLUMN IF NOT EXISTS name VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS email VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS phone VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS avatar VARCHAR(255);

-- 2. 修改 products 表，添加商品详情字段
ALTER TABLE products ADD COLUMN IF NOT EXISTS description TEXT;
ALTER TABLE products ADD COLUMN IF NOT EXISTS images TEXT;
ALTER TABLE products ADD COLUMN IF NOT EXISTS specs TEXT;
ALTER TABLE products ADD COLUMN IF NOT EXISTS rating DECIMAL(3,2);
ALTER TABLE products ADD COLUMN IF NOT EXISTS review_count INTEGER DEFAULT 0;

-- 3. 修改 orders 表，添加收货地址和备注字段
ALTER TABLE orders ADD COLUMN IF NOT EXISTS remark TEXT;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS receiver_name VARCHAR(255);
ALTER TABLE orders ADD COLUMN IF NOT EXISTS receiver_phone VARCHAR(255);
ALTER TABLE orders ADD COLUMN IF NOT EXISTS province VARCHAR(255);
ALTER TABLE orders ADD COLUMN IF NOT EXISTS city VARCHAR(255);
ALTER TABLE orders ADD COLUMN IF NOT EXISTS district VARCHAR(255);
ALTER TABLE orders ADD COLUMN IF NOT EXISTS address_detail VARCHAR(255);

-- 4. 修改 order_items 表，添加规格和图片字段
ALTER TABLE order_items ADD COLUMN IF NOT EXISTS selected_specs TEXT;
ALTER TABLE order_items ADD COLUMN IF NOT EXISTS image VARCHAR(255);

-- 5. 创建 categories 表
CREATE TABLE IF NOT EXISTS categories (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    icon VARCHAR(255),
    product_count INTEGER DEFAULT 0,
    parent_id VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- 6. 插入测试类目数据
INSERT INTO categories (id, name, icon, product_count, parent_id, created_at, updated_at) 
VALUES 
    ('cat-001', '电子产品', '📱', 0, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('cat-002', '家居生活', '🏠', 0, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('cat-003', '服装鞋包', '👔', 0, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('cat-001-01', '手机', NULL, 0, 'cat-001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('cat-001-02', '电脑', NULL, 0, 'cat-001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('cat-002-01', '家具', NULL, 0, 'cat-002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('cat-002-02', '厨具', NULL, 0, 'cat-002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- 7. 更新现有商品数据，添加示例图片和规格
UPDATE products 
SET 
    images = 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=300&h=300&fit=crop',
    description = '精选优质商品，品质保证',
    rating = 4.5,
    review_count = 100
WHERE images IS NULL;
