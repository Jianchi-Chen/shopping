package io.cjc.backend.service;

import io.cjc.backend.entity.Order;
import io.cjc.backend.enums.OrderStatus;
import io.cjc.backend.repository.OrderRepository;
import io.cjc.backend.repository.ProductRepository;
import io.cjc.backend.repository.ProductViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductViewRepository productViewRepository;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * 获取统计数据
     * 如果shopId不为空，返回该商家的数据；否则返回全站数据
     */
        public Map<String, Object> getStatistics(String shopId) {
        Map<String, Object> result = new HashMap<>();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        LocalDateTime startOfWeek = startOfDay.minusDays(7);
        LocalDateTime startOfMonth = startOfDay.minusDays(30);

        boolean isMerchant = shopId != null && !shopId.isEmpty();

        long visits = isMerchant
            ? productViewRepository.countByShopIdAndViewedAtBetween(shopId, startOfDay, now)
            : productViewRepository.countByViewedAtBetween(startOfDay, now);
        long totalVisits = isMerchant
            ? productViewRepository.countByShopId(shopId)
            : productViewRepository.count();

        BigDecimal weekSales = isMerchant
            ? orderRepository.sumTotalAmountByShopIdAndStatusAndCreatedAtBetween(shopId, OrderStatus.COMPLETED, startOfWeek, now)
            : orderRepository.sumTotalAmountByStatusAndCreatedAtBetween(OrderStatus.COMPLETED, startOfWeek, now);

        BigDecimal totalSales = isMerchant
            ? orderRepository.sumTotalAmountByShopIdAndStatus(shopId, OrderStatus.COMPLETED)
            : orderRepository.sumTotalAmountByStatus(OrderStatus.COMPLETED);

        long weekOrders = isMerchant
            ? orderRepository.countByShopIdAndStatusAndCreatedAtBetween(shopId, OrderStatus.COMPLETED, startOfWeek, now)
            : orderRepository.countByStatusAndCreatedAtBetween(OrderStatus.COMPLETED, startOfWeek, now);

        long totalOrders = isMerchant
            ? orderRepository.countByShopIdAndStatus(shopId, OrderStatus.COMPLETED)
            : orderRepository.countByStatus(OrderStatus.COMPLETED);

        BigDecimal monthRevenue = isMerchant
            ? orderRepository.sumTotalAmountByShopIdAndStatusAndCreatedAtBetween(shopId, OrderStatus.COMPLETED, startOfMonth, now)
            : orderRepository.sumTotalAmountByStatusAndCreatedAtBetween(OrderStatus.COMPLETED, startOfMonth, now);

        BigDecimal totalRevenue = isMerchant
            ? orderRepository.sumTotalAmountByShopIdAndStatus(shopId, OrderStatus.COMPLETED)
            : orderRepository.sumTotalAmountByStatus(OrderStatus.COMPLETED);

        result.put("visits", visits);
        result.put("totalVisits", totalVisits);
        result.put("sales", weekSales.setScale(2, RoundingMode.HALF_UP));
        result.put("totalSales", totalSales.setScale(2, RoundingMode.HALF_UP));
        result.put("orders", weekOrders);
        result.put("totalOrders", totalOrders);
        result.put("revenue", monthRevenue.setScale(2, RoundingMode.HALF_UP));
        result.put("totalRevenue", totalRevenue.setScale(2, RoundingMode.HALF_UP));

        return result;
    }

        /**
         * 获取趋势数据
         */
        public Map<String, Object> getMetrics(String shopId, int days) {
        Map<String, Object> result = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<Long> visits = new ArrayList<>();
        List<Long> orders = new ArrayList<>();
        List<BigDecimal> revenue = new ArrayList<>();

        LocalDate today = LocalDate.now();
        int range = Math.max(1, Math.min(days, 60));
        boolean isMerchant = shopId != null && !shopId.isEmpty();

        for (int i = range - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.plusDays(1).atStartOfDay();

            dates.add(date.toString());

            long dayVisits = isMerchant
                ? productViewRepository.countByShopIdAndViewedAtBetween(shopId, start, end)
                : productViewRepository.countByViewedAtBetween(start, end);
            visits.add(dayVisits);

            long dayOrders = isMerchant
                ? orderRepository.countByShopIdAndStatusAndCreatedAtBetween(shopId, OrderStatus.COMPLETED, start, end)
                : orderRepository.countByStatusAndCreatedAtBetween(OrderStatus.COMPLETED, start, end);
            orders.add(dayOrders);

            BigDecimal dayRevenue = isMerchant
                ? orderRepository.sumTotalAmountByShopIdAndStatusAndCreatedAtBetween(shopId, OrderStatus.COMPLETED, start, end)
                : orderRepository.sumTotalAmountByStatusAndCreatedAtBetween(OrderStatus.COMPLETED, start, end);
            revenue.add(dayRevenue.setScale(2, RoundingMode.HALF_UP));
        }

        result.put("dates", dates);
        result.put("visits", visits);
        result.put("orders", orders);
        result.put("revenue", revenue);

        List<Map<String, Object>> statusBreakdown = Arrays.stream(OrderStatus.values())
            .map(status -> {
                long count = isMerchant
                    ? orderRepository.countByShopIdAndStatus(shopId, status)
                    : orderRepository.countByStatus(status);
                Map<String, Object> item = new HashMap<>();
                item.put("name", status.name());
                item.put("value", count);
                return item;
            })
            .collect(Collectors.toList());
        result.put("statusBreakdown", statusBreakdown);

        return result;
        }

    /**
     * 计算全站总销售额（已完成订单的总金额）
     */
    private BigDecimal calculateTotalRevenue() {
        try {
            return orderRepository.sumTotalAmountByStatus(OrderStatus.COMPLETED);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }


    /**
     * 获取待办列表（模拟数据）
     */
    public List<Map<String, Object>> getTodoList(String userId) {
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
