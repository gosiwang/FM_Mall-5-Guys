package com.sesac.fmmall.Service;

import com.sesac.fmmall.Constant.RefundStatus;
import com.sesac.fmmall.Constant.RefundType;
import com.sesac.fmmall.Constant.YesNo;
import com.sesac.fmmall.DTO.Refund.RefundCreateRequest;
import com.sesac.fmmall.DTO.Refund.RefundItemCreateRequest;
import com.sesac.fmmall.DTO.Refund.RefundItemResponse;
import com.sesac.fmmall.DTO.Refund.RefundResponse;
import com.sesac.fmmall.Entity.*;
import com.sesac.fmmall.Repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RefundService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;
    private final RefundItemRepository refundItemRepository;

    private final ModelMapper modelMapper;

    /* Refund/insert/{userId} / POST / 환불 요청 생성 */
    @Transactional
    public RefundResponse createRefund(Integer userId, RefundCreateRequest request) {

        // 주문 조회
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다. orderId=" + request.getOrderId()));

        if (order.getUser().getId() != userId) {
            throw new IllegalArgumentException("본인의 주문에 대해서만 환불을 요청할 수 있습니다.");
        }

        // 결제 조회
        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new IllegalArgumentException("결제 정보가 존재하지 않습니다. paymentId=" + request.getPaymentId()));

        if (payment.getOrder().getId() != order.getId()) {
            throw new IllegalArgumentException("주문과 결제 정보가 일치하지 않습니다.");
        }

        // 환불 타입 파싱 (FULL / PARTIAL)
        RefundType refundType = RefundType.valueOf(request.getRefundType());

        Refund refund = Refund.builder()
                .reasonCode(request.getReasonCode())
                .reasonDetail(request.getReasonDetail())
                .totalAmount(0)
                .refundType(refundType)
                .isTrue(YesNo.N)   // 초기 상태: 확정 전
                .order(order)
                .payment(payment)
                .build();

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("환불 상품이 없습니다.");
        }

        // 환불아이템 생성
        for (RefundItemCreateRequest itemReq : request.getItems()) {

            OrderItem orderItem = orderItemRepository.findById(itemReq.getOrderItemId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "주문 상품이 존재하지 않습니다. orderItemId=" + itemReq.getOrderItemId()));

            if (orderItem.getOrder().getId() != order.getId()) {
                throw new IllegalArgumentException("해당 주문에 속하지 않는 주문상품입니다. orderItemId=" + orderItem.getId());
            }

            Integer refundQuantity = itemReq.getRefundQuantity();
            if (refundQuantity == null || refundQuantity < 1) {
                throw new IllegalArgumentException("환불 수량은 1개 이상이어야 합니다.");
            }

            // 이미 환불된 수량 계산
            int alreadyRefunded = refundItemRepository.findByOrderItem(orderItem).stream()
                    .mapToInt(RefundItem::getRefundQuantity)
                    .sum();

            if (alreadyRefunded + refundQuantity > orderItem.getQuantity()) {
                throw new IllegalArgumentException(
                        "환불 수량이 주문 수량을 초과합니다. orderItemId=" + orderItem.getId());
            }

            int productPrice = orderItem.getProduct().getPrice();
            int refundPrice = productPrice * refundQuantity;

            RefundItem refundItem = RefundItem.builder()
                    .orderItem(orderItem)
                    .refundQuantity(refundQuantity)
                    .refundPrice(refundPrice)
                    .refundStatus(RefundStatus.REQUESTED)
                    .refund(refund)
                    .build();

            refund.addRefundItem(refundItem);
        }

        // 총 환불 금액 계산
        refund.setTotalAmount(refund.calculateTotalAmount());

        Refund savedRefund = refundRepository.save(refund);

        return mapToRefundResponse(savedRefund);
    }

    /* Refund/findAll/{userId} / GET / 사용자 본인의 환불 전체 조회 */
    @Transactional
    public List<RefundResponse> getRefundsByUser(Integer userId) {

        // userId 기준으로 주문 조회 → 각 주문의 환불 목록
        List<Order> orders = orderRepository.findByUser_Id(userId);

        List<Refund> allRefunds = new ArrayList<>();
        for (Order order : orders) {
            allRefunds.addAll(order.getRefunds());
        }

        return allRefunds.stream()
                .map(this::mapToRefundResponse)
                .collect(Collectors.toList());
    }

    /* Refund/findByProduct/{userId}/{productId} / GET / 환불 기록에서 "환불 상품 기준" 조회 */
    @Transactional
    public List<RefundResponse> getRefundsByUserAndProduct(Integer userId, Integer productId) {

        // productId로 주문상품 찾기
        List<OrderItem> orderItems = orderItemRepository.findByProduct_Id(productId);

        Set<Integer> refundIdSet = new LinkedHashSet<>();
        List<Refund> resultRefunds = new ArrayList<>();

        for (OrderItem orderItem : orderItems) {
            // 해당 주문이 userId의 주문인지 확인
            if (orderItem.getOrder().getUser().getId() != userId) {
                continue;
            }

            // 주문상품에 연결된 환불아이템들 조회
            List<RefundItem> refundItems = refundItemRepository.findByOrderItem(orderItem);
            for (RefundItem refundItem : refundItems) {
                Refund refund = refundItem.getRefund();
                if (refundIdSet.add(refund.getId())) {
                    resultRefunds.add(refund);
                }
            }
        }

        return resultRefunds.stream()
                .map(this::mapToRefundResponse)
                .collect(Collectors.toList());
    }

    /* Refund/findOne/{refundId} / GET / 환불 단건 상세 조회 */
    @Transactional
    public RefundResponse getRefundDetail(Integer refundId) {

        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "환불 정보가 존재하지 않습니다. refundId=" + refundId));

        return mapToRefundResponse(refund);
    }

    // ===================== 매핑 메서드 ===================== //

    private RefundResponse mapToRefundResponse(Refund refund) {

        RefundResponse dto = modelMapper.map(refund, RefundResponse.class);

        dto.setRefundId(refund.getId());
        dto.setReasonCode(refund.getReasonCode());
        dto.setReasonDetail(refund.getReasonDetail());
        dto.setTotalAmount(refund.getTotalAmount());
        dto.setRefundType(refund.getRefundType().name());
        dto.setIsTrue(refund.getIsTrue().name());
        dto.setOrderId(refund.getOrder().getId());
        dto.setPaymentId(refund.getPayment().getId());

        List<RefundItemResponse> itemDtos = refund.getRefundItems().stream()
                .map(this::mapToRefundItemResponse)
                .collect(Collectors.toList());
        dto.setItems(itemDtos);

        return dto;
    }

    private RefundItemResponse mapToRefundItemResponse(RefundItem item) {

        RefundItemResponse dto = modelMapper.map(item, RefundItemResponse.class);

        dto.setRefundItemId(item.getId());
        dto.setOrderItemId(item.getOrderItem().getId());
        dto.setRefundQuantity(item.getRefundQuantity());
        dto.setRefundPrice(item.getRefundPrice());
        dto.setRefundStatus(item.getRefundStatus().name());

        return dto;
    }
}
