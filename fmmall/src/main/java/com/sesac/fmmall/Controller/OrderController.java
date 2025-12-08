package com.sesac.fmmall.Controller;

import com.sesac.fmmall.DTO.Order.CartOrderCreateRequest;
import com.sesac.fmmall.DTO.Order.OrderCreateRequest;
import com.sesac.fmmall.DTO.Order.OrderResponse;
import com.sesac.fmmall.DTO.Order.OrderSummaryResponse;
import com.sesac.fmmall.Service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Order")
@RequiredArgsConstructor
public class OrderController extends BaseController {

    private final OrderService orderService;

    /**
     * 주문 생성 (주문 + 결제 동시 처리)
     * - /Order/insert   (로그인 사용자 기준)
     */
    @PostMapping("/insert")
    public ResponseEntity<OrderResponse> insertOrder(
            @RequestBody OrderCreateRequest request
    ) {
        OrderResponse response = orderService.createOrder(getCurrentUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/insertFromCart")
    public ResponseEntity<OrderResponse> insertOrderFromCart(
            @RequestBody CartOrderCreateRequest request
    ) {
        OrderResponse response = orderService.createOrderFromCart(getCurrentUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 로그인 사용자의 주문 전체 조회 (요약)
     * - /Order/findAll
     */
    @GetMapping("/findAll")
    public ResponseEntity<List<OrderSummaryResponse>> findAllByUser(

    ) {
        List<OrderSummaryResponse> responses = orderService.getOrdersByUser(getCurrentUserId());
        return ResponseEntity.ok(responses);
    }

    /**
     * 로그인 사용자의 특정 주문 단건 상세 조회
     * - /Order/findOne/{orderId}
     */
    @GetMapping("/findOne/{orderId}")
    public ResponseEntity<OrderResponse> findOne(
            @PathVariable Integer orderId

    ) {
        OrderResponse response = orderService.getOrderDetail(orderId, getCurrentUserId());
        return ResponseEntity.ok(response);
    }

    /**
     * 로그인 사용자의 특정 상품에 대한 주문 목록 조회 (상세)
     * - /Order/findByProduct/{productId}
     */
    @GetMapping("/findByProduct/{productId}")
    public ResponseEntity<List<OrderResponse>> findByProduct(
            @PathVariable Integer productId

    ) {
        List<OrderResponse> responses = orderService.getOrdersByUserAndProduct(getCurrentUserId(), productId);
        return ResponseEntity.ok(responses);
    }

    /**
     * 로그인 사용자의 주문 취소
     * - /Order/cancel/{orderId}
     */
    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable Integer orderId

    ) {
        orderService.cancelOrder(orderId, getCurrentUserId());
        return ResponseEntity.noContent().build();
    }
}
