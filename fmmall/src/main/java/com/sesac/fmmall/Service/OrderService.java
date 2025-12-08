package com.sesac.fmmall.Service;

import com.sesac.fmmall.DTO.Order.OrderCreateRequest;
import com.sesac.fmmall.DTO.Order.OrderItemCreateRequest;
import com.sesac.fmmall.DTO.Order.OrderItemResponse;
import com.sesac.fmmall.DTO.Order.OrderResponse;
import com.sesac.fmmall.DTO.Order.OrderSummaryResponse;
import com.sesac.fmmall.DTO.Refund.RefundSummaryResponse;
import com.sesac.fmmall.DTO.Settlement.PaymentSummaryResponse;
import com.sesac.fmmall.Entity.Address;
import com.sesac.fmmall.Entity.Order;
import com.sesac.fmmall.Entity.OrderItem;
import com.sesac.fmmall.Entity.Payment;
import com.sesac.fmmall.Entity.PaymentMethod;
import com.sesac.fmmall.Entity.Product;
import com.sesac.fmmall.Entity.Refund;
import com.sesac.fmmall.Entity.User;
import com.sesac.fmmall.Repository.AddressRepository;
import com.sesac.fmmall.Repository.OrderItemRepository;
import com.sesac.fmmall.Repository.OrderRepository;
import com.sesac.fmmall.Repository.PaymentMethodRepository;
import com.sesac.fmmall.Repository.PaymentRepository;
import com.sesac.fmmall.Repository.ProductRepository;
import com.sesac.fmmall.Repository.UserRepository;
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
    private final AddressRepository addressRepository;
    private final PaymentMethodRepository paymentMethodRepository;

    private final ModelMapper modelMapper;

    /* Order/insert/{userId} - 주문 생성 (주문 + 결제 동시 처리) */
    @Transactional
    public OrderResponse createOrder(Integer userId, OrderCreateRequest request) {

        // 1. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. userId=" + userId));

        // 2. 주문 상품 검증
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("주문 상품이 없습니다.");
        }

        // 3. 배송지 선택 (addressId 있으면 해당 주소, 없으면 기본 배송지 사용)
        Address shippingAddress;
        if (request.getAddressId() != null) {
            shippingAddress = addressRepository.findById(request.getAddressId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "배송지 정보가 존재하지 않습니다. addressId=" + request.getAddressId()));

            if (shippingAddress.getUser().getUserId() != userId) {
                throw new IllegalArgumentException("본인의 배송지 정보만 사용할 수 있습니다.");
            }

        } else {
            shippingAddress = addressRepository
                    .findByUser_UserIdAndIsDefault(userId, "Y")
                    .orElseThrow(() -> new IllegalArgumentException(
                            "기본 배송지가 설정되어 있지 않습니다. addressId를 지정하거나 기본 배송지를 등록해주세요."));
        }

        // 4. 주문 엔티티 생성 (주소 정보는 Address에서 가져옴)
        Order order = Order.builder()
                .receiverName(shippingAddress.getReceiverName())
                .receiverPhone(shippingAddress.getReceiverPhone())
                .zipcode(shippingAddress.getZipcode())
                .address1(shippingAddress.getAddress1())
                .address2(shippingAddress.getAddress2())
                .totalPrice(0)   // 나중에 계산
                .deliveryTrackingNumber(null)
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();

        // 5. 주문상품 생성 + 재고 체크/차감
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

            // 재고 차감
            product.setStockQuantity(product.getStockQuantity() - qty);

            // OrderItem 생성
            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(qty)
                    .deliveryDate(null)
                    .installationDate(null)
                    .build();

            // 양방향 연관 관계 설정
            order.addOrderItem(orderItem);
        }

        // 6. 주문 총 금액 계산
        order.setTotalPrice(order.calculateTotalPrice());

        // 7. 주문 저장
        Order savedOrder = orderRepository.save(order);

        // 8. 결제수단 선택 (paymentMethodId 있으면 그 카드, 없으면 기본 카드)
        PaymentMethod paymentMethod;
        if (request.getPaymentMethodId() != null) {
            paymentMethod = paymentMethodRepository.findById(request.getPaymentMethodId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "결제수단 정보가 존재하지 않습니다. paymentMethodId=" + request.getPaymentMethodId()));

            if (paymentMethod.getUser().getUserId() != userId) {
                throw new IllegalArgumentException("본인의 결제수단만 사용할 수 있습니다.");
            }
        } else {
            paymentMethod = paymentMethodRepository
                    .findByUser_UserIdAndIsDefault(userId, true)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "기본 결제수단이 설정되어 있지 않습니다. paymentMethodId를 지정하거나 기본 결제수단을 등록해주세요."));
        }

        // 9. 결제 생성 (결제 성공 가정)
        Payment payment = Payment.builder()
                .paymentMethodType(paymentMethod.getCardCompany())  // 예: "HyundaiCard"
                .paidAt(LocalDateTime.now())
                .order(savedOrder)
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        savedOrder.setPayment(savedPayment);

        return mapToOrderResponse(savedOrder);
    }

    /* Order/findAll/{userId} - 사용자 본인의 주문 전체 조회 (요약) */
    @Transactional
    public List<OrderSummaryResponse> getOrdersByUser(Integer userId) {

        List<Order> orders = orderRepository.findByUser_UserId(userId);

        return orders.stream()
                .map(this::mapToOrderSummaryResponse)
                .collect(Collectors.toList());
    }

    /* Order/findOne/{orderId}/{userId} - 사용자 본인의 주문 단건 상세 조회 (상세) */
    @Transactional
    public OrderResponse getOrderDetail(Integer orderId, Integer userId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다. orderId=" + orderId));

        if (order.getUser().getUserId() != userId) {
            throw new IllegalArgumentException("본인의 주문만 조회할 수 있습니다.");
        }

        return mapToOrderResponse(order);
    }

    /* Order/findByProduct/{userId}/{productId} - "주문 상품 기준" 조회 (상세) */
    @Transactional
    public List<OrderResponse> getOrdersByUserAndProduct(Integer userId, Integer productId) {

        // productId 기준으로 주문상품 조회
        List<OrderItem> orderItems = orderItemRepository.findByProduct_ProductId(productId);

        // userId로 필터링 + 주문 중복 제거
        Map<Integer, Order> uniqueOrders = new LinkedHashMap<>();

        for (OrderItem item : orderItems) {
            Order order = item.getOrder();
            if (order.getUser().getUserId() == userId) {
                uniqueOrders.putIfAbsent(order.getOrderId(), order);
            }
        }

        return uniqueOrders.values().stream()
                .map(this::mapToOrderResponse)   // 상세 DTO로 매핑
                .collect(Collectors.toList());
    }

    /* Order/cancel/{orderId}/{userId} - 주문 취소 (결제 직후 ~ 배송완료 직전) */
    @Transactional
    public void cancelOrder(Integer orderId, Integer userId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다. orderId=" + orderId));

        if (order.getUser().getUserId() != userId) {
            throw new IllegalArgumentException("본인의 주문만 취소할 수 있습니다.");
        }

        // 환불 이력이 있으면 취소 불가
        if (!order.getRefunds().isEmpty()) {
            throw new IllegalStateException("환불 이력이 있는 주문은 취소할 수 없습니다.");
        }

        LocalDate today = LocalDate.now();

        // 배송일 기준 취소 가능 여부 판단 (배송일이 null이거나 오늘 이후인 경우만 취소 가능)
        boolean canCancel = order.getOrderItems().stream()
                .allMatch(item ->
                        item.getDeliveryDate() == null ||
                                item.getDeliveryDate().isAfter(today)
                );

        if (!canCancel) {
            throw new IllegalStateException("이미 배송이 시작되었거나 완료된 상품이 있어 주문 취소가 불가능합니다. 환불을 이용해주세요.");
        }

        // 재고 복구
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
        }

        // 결제 삭제
        Payment payment = order.getPayment();
        if (payment != null) {
            paymentRepository.delete(payment);
            order.setPayment(null);
        }

        // 주문 삭제 (OrderItem, Refund는 cascade + orphanRemoval로 함께 삭제)
        orderRepository.delete(order);
    }

    // ===================== 매핑 메서드 ===================== //

    /* 상세 조회용 매핑 */
    private OrderResponse mapToOrderResponse(Order order) {

        // 기본 필드는 ModelMapper로 매핑
        OrderResponse dto = modelMapper.map(order, OrderResponse.class);

        // 이름이 다른 필드 수동 보정
        dto.setOrderId(order.getOrderId());
        dto.setUserId(order.getUser().getUserId());

        // 주문상품 리스트 매핑
        List<OrderItemResponse> itemDtos = order.getOrderItems().stream()
                .map(this::mapToOrderItemResponse)
                .collect(Collectors.toList());
        dto.setItems(itemDtos);

        // 결제 요약 매핑
        PaymentSummaryResponse paymentDto = null;
        if (order.getPayment() != null) {
            paymentDto = modelMapper.map(order.getPayment(), PaymentSummaryResponse.class);
            paymentDto.setPaymentId(order.getPayment().getPaymentId());
        }
        dto.setPayment(paymentDto);

        // 환불 요약 리스트 매핑
        List<RefundSummaryResponse> refundDtos = order.getRefunds().stream()
                .map(this::mapToRefundSummaryResponse)
                .collect(Collectors.toList());
        dto.setRefunds(refundDtos);

        return dto;
    }

    /* 목록(요약) 조회용 매핑 */
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
