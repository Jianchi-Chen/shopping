package io.cjc.backend.config;

import io.cjc.backend.entity.*;
import io.cjc.backend.enums.*;
import io.cjc.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
//@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final MerchantRepository merchantRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        try {
            log.info("开始初始化测试数据...");
            
            long count = userRepository.count();
            log.info("当前用户数量: {}", count);
            if (count > 0) {
                log.info("数据已存在，跳过初始化");
                return;
            }

            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(UserRole.ADMIN);
            userRepository.save(admin);
            log.info("管理员账号创建完成");

            User merchantUser = new User();
            merchantUser.setUsername("merchant1");
            merchantUser.setPassword(passwordEncoder.encode("merchant123"));
            merchantUser.setRole(UserRole.MERCHANT);
            merchantUser.setMerchantId("shop-001");
            userRepository.save(merchantUser);
            log.info("商家账号创建完成");

            Merchant merchant = new Merchant();
            merchant.setShopId("shop-001");
            merchant.setShopName("测试商店1");
            merchant.setOwnerName("张三");
            merchant.setContactPhone("13800138000");
            merchant.setStatus(MerchantStatus.ACTIVE);
            merchant.setUser(merchantUser);
            merchantRepository.save(merchant);
            log.info("商家信息创建完成");

            createProduct("iPhone 15 Pro", "IP15P-001", "7999.00", "8999.00", 100, "电子产品", "shop-001", "测试商店1");
            createProduct("MacBook Pro", "MBP-001", "12999.00", "14999.00", 50, "电子产品", "shop-001", "测试商店1");
            createProduct("AirPods Pro", "APP-001", "1899.00", "1999.00", 200, "电子产品", "shop-001", "测试商店1");
            createProduct("iPad Air", "IPA-001", "4999.00", "5499.00", 80, "电子产品", "shop-001", "测试商店1");
            createProduct("Apple Watch", "AW-001", "2999.00", "3299.00", 120, "电子产品", "shop-001", "测试商店1");
            log.info("商品数据创建完成");

            createCustomer("李四", "13900139000", "lisi@example.com");
            createCustomer("王五", "13900139001", "wangwu@example.com");
            log.info("客户数据创建完成");

            System.out.println("==================================");
            System.out.println("测试数据初始化完成！");
            System.out.println("管理员账号: admin / admin123");
            System.out.println("商家账号: merchant1 / merchant123");
            System.out.println("==================================");
            log.info("所有测试数据初始化完成");
        } catch (Exception e) {
            log.error("数据初始化失败", e);
            throw new RuntimeException("数据初始化失败", e);
        }
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
