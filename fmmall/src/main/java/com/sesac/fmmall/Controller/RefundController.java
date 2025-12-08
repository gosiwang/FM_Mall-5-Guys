package com.sesac.fmmall.Controller;

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


    @PostMapping("/insert/{userId}")
    public ResponseEntity<RefundResponse> insertRefund(
            @PathVariable Integer userId,
            @RequestBody RefundCreateRequest request
    ) {
        RefundResponse response = refundService.createRefund(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/findAll/{userId}")
    public ResponseEntity<List<RefundResponse>> findAllByUser(
            @PathVariable Integer userId
    ) {
        List<RefundResponse> responses = refundService.getRefundsByUser(userId);
        return ResponseEntity.ok(responses);
    }


    @GetMapping("/findByProduct/{userId}/{productId}")
    public ResponseEntity<List<RefundResponse>> findByProduct(
            @PathVariable Integer userId,
            @PathVariable Integer productId
    ) {
        List<RefundResponse> responses = refundService.getRefundsByUserAndProduct(userId, productId);
        return ResponseEntity.ok(responses);
    }


    @GetMapping("/findOne/{refundId}")
    public ResponseEntity<RefundResponse> findOne(
            @PathVariable Integer refundId
    ) {
        RefundResponse response = refundService.getRefundDetail(refundId);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/admin/approve/{refundId}/{adminUserId}")
    public ResponseEntity<RefundResponse> approveRefund(
            @PathVariable Integer refundId,
            @PathVariable Integer adminUserId
    ) {
        RefundResponse response = refundService.approveRefund(refundId, adminUserId);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/admin/reject/{refundId}/{adminUserId}")
    public ResponseEntity<RefundResponse> rejectRefund(
            @PathVariable Integer refundId,
            @PathVariable Integer adminUserId
    ) {
        RefundResponse response = refundService.rejectRefund(refundId, adminUserId);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/admin/complete/{refundId}/{adminUserId}")
    public ResponseEntity<RefundResponse> completeRefund(
            @PathVariable Integer refundId,
            @PathVariable Integer adminUserId
    ) {
        RefundResponse response = refundService.completeRefund(refundId, adminUserId);
        return ResponseEntity.ok(response);
    }
}
