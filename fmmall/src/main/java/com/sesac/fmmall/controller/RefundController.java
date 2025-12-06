package com.sesac.fmmall.controller;
//package com.sesac.fmmall.Controller;

import com.sesac.fmmall.DTO.Refund.RefundCreateRequest;
import com.sesac.fmmall.DTO.Refund.RefundResponse;
import com.sesac.fmmall.Service.RefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Refund")
@RequiredArgsConstructor
public class RefundController {

    private final RefundService refundService;

    /* Refund/insert/{userId} / POST / 환불 요청 생성 */
    @PostMapping("/insert/{userId}")
    public ResponseEntity<RefundResponse> insertRefund(
            @PathVariable Integer userId,
            @RequestBody RefundCreateRequest request
    ) {
        RefundResponse response = refundService.createRefund(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /* Refund/findAll/{userId} / GET / 사용자 본인의 환불 전체 조회 */
    @GetMapping("/findAll/{userId}")
    public ResponseEntity<List<RefundResponse>> findAllByUser(
            @PathVariable Integer userId
    ) {
        List<RefundResponse> responses = refundService.getRefundsByUser(userId);
        return ResponseEntity.ok(responses);
    }

    /* Refund/findByProduct/{userId}/{productId} / GET / 환불 기록에서 "환불 상품 기준" 조회 */
    @GetMapping("/findByProduct/{userId}/{productId}")
    public ResponseEntity<List<RefundResponse>> findByProduct(
            @PathVariable Integer userId,
            @PathVariable Integer productId
    ) {
        List<RefundResponse> responses = refundService.getRefundsByUserAndProduct(userId, productId);
        return ResponseEntity.ok(responses);
    }

    /* Refund/findOne/{refundId} / GET / 환불 단건 상세 조회 */
    @GetMapping("/findOne/{refundId}")
    public ResponseEntity<RefundResponse> findOne(
            @PathVariable Integer refundId
    ) {
        RefundResponse response = refundService.getRefundDetail(refundId);
        return ResponseEntity.ok(response);
    }
}
