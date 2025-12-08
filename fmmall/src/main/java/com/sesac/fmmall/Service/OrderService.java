package com.sesac.fmmall.Service;

import com.sesac.fmmall.DTO.Order.OrderCreateRequest;
import com.sesac.fmmall.DTO.Order.OrderItemCreateRequest;
import com.sesac.fmmall.DTO.Order.OrderItemResponse;
import com.sesac.fmmall.DTO.Order.OrderResponse;
import com.sesac.fmmall.DTO.Order.OrderSummaryResponse;
import com.sesac.fmmall.DTO.Refund.RefundSummaryResponse;
import com.sesac.fmmall.DTO.Settlement.PaymentSummaryResponse;
import com.sesac.fmmall.Entity.*;
import com.sesac.fmmall.Repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;

    private final ModelMapper modelMapper;


    @Transactional
    public OrderResponse createOrder(Integer userId, OrderCreateRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. userId=" + userId));

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("주문 상품이 없습니다.");
        }


        Order order = Order.builder()
                .receiverName(request.getReceiverName())
                .receiverPhone(request.getReceiverPhone())
                .zipcode(request.getZipcode())
                .address1(request.getAddress1())
                .address2(request.getAddress2())
                .totalPrice(0)   // 나중에 계산
                .deliveryTrackingNumber(null)
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();


        for (OrderItemCreateRequest itemReq : request.getItems()) {

            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "존재하지 않는 상품입니다. productId=" + itemReq.getProductId()));

            Integer qty = itemReq.getQuantity();
            if (qty == null || qty < 1) {
                throw new IllegalArgumentException("상품 수량은 1개 이상이어야 합니다.");
            }

            if (product.getStockQuantity() < qty) {
                throw new IllegalArgumentException(
                        "상품 재고가 부족합니다. productId=" + product.getProductId()
                                + ", stock=" + product.getStockQuantity()
                                + ", requested=" + qty
                );
            }


            product.setStockQuantity(product.getStockQuantity() - qty);


            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(qty)
                    .deliveryDate(null)
                    .installationDate(null)
                    .build();


            order.addOrderItem(orderItem);
        }


        order.setTotalPrice(order.calculateTotalPrice());


        Order savedOrder = orderRepository.save(order);


        Payment payment = Payment.builder()
                .paymentMethodType(request.getPaymentMethodType())
                .paidAt(LocalDateTime.now())
                .order(savedOrder)
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        savedOrder.setPayment(savedPayment);

        return mapToOrderResponse(savedOrder);
    }


    @Transactional
    public List<OrderSummaryResponse> getOrdersByUser(Integer userId) {

        List<Order> orders = orderRepository.findByUser_UserId(userId);

        return orders.stream()
                .map(this::mapToOrderSummaryResponse)
                .collect(Collectors.toList());
    }


    @Transactional
    public OrderResponse getOrderDetail(Integer orderId, Integer userId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다. orderId=" + orderId));

        if (order.getUser().getUserId() != userId) {
            throw new IllegalArgumentException("본인의 주문만 조회할 수 있습니다.");
        }

        return mapToOrderResponse(order);
    }


    @Transactional
    public List<OrderResponse> getOrdersByUserAndProduct(Integer userId, Integer productId) {


        List<OrderItem> orderItems = orderItemRepository.findByProduct_ProductId(productId);


        Map<Integer, Order> uniqueOrders = new LinkedHashMap<>();

        for (OrderItem item : orderItems) {
            Order order = item.getOrder();
            if (order.getUser().getUserId() == userId) {
                uniqueOrders.putIfAbsent(order.getOrderId(), order);
            }
        }

        return uniqueOrders.values().stream()
                .map(this::mapToOrderResponse)   // ✅ 상세 DTO로 매핑
                .collect(Collectors.toList());
    }


    @Transactional
    public void cancelOrder(Integer orderId, Integer userId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다. orderId=" + orderId));

        if (order.getUser().getUserId() != userId) {
            throw new IllegalArgumentException("본인의 주문만 취소할 수 있습니다.");
        }


        if (!order.getRefunds().isEmpty()) {
            throw new IllegalStateException("환불 이력이 있는 주문은 취소할 수 없습니다.");
        }

        LocalDate today = LocalDate.now();


        boolean canCancel = order.getOrderItems().stream()
                .allMatch(item ->
                        item.getDeliveryDate() == null ||
                                item.getDeliveryDate().isAfter(today)
                );

        if (!canCancel) {
            throw new IllegalStateException("이미 배송이 시작되었거나 완료된 상품이 있어 주문 취소가 불가능합니다. 환불을 이용해주세요.");
        }


        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
        }


        Payment payment = order.getPayment();
        if (payment != null) {
            paymentRepository.delete(payment);
            order.setPayment(null);
        }


        orderRepository.delete(order);
    }


    private OrderResponse mapToOrderResponse(Order order) {


        OrderResponse dto = modelMapper.map(order, OrderResponse.class);


        dto.setOrderId(order.getOrderId());
        dto.setUserId(order.getUser().getUserId());


        List<OrderItemResponse> itemDtos = order.getOrderItems().stream()
                .map(this::mapToOrderItemResponse)
                .collect(Collectors.toList());
        dto.setItems(itemDtos);


        PaymentSummaryResponse paymentDto = null;
        if (order.getPayment() != null) {
            paymentDto = modelMapper.map(order.getPayment(), PaymentSummaryResponse.class);
            paymentDto.setPaymentId(order.getPayment().getPaymentId());
        }
        dto.setPayment(paymentDto);


        List<RefundSummaryResponse> refundDtos = order.getRefunds().stream()
                .map(this::mapToRefundSummaryResponse)
                .collect(Collectors.toList());
        dto.setRefunds(refundDtos);

        return dto;
    }


    private OrderSummaryResponse mapToOrderSummaryResponse(Order order) {

        int totalQuantity = order.getOrderItems().stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();

        List<String> productNames = order.getOrderItems().stream()
                .map(oi -> oi.getProduct().getName())
                .distinct()
                .collect(Collectors.toList());

        return OrderSummaryResponse.builder()
                .orderId(order.getOrderId())
                .totalPrice(order.getTotalPrice())
                .createdAt(order.getCreatedAt())
                .totalQuantity(totalQuantity)
                .productNames(productNames)
                .build();
    }

    private OrderItemResponse mapToOrderItemResponse(OrderItem item) {

        OrderItemResponse dto = modelMapper.map(item, OrderItemResponse.class);

        dto.setOrderItemId(item.getOrderItemId());
        dto.setProductId(item.getProduct().getProductId());
        dto.setProductName(item.getProduct().getName());
        dto.setProductPrice(item.getProduct().getPrice());
        dto.setLineTotalPrice(item.calculateLineTotalPrice());

        return dto;
    }

    private RefundSummaryResponse mapToRefundSummaryResponse(Refund refund) {

        return RefundSummaryResponse.builder()
                .refundId(refund.getRefundId())
                .refundType(refund.getRefundType().name())
                .totalAmount(refund.getTotalAmount())
                .isTrue(refund.getIsTrue().name())
                .build();
    }
}
