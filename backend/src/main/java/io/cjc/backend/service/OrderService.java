package io.cjc.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cjc.backend.common.PageResponse;
import io.cjc.backend.dto.CreateOrderRequest;
import io.cjc.backend.dto.OrderDTO;
import io.cjc.backend.dto.OrderDetailDTO;
import io.cjc.backend.dto.OrderItemDTO;
import io.cjc.backend.entity.Order;
import io.cjc.backend.entity.OrderItem;
import io.cjc.backend.entity.Product;
import io.cjc.backend.enums.OrderStatus;
import io.cjc.backend.enums.PayStatus;
import io.cjc.backend.enums.RefundStatus;
import io.cjc.backend.repository.OrderRepository;
import io.cjc.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
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
    public OrderDetailDTO createOrder(String username, CreateOrderRequest request) {
        // 验证商品并计算总价
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        String shopId = null;
        String shopName = null;

        for (CreateOrderRequest.OrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("商品不存在: " + itemReq.getProductId()));

            // 检查库存
            if (product.getStock() < itemReq.getQuantity()) {
                throw new RuntimeException("商品库存不足: " + product.getTitle());
            }

            // 暂时假设所有商品来自同一店铺
            if (shopId == null) {
                shopId = product.getShopId();
                shopName = product.getShopName();
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setTitle(product.getTitle());
            orderItem.setQuantity(itemReq.getQuantity());
            orderItem.setPrice(product.getPrice());
            
            // 保存选中的规格
            if (itemReq.getSelectedSpecs() != null && !itemReq.getSelectedSpecs().isEmpty()) {
                try {
                    orderItem.setSelectedSpecs(objectMapper.writeValueAsString(itemReq.getSelectedSpecs()));
                } catch (Exception e) {
                    // 忽略规格序列化错误
                }
            }
            
            // 获取商品首图
            if (product.getImages() != null && !product.getImages().isEmpty()) {
                orderItem.setImage(product.getImages().split(",")[0]);
            }

            orderItems.add(orderItem);
            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity())));
        }

        // 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setPayStatus(PayStatus.UNPAID);
        order.setRefundStatus(RefundStatus.NONE);
        order.setTotalAmount(totalAmount);
        order.setItemCount(orderItems.stream().mapToInt(OrderItem::getQuantity).sum());
        order.setBuyerId(username);
        order.setBuyerName(request.getShippingAddress().getReceiverName());
        order.setShopId(shopId);
        order.setShopName(shopName);
        order.setRemark(request.getRemark());
        
        // 设置收货地址
        order.setReceiverName(request.getShippingAddress().getReceiverName());
        order.setReceiverPhone(request.getShippingAddress().getReceiverPhone());
        order.setProvince(request.getShippingAddress().getProvince());
        order.setCity(request.getShippingAddress().getCity());
        order.setDistrict(request.getShippingAddress().getDistrict());
        order.setAddressDetail(request.getShippingAddress().getDetail());

        // 关联订单项
        for (OrderItem item : orderItems) {
            item.setOrder(order);
        }
        order.setItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        // 返回订单详情
        OrderDetailDTO dto = new OrderDetailDTO();
        dto.setId(savedOrder.getId());
        dto.setOrderNo(savedOrder.getOrderNo());
        dto.setStatus(savedOrder.getStatus());
        dto.setPayStatus(savedOrder.getPayStatus());
        dto.setTotalAmount(savedOrder.getTotalAmount());
        dto.setItemCount(savedOrder.getItemCount());
        dto.setCreatedAt(savedOrder.getCreatedAt().format(DATE_FORMATTER));
        
        return dto;
    }

    @Transactional(readOnly = true)
    public PageResponse<OrderDetailDTO> getMyOrders(String username, Integer page, Integer pageSize, OrderStatus status) {
        Pageable pageable = PageRequest.of(
                page - 1, pageSize,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<Order> orderPage;
        if (status != null) {
            orderPage = orderRepository.findByBuyerIdAndStatus(username, status, pageable);
        } else {
            orderPage = orderRepository.findByBuyerId(username, pageable);
        }

        List<OrderDetailDTO> dtoList = orderPage.getContent().stream()
                .map(this::toDetailDTO)
                .collect(Collectors.toList());

        return PageResponse.of(page, pageSize, orderPage.getTotalElements(), dtoList);
    }

    private String generateOrderNo() {
        LocalDateTime now = LocalDateTime.now();
        String dateStr = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomStr = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "ORD" + dateStr + randomStr;
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

    private OrderDetailDTO toDetailDTO(Order order) {
        OrderDetailDTO dto = new OrderDetailDTO();
        dto.setId(order.getId());
        dto.setOrderNo(order.getOrderNo());
        dto.setStatus(order.getStatus());
        dto.setPayStatus(order.getPayStatus());
        dto.setRefundStatus(order.getRefundStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setItemCount(order.getItemCount());
        dto.setBuyerId(order.getBuyerId());
        dto.setBuyerName(order.getBuyerName());
        dto.setShopId(order.getShopId());
        dto.setShopName(order.getShopName());
        dto.setCreatedAt(order.getCreatedAt().format(DATE_FORMATTER));

        List<OrderDetailDTO.OrderItemDTO> items = order.getItems().stream()
                .map(item -> {
                    OrderDetailDTO.OrderItemDTO itemDTO = new OrderDetailDTO.OrderItemDTO();
                    itemDTO.setProductId(item.getProductId());
                    itemDTO.setTitle(item.getTitle());
                    itemDTO.setImage(item.getImage());
                    itemDTO.setQuantity(item.getQuantity());
                    itemDTO.setPrice(item.getPrice());
                    return itemDTO;
                })
                .collect(Collectors.toList());
        dto.setItems(items);

        return dto;
    }
}
