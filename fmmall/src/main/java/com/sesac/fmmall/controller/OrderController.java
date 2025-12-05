package com.sesac.fmmall.Controller;

import com.sesac.fmmall.DTO.Order.OrderCreateRequest;
import com.sesac.fmmall.DTO.Order.OrderResponse;
import com.sesac.fmmall.Service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /* Order/insert/{userId} / POST / 주문 생성 (주문 + 결제 동시 처리) */
    @PostMapping("/insert/{userId}")
    public ResponseEntity<OrderResponse> insertOrder(
            @PathVariable Integer userId,
            @RequestBody OrderCreateRequest request
    ) {
        OrderResponse response = orderService.createOrder(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /* Order/findAll/{userId} / GET / 사용자 본인의 주문 전체 조회 */
    @GetMapping("/findAll/{userId}")
    public ResponseEntity<List<OrderResponse>> findAllByUser(@PathVariable Integer userId) {

        List<OrderResponse> responses = orderService.getOrdersByUser(userId);
        return ResponseEntity.ok(responses);
    }

    /* Order/findOne/{orderId}/{userId} / GET / 사용자 본인의 주문 단건 상세 조회 */
    @GetMapping("/findOne/{orderId}/{userId}")
    public ResponseEntity<OrderResponse> findOne(
            @PathVariable Integer orderId,
            @PathVariable Integer userId
    ) {
        OrderResponse response = orderService.getOrderDetail(orderId, userId);
        return ResponseEntity.ok(response);
    }

    /* Order/findByProduct/{userId}/{productId} / GET / "주문 상품 기준" 조회 */
    @GetMapping("/findByProduct/{userId}/{productId}")
    public ResponseEntity<List<OrderResponse>> findByProduct(
            @PathVariable Integer userId,
            @PathVariable Integer productId
    ) {
        List<OrderResponse> responses = orderService.getOrdersByUserAndProduct(userId, productId);
        return ResponseEntity.ok(responses);
    }

    /* Order/findByOrderItem/{orderItemId}/{userId} / GET / 주문상품 기준 주문 정보 조회 */
    @GetMapping("/findByOrderItem/{orderItemId}/{userId}")
    public ResponseEntity<OrderResponse> findByOrderItem(
            @PathVariable Integer orderItemId,
            @PathVariable Integer userId
    ) {
        OrderResponse response = orderService.getOrderByOrderItem(orderItemId, userId);
        return ResponseEntity.ok(response);
    }

    /* Order/cancel/{orderId}/{userId} / PUT / 주문 취소 */
    @PutMapping("/cancel/{orderId}/{userId}")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable Integer orderId,
            @PathVariable Integer userId
    ) {
        orderService.cancelOrder(orderId, userId);
        return ResponseEntity.noContent().build();
    }
}
