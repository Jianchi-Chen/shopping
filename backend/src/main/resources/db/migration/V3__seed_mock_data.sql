-- 数据库迁移脚本 V3
-- 插入前端可用的模拟数据
-- 创建时间: 2026-02-03

-- 0. 启用 pgcrypto 以便使用 crypt/gen_random_uuid
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- 1. 插入测试用户（密码：admin123 / merchant123 / user123）
INSERT INTO users (id, username, password, role, merchant_id, name, email, phone, avatar, created_at, updated_at)
VALUES
    (gen_random_uuid()::text, 'admin',    crypt('admin123',    gen_salt('bf')), 'ADMIN',    NULL,      '平台管理员', 'admin@example.com',    '13800000000', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200&h=200&fit=crop', NOW(), NOW()),
    (gen_random_uuid()::text, 'merchant1',crypt('merchant123', gen_salt('bf')), 'MERCHANT', 'shop-001','测试商家',  'merchant1@example.com','13800000001', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=200&h=200&fit=crop', NOW(), NOW()),
    (gen_random_uuid()::text, 'user1',    crypt('user123',     gen_salt('bf')), 'USER',     NULL,      '测试用户',  'user1@example.com',    '13800000002', 'https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=200&h=200&fit=crop', NOW(), NOW())
ON CONFLICT (username) DO NOTHING;

-- 2. 插入测试商家
INSERT INTO merchants (id, shop_id, shop_name, owner_name, contact_phone, status, created_at, updated_at)
VALUES
    (gen_random_uuid()::text, 'shop-001', '测试商店1', '商家一', '13800000001', 'ACTIVE', NOW(), NOW())
ON CONFLICT (shop_id) DO NOTHING;

-- 3. 插入测试客户
INSERT INTO customers (id, name, phone, email, status, order_count, total_spent, created_at, updated_at)
VALUES
    (gen_random_uuid()::text, '李四', '13900139000', 'lisi@example.com', 'ACTIVE', 2, 2999.00, NOW(), NOW()),
    (gen_random_uuid()::text, '王五', '13900139001', 'wangwu@example.com', 'ACTIVE', 1, 599.00, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- 4. 插入测试商品
INSERT INTO products (id, title, sku, price, original_price, stock, status, category, shop_id, shop_name, description, images, specs, rating, review_count, created_at, updated_at)
VALUES
    (gen_random_uuid()::text, 'iPhone 15 Pro', 'MOCK-IPHONE-15P', 7999.00, 8999.00, 120, 'ON_SALE', '电子产品', 'shop-001', '测试商店1',
     '全新 A17 Pro 芯片，钛金属机身，超强摄影体验。',
     'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=600&h=600&fit=crop,https://images.unsplash.com/photo-1510552776732-01acc9a4c4c7?w=600&h=600&fit=crop',
     '{"颜色":["钛金属黑","钛金属白"],"存储":["256G","512G"]}',
     4.8, 256, NOW(), NOW()),
    (gen_random_uuid()::text, 'MacBook Pro 14', 'MOCK-MBP-14', 13999.00, 15999.00, 40, 'ON_SALE', '电子产品', 'shop-001', '测试商店1',
     'M3 Pro 芯片，Liquid Retina XDR 屏幕。',
     'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=600&h=600&fit=crop',
     '{"颜色":["深空灰","银色"],"内存":["16G","32G"]}',
     4.7, 128, NOW(), NOW()),
    (gen_random_uuid()::text, 'AirPods Pro', 'MOCK-AIRPODS-PRO', 1899.00, 1999.00, 300, 'ON_SALE', '电子产品', 'shop-001', '测试商店1',
     '主动降噪，空间音频，佩戴舒适。',
     'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&h=600&fit=crop',
     '{"颜色":["白色"],"版本":["USB-C"]}',
     4.6, 89, NOW(), NOW()),
    (gen_random_uuid()::text, '北欧风沙发', 'MOCK-SOFA-001', 2599.00, 2999.00, 25, 'ON_SALE', '家居生活', 'shop-001', '测试商店1',
     '简约北欧风，舒适耐用。',
     'https://images.unsplash.com/photo-1549187774-b4e9b0445b41?w=600&h=600&fit=crop',
     '{"颜色":["浅灰","奶油白"],"尺寸":["2座","3座"]}',
     4.5, 42, NOW(), NOW()),
    (gen_random_uuid()::text, '不粘锅套装', 'MOCK-PAN-SET', 399.00, 499.00, 80, 'ON_SALE', '家居生活', 'shop-001', '测试商店1',
     '三层不粘涂层，轻油少烟。',
     'https://images.unsplash.com/photo-1543353071-873f17a7a088?w=600&h=600&fit=crop',
     '{"规格":["三件套","五件套"]}',
     4.4, 36, NOW(), NOW()),
    (gen_random_uuid()::text, '轻薄羽绒服', 'MOCK-DOWN-001', 699.00, 899.00, 150, 'ON_SALE', '服装鞋包', 'shop-001', '测试商店1',
     '保暖轻盈，多色可选。',
     'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=600&h=600&fit=crop',
     '{"颜色":["黑色","藏青","卡其"],"尺码":["S","M","L","XL"]}',
     4.3, 65, NOW(), NOW())
ON CONFLICT (sku) DO NOTHING;

-- 5. 更新类目商品数量
UPDATE categories c
SET product_count = s.cnt
FROM (
    SELECT category, COUNT(*) AS cnt
    FROM products
    GROUP BY category
) s
WHERE c.name = s.category;
