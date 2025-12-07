package com.sesac.fmmall.Service;

import com.sesac.fmmall.Constant.RefundReasonCode;
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

        // 1. 주문 조회 및 본인 주문 검증
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다. orderId=" + request.getOrderId()));

        if (order.getUser().getUserId() != userId) {
            throw new IllegalArgumentException("본인의 주문에 대해서만 환불을 요청할 수 있습니다.");
        }

        // 2. 결제 조회 및 주문-결제 일치 여부 검증
        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new IllegalArgumentException("결제 정보가 존재하지 않습니다. paymentId=" + request.getPaymentId()));

        if (payment.getOrder().getOrderId() != order.getOrderId()) {
            throw new IllegalArgumentException("주문과 결제 정보가 일치하지 않습니다.");
        }

        // 3. 환불 사유 코드 검증 (RefundReasonCode)
        RefundReasonCode reasonCodeEnum;
        try {
            reasonCodeEnum = RefundReasonCode.valueOf(request.getReasonCode());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "유효하지 않은 환불 사유 코드입니다. reasonCode=" + request.getReasonCode()
            );
        }

        // 4. 환불 타입 파싱 (FULL / PARTIAL)
        RefundType refundType;
        try {
            refundType = RefundType.valueOf(request.getRefundType());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "유효하지 않은 환불 타입입니다. refundType=" + request.getRefundType()
            );
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("환불 상품이 없습니다.");
        }

        // 이미 환불된 수량 / 이번에 요청한 수량을 orderItemId 기준으로 모아둘 맵
        Map<Integer, Integer> alreadyRefundedByOrderItemId = new HashMap<>();
        Map<Integer, Integer> requestedQtyByOrderItemId = new HashMap<>();

        // 5. Refund 엔티티 기본 생성 (금액 = 0, 상태는 하위 RefundItem으로 관리)
        Refund refund = Refund.builder()
                .reasonCode(reasonCodeEnum.name())        // enum 이름을 그대로 저장
                .reasonDetail(request.getReasonDetail())
                .totalAmount(0)
                .refundType(refundType)
                .isTrue(YesNo.N)                         // 최초 생성 시: 최종완료 아님
                .order(order)
                .payment(payment)
                .build();

        // 6. RefundItem 생성
        for (RefundItemCreateRequest itemReq : request.getItems()) {

            OrderItem orderItem = orderItemRepository.findById(itemReq.getOrderItemId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "주문 상품이 존재하지 않습니다. orderItemId=" + itemReq.getOrderItemId()));

            if (orderItem.getOrder().getOrderId() != order.getOrderId()) {
                throw new IllegalArgumentException("해당 주문에 속하지 않는 주문상품입니다. orderItemId=" + orderItem.getOrderItemId());
            }

            Integer refundQuantity = itemReq.getRefundQuantity();
            if (refundQuantity == null || refundQuantity < 1) {
                throw new IllegalArgumentException("환불 수량은 1개 이상이어야 합니다.");
            }

            // 이미 환불된 수량 계산 (캐싱)
            int orderItemId = orderItem.getOrderItemId();
            int alreadyRefunded = alreadyRefundedByOrderItemId.computeIfAbsent(
                    orderItemId,
                    id -> refundItemRepository.findByOrderItem(orderItem).stream()
                            .mapToInt(RefundItem::getRefundQuantity)
                            .sum()
            );

            // "이번 요청으로 환불될 수량"을 누적
            requestedQtyByOrderItemId.merge(orderItemId, refundQuantity, Integer::sum);

            // (기존 환불 + 이번 요청 수량) 이 주문 원래 수량을 초과하면 안됨
            int afterTotal = alreadyRefunded + requestedQtyByOrderItemId.get(orderItemId);
            if (afterTotal > orderItem.getQuantity()) {
                throw new IllegalArgumentException(
                        "환불 수량이 주문 수량을 초과합니다. orderItemId=" + orderItemId);
            }

            int productPrice = orderItem.getProduct().getPrice();
            int refundPrice = productPrice * refundQuantity;

            RefundItem refundItem = RefundItem.builder()
                    .orderItem(orderItem)
                    .refundQuantity(refundQuantity)
                    .refundPrice(refundPrice)
                    .refundStatus(RefundStatus.REQUESTED)   // 최초 상태: REQUESTED(요청됨)
                    .refund(refund)
                    .build();

            refund.addRefundItem(refundItem);
        }

        // 7. FULL / PARTIAL 유효성 검증
        boolean isFullRefund = isFullRefundForOrder(order, alreadyRefundedByOrderItemId, requestedQtyByOrderItemId);

        if (refundType == RefundType.FULL && !isFullRefund) {
            throw new IllegalArgumentException("환불 타입이 FULL이지만, 주문 전체 수량이 모두 환불되도록 선택되지 않았습니다.");
        }

        if (refundType == RefundType.PARTIAL && isFullRefund) {
            throw new IllegalArgumentException("환불 타입이 PARTIAL인데, 결과적으로 주문 전체가 모두 환불되도록 요청되었습니다.");
        }

        // 8. 총 환불 금액 계산 후 저장
        refund.setTotalAmount(refund.calculateTotalAmount());

        Refund savedRefund = refundRepository.save(refund);

        return mapToRefundResponse(savedRefund);
    }

    /**
     * 주문 기준으로 "이번 요청까지 포함했을 때 전체가 다 환불되는지" 판단하는 헬퍼 메서드
     */
    private boolean isFullRefundForOrder(
            Order order,
            Map<Integer, Integer> alreadyRefundedByOrderItemId,
            Map<Integer, Integer> requestedQtyByOrderItemId
    ) {
        for (OrderItem orderItem : order.getOrderItems()) {

            int orderItemId = orderItem.getOrderItemId();

            int alreadyRefunded = alreadyRefundedByOrderItemId.getOrDefault(orderItemId, 0);
            int requestedQty = requestedQtyByOrderItemId.getOrDefault(orderItemId, 0);

            int afterTotal = alreadyRefunded + requestedQty;

            // 주문원 수량과 정확히 같아야 "그 라인 전체 환불"
            // 하나라도 덜 환불되는 라인이 있으면 "전체 환불"이 아님
            if (afterTotal != orderItem.getQuantity()) {
                return false;
            }
        }
        return true;
    }

    /* Refund/findAll/{userId} / GET / 사용자 본인의 환불 전체 조회 */
    @Transactional
    public List<RefundResponse> getRefundsByUser(Integer userId) {

        // userId 기준으로 주문 조회 → 각 주문의 환불 목록
        List<Order> orders = orderRepository.findByUser_UserId(userId);

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
        List<OrderItem> orderItems = orderItemRepository.findByProduct_ProductId(productId);

        Set<Integer> refundIdSet = new LinkedHashSet<>();
        List<Refund> resultRefunds = new ArrayList<>();

        for (OrderItem orderItem : orderItems) {
            // 해당 주문이 userId의 주문인지 확인
            if (orderItem.getOrder().getUser().getUserId() != userId) {
                continue;
            }

            // 주문상품에 연결된 환불아이템들 조회
            List<RefundItem> refundItems = refundItemRepository.findByOrderItem(orderItem);
            for (RefundItem refundItem : refundItems) {
                Refund refund = refundItem.getRefund();
                if (refundIdSet.add(refund.getRefundId())) {
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

    // ===================== 관리자용 상태 변경 메서드 ===================== //

    /* (관리자) 환불 승인: REQUESTED → APPROVED */
    @Transactional
    public RefundResponse approveRefund(Integer refundId) {

        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new IllegalArgumentException("환불 정보가 존재하지 않습니다. refundId=" + refundId));

        for (RefundItem item : refund.getRefundItems()) {
            if (item.getRefundStatus() != RefundStatus.REQUESTED) {
                throw new IllegalStateException(
                        "REQUESTED 상태가 아닌 환불아이템이 포함되어 있어 승인할 수 없습니다. refundItemId=" + item.getRefundItemId());
            }
            item.changeStatus(RefundStatus.APPROVED);
        }

        // 승인했다고 해서 isTrue를 Y로 바꾸지는 않음 (실제 PG 환불 완료 시 COMPLETE에서 변경)
        return mapToRefundResponse(refund);
    }

    /* (관리자) 환불 거절: REQUESTED → REJECTED */
    @Transactional
    public RefundResponse rejectRefund(Integer refundId) {

        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new IllegalArgumentException("환불 정보가 존재하지 않습니다. refundId=" + refundId));

        for (RefundItem item : refund.getRefundItems()) {
            if (item.getRefundStatus() != RefundStatus.REQUESTED) {
                throw new IllegalStateException(
                        "REQUESTED 상태가 아닌 환불아이템이 포함되어 있어 거절할 수 없습니다. refundItemId=" + item.getRefundItemId());
            }
            item.changeStatus(RefundStatus.REJECTED);
        }

        // 거절 시 isTrue는 여전히 N 유지
        refund.setIsTrue(YesNo.N);

        return mapToRefundResponse(refund);
    }

    /* (관리자 or 배치) 환불 완료 처리: APPROVED → COMPLETED, isTrue = Y */
    @Transactional
    public RefundResponse completeRefund(Integer refundId) {

        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new IllegalArgumentException("환불 정보가 존재하지 않습니다. refundId=" + refundId));

        for (RefundItem item : refund.getRefundItems()) {
            if (item.getRefundStatus() != RefundStatus.APPROVED
                    && item.getRefundStatus() != RefundStatus.COMPLETED) {
                throw new IllegalStateException(
                        "APPROVED 상태가 아닌 환불아이템이 포함되어 있어 완료 처리할 수 없습니다. refundItemId=" + item.getRefundItemId());
            }
            // 이미 COMPLETED면 그대로 두고, APPROVED면 COMPLETED로 변경
            item.changeStatus(RefundStatus.COMPLETED);
        }

        // 실제 PG 환불까지 완료되었다고 보고 isTrue = Y로 설정
        refund.setIsTrue(YesNo.Y);

        return mapToRefundResponse(refund);
    }

    // ===================== 매핑 메서드 ===================== //

    private RefundResponse mapToRefundResponse(Refund refund) {

        RefundResponse dto = modelMapper.map(refund, RefundResponse.class);

        dto.setRefundId(refund.getRefundId());
        dto.setReasonCode(refund.getReasonCode());
        dto.setReasonDetail(refund.getReasonDetail());
        dto.setTotalAmount(refund.getTotalAmount());
        dto.setRefundType(refund.getRefundType().name());
        dto.setIsTrue(refund.getIsTrue().name());
        dto.setOrderId(refund.getOrder().getOrderId());
        dto.setPaymentId(refund.getPayment().getPaymentId());

        List<RefundItemResponse> itemDtos = refund.getRefundItems().stream()
                .map(this::mapToRefundItemResponse)
                .collect(Collectors.toList());
        dto.setItems(itemDtos);

        return dto;
    }

    private RefundItemResponse mapToRefundItemResponse(RefundItem item) {

        RefundItemResponse dto = modelMapper.map(item, RefundItemResponse.class);

        dto.setRefundItemId(item.getRefundItemId());
        dto.setOrderItemId(item.getOrderItem().getOrderItemId());
        dto.setRefundQuantity(item.getRefundQuantity());
        dto.setRefundPrice(item.getRefundPrice());
        dto.setRefundStatus(item.getRefundStatus().name());

        return dto;
    }
}
