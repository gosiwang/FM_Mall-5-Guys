package com.sesac.fmmall.Controller;

import com.sesac.fmmall.DTO.Order.OrderCreateRequest;
import com.sesac.fmmall.DTO.Order.OrderResponse;
import com.sesac.fmmall.DTO.Order.OrderSummaryResponse;
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


    @PostMapping("/insert/{userId}")
    public ResponseEntity<OrderResponse> insertOrder(
            @PathVariable Integer userId,
            @RequestBody OrderCreateRequest request
    ) {
        OrderResponse response = orderService.createOrder(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/findAll/{userId}")
    public ResponseEntity<List<OrderSummaryResponse>> findAllByUser(
            @PathVariable Integer userId
    ) {
        List<OrderSummaryResponse> responses = orderService.getOrdersByUser(userId);
        return ResponseEntity.ok(responses);
    }


    @GetMapping("/findOne/{orderId}/{userId}")
    public ResponseEntity<OrderResponse> findOne(
            @PathVariable Integer orderId,
            @PathVariable Integer userId
    ) {
        OrderResponse response = orderService.getOrderDetail(orderId, userId);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/findByProduct/{userId}/{productId}")
    public ResponseEntity<List<OrderResponse>> findByProduct(
            @PathVariable Integer userId,
            @PathVariable Integer productId
    ) {
        List<OrderResponse> responses = orderService.getOrdersByUserAndProduct(userId, productId);
        return ResponseEntity.ok(responses);
    }


    @PutMapping("/cancel/{orderId}/{userId}")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable Integer orderId,
            @PathVariable Integer userId
    ) {
        orderService.cancelOrder(orderId, userId);
        return ResponseEntity.noContent().build();
    }
}
