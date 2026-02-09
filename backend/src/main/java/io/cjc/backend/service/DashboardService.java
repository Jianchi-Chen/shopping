package io.cjc.backend.service;

import io.cjc.backend.repository.OrderRepository;
import io.cjc.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class DashboardService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * 获取统计数据
     */
    public Map<String, Object> getStatistics(Long shopId) {
        Map<String, Object> result = new HashMap<>();

        // 模拟统计数据（实际应从数据库查询）
        if (shopId != null) {
            // 商家数据
            Long productCount = productRepository.count();
            Long orderCount = orderRepository.count();
            
            result.put("visits", 1234);
            result.put("totalVisits", 56789);
            result.put("sales", 12345);
            result.put("totalSales", 678901);
            result.put("orders", orderCount != null ? orderCount.intValue() : 0);
            result.put("totalOrders", orderCount != null ? orderCount.intValue() : 0);
            result.put("revenue", 23456);
            result.put("totalRevenue", 789012);
        } else {
            // 全站数据
            Long totalProducts = productRepository.count();
            Long totalOrders = orderRepository.count();
            
            result.put("visits", 5678);
            result.put("totalVisits", 234567);
            result.put("sales", 56789);
            result.put("totalSales", 2345678);
            result.put("orders", totalOrders != null ? totalOrders.intValue() : 0);
            result.put("totalOrders", totalOrders != null ? totalOrders.intValue() : 0);
            result.put("revenue", 67890);
            result.put("totalRevenue", 3456789);
        }

        return result;
    }

    /**
     * 获取待办列表（模拟数据）
     */
    public List<Map<String, Object>> getTodoList(Long userId) {
        List<Map<String, Object>> todos = new ArrayList<>();

        // 模拟待办数据
        Map<String, Object> todo1 = new HashMap<>();
        todo1.put("id", 1);
        todo1.put("title", "处理待发货订单");
        todo1.put("status", "PENDING");
        todo1.put("createTime", LocalDateTime.now().minusHours(2).format(DATE_TIME_FORMATTER));
        todos.add(todo1);

        Map<String, Object> todo2 = new HashMap<>();
        todo2.put("id", 2);
        todo2.put("title", "审核新上架商品");
        todo2.put("status", "PENDING");
        todo2.put("createTime", LocalDateTime.now().minusHours(1).format(DATE_TIME_FORMATTER));
        todos.add(todo2);

        Map<String, Object> todo3 = new HashMap<>();
        todo3.put("id", 3);
        todo3.put("title", "回复客户咨询");
        todo3.put("status", "COMPLETED");
        todo3.put("createTime", LocalDateTime.now().minusDays(1).format(DATE_TIME_FORMATTER));
        todos.add(todo3);

        return todos;
    }
}
