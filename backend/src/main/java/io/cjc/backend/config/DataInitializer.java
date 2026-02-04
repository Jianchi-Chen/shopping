package io.cjc.backend.config;

import io.cjc.backend.entity.*;
import io.cjc.backend.enums.*;
import io.cjc.backend.repository.*;
import io.cjc.backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final MerchantRepository merchantRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoryService categoryService;

    @Override
    public void run(String... args) {
        try {
            log.info("开始初始化测试数据...");
            
            long userCount = userRepository.count();
            long merchantCount = merchantRepository.count();
            long productCount = productRepository.count();
            long customerCount = customerRepository.count();
            long categoryCount = categoryRepository.count();

            log.info("当前数据统计: users={}, merchants={}, products={}, customers={}, categories={}",
                    userCount, merchantCount, productCount, customerCount, categoryCount);

            if (userCount == 0) {
                createAdminUserIfMissing();
                createMerchantUserIfMissing();
            }

            if (merchantCount == 0) {
                createMerchantIfMissing();
            }

            if (categoryCount == 0) {
                createDefaultCategories();
                log.info("类目数据创建完成");
            }

            if (productCount == 0) {
                createInitialProducts();
                log.info("商品数据创建完成");
            } else {
                createAdditionalProducts();
                log.info("追加商品示例数据完成");
            }

            if (customerCount == 0) {
                createInitialCustomers();
                log.info("客户数据创建完成");
            } else {
                createAdditionalCustomer();
                log.info("追加客户示例数据完成");
            }

            // 更新分类商品计数
            categoryService.updateCategoryProductCounts();
            log.info("分类商品计数更新完成");

            System.out.println("==================================");
            System.out.println("测试数据初始化完成！");
            System.out.println("管理员账号: admin / admin123");
            System.out.println("商家账号: merchant1 / merchant123");
            System.out.println("==================================");
            log.info("数据初始化/补充完成");
        } catch (Exception e) {
            log.error("数据初始化失败", e);
            throw new RuntimeException("数据初始化失败", e);
        }
    }

    private void createAdminUserIfMissing() {
        if (userRepository.existsByUsername("admin")) {
            return;
        }
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(UserRole.ADMIN);
        userRepository.save(admin);
        log.info("管理员账号创建完成");
    }

    private void createMerchantUserIfMissing() {
        if (userRepository.existsByUsername("merchant1")) {
            return;
        }
        User merchantUser = new User();
        merchantUser.setUsername("merchant1");
        merchantUser.setPassword(passwordEncoder.encode("merchant123"));
        merchantUser.setRole(UserRole.MERCHANT);
        merchantUser.setMerchantId("shop-001");
        userRepository.save(merchantUser);
        log.info("商家账号创建完成");
    }

    private void createMerchantIfMissing() {
        if (merchantRepository.findByShopId("shop-001").isPresent()) {
            return;
        }
        User merchantUser = userRepository.findByUsername("merchant1")
                .orElseGet(() -> {
                    createMerchantUserIfMissing();
                    return userRepository.findByUsername("merchant1").orElse(null);
                });

        Merchant merchant = new Merchant();
        merchant.setShopId("shop-001");
        merchant.setShopName("测试商店1");
        merchant.setOwnerName("张三");
        merchant.setContactPhone("13800138000");
        merchant.setStatus(MerchantStatus.ACTIVE);
        merchant.setUser(merchantUser);
        merchantRepository.save(merchant);
        log.info("商家信息创建完成");
    }

    private void createDefaultCategories() {
        Category electronics = createCategory("电子产品", "icon-device", null);
        Category apparel = createCategory("服饰箱包", "icon-apparel", null);
        Category home = createCategory("家居生活", "icon-home", null);

        createCategory("手机数码", "icon-phone", electronics.getId());
        createCategory("电脑办公", "icon-laptop", electronics.getId());
        createCategory("男装", "icon-men", apparel.getId());
        createCategory("女装", "icon-women", apparel.getId());
        createCategory("家用电器", "icon-appliance", home.getId());
        createCategory("厨具用品", "icon-kitchen", home.getId());
    }

    private Category createCategory(String name, String icon, String parentId) {
        Category category = new Category();
        category.setName(name);
        category.setIcon(icon);
        category.setParentId(parentId);
        return categoryRepository.save(category);
    }

    private void createInitialProducts() {
        createProduct("iPhone 15 Pro", "IP15P-001", "7999.00", "8999.00", 100, "电子产品", "shop-001", "测试商店1");
        createProduct("MacBook Pro", "MBP-001", "12999.00", "14999.00", 50, "电子产品", "shop-001", "测试商店1");
        createProduct("AirPods Pro", "APP-001", "1899.00", "1999.00", 200, "电子产品", "shop-001", "测试商店1");
        createProduct("iPad Air", "IPA-001", "4999.00", "5499.00", 80, "电子产品", "shop-001", "测试商店1");
        createProduct("Apple Watch", "AW-001", "2999.00", "3299.00", 120, "电子产品", "shop-001", "测试商店1");
    }

    private void createAdditionalProducts() {
        String suffix = String.valueOf(System.currentTimeMillis());
        createProduct("示例蓝牙音箱", "SPK-" + suffix, "299.00", "399.00", 60, "电子产品", "shop-001", "测试商店1");
        createProduct("示例智能台灯", "LAMP-" + suffix, "159.00", "199.00", 80, "家居生活", "shop-001", "测试商店1");
    }

    private void createInitialCustomers() {
        createCustomer("李四", "13900139000", "lisi@example.com");
        createCustomer("王五", "13900139001", "wangwu@example.com");
    }

    private void createAdditionalCustomer() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        String phoneTail = String.valueOf(System.currentTimeMillis());
        phoneTail = phoneTail.substring(phoneTail.length() - 8);
        createCustomer("示例用户" + suffix, "139" + phoneTail, "demo_" + suffix + "@example.com");
    }

    private void createProduct(String title, String sku, String price, String originalPrice, 
                               int stock, String category, String shopId, String shopName) {
        Product product = new Product();
        product.setTitle(title);
        product.setSku(sku);
        product.setPrice(new BigDecimal(price));
        product.setOriginalPrice(new BigDecimal(originalPrice));
        product.setStock(stock);
        product.setStatus(ProductStatus.ON_SALE);
        product.setCategory(category);
        product.setShopId(shopId);
        product.setShopName(shopName);
        productRepository.save(product);
    }

    private void createCustomer(String name, String phone, String email) {
        Customer customer = new Customer();
        customer.setName(name);
        customer.setPhone(phone);
        customer.setEmail(email);
        customer.setStatus(CustomerStatus.ACTIVE);
        customerRepository.save(customer);
    }
}
