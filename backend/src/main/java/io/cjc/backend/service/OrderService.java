package io.cjc.backend.service;

import io.cjc.backend.common.PageResponse;
import io.cjc.backend.dto.OrderDTO;
import io.cjc.backend.dto.OrderItemDTO;
import io.cjc.backend.entity.Order;
import io.cjc.backend.entity.OrderItem;
import io.cjc.backend.enums.OrderStatus;
import io.cjc.backend.enums.PayStatus;
import io.cjc.backend.enums.RefundStatus;
import io.cjc.backend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Transactional(readOnly = true)
    public PageResponse<OrderDTO> getOrders(
            Integer page, Integer pageSize,
            String orderNo, OrderStatus status,
            PayStatus payStatus, RefundStatus refundStatus,
            String shopId, String buyerId) {
        
        Pageable pageable = PageRequest.of(
                page - 1, pageSize,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        
        Page<Order> orderPage = orderRepository.findByFilters(
                orderNo, status, payStatus, refundStatus, shopId, buyerId, pageable
        );
        
        List<OrderDTO> dtoList = orderPage.getContent().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        
        return PageResponse.of(page, pageSize, orderPage.getTotalElements(), dtoList);
    }

    @Transactional
    public OrderDTO updateStatus(String id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        
        order.setStatus(status);
        Order saved = orderRepository.save(order);
        
        return toDTO(saved);
    }

    @Transactional
    public OrderDTO updateRefundStatus(String id, RefundStatus refundStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        
        order.setRefundStatus(refundStatus);
        Order saved = orderRepository.save(order);
        
        return toDTO(saved);
    }

    private OrderDTO toDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderNo(order.getOrderNo());
        dto.setStatus(order.getStatus());
        dto.setPayStatus(order.getPayStatus());
        dto.setRefundStatus(order.getRefundStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setItemCount(order.getItemCount());
        dto.setBuyerName(order.getBuyerName());
        dto.setShopId(order.getShopId());
        dto.setShopName(order.getShopName());
        dto.setCreatedAt(order.getCreatedAt().format(DATE_FORMATTER));
        
        List<OrderItemDTO> items = order.getItems().stream()
                .map(this::toItemDTO)
                .collect(Collectors.toList());
        dto.setItems(items);
        
        return dto;
    }

    private OrderItemDTO toItemDTO(OrderItem item) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setProductId(item.getProductId());
        dto.setTitle(item.getTitle());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        return dto;
    }
}
