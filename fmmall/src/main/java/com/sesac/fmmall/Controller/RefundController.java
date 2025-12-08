package com.sesac.fmmall.Controller;

import com.sesac.fmmall.DTO.Refund.RefundCreateRequest;
import com.sesac.fmmall.DTO.Refund.RefundResponse;
import com.sesac.fmmall.DTO.Refund.RefundSummaryResponse;
import com.sesac.fmmall.Service.RefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Refund")
@RequiredArgsConstructor
public class RefundController extends BaseController {

    private final RefundService refundService;

    /** 환불 신청 - /Refund/insert */
    @PostMapping("/insert")
    public ResponseEntity<RefundResponse> insertRefund(

            @RequestBody RefundCreateRequest request
    ) {
        RefundResponse response = refundService.createRefund(getCurrentUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 로그인 사용자의 환불 내역 전체 조회(요약)
     * - /Refund/findAll
     * - RefundSummaryResponse 사용
     */
    @GetMapping("/findAll")
    public ResponseEntity<List<RefundSummaryResponse>> findAllByUser(

    ) {
        List<RefundSummaryResponse> responses = refundService.getRefundsByUser(getCurrentUserId());
        return ResponseEntity.ok(responses);
    }

    /**
     * 로그인 사용자의 특정 상품 기준 환불 내역 조회(상세)
     * - /Refund/findByProduct/{productId}
     * - RefundResponse 사용
     */
    @GetMapping("/findByProduct/{productId}")
    public ResponseEntity<List<RefundResponse>> findByProduct(
            @PathVariable int productId
    ) {
        List<RefundResponse> responses = refundService.getRefundsByUserAndProduct(getCurrentUserId(), productId);
        return ResponseEntity.ok(responses);
    }

    /**
     * 환불 단건 상세 조회(상세)
     * - /Refund/findOne/{refundId}
     */
    @GetMapping("/findOne/{refundId}")
    public ResponseEntity<RefundResponse> findOne(
            @PathVariable int refundId
    ) {
        RefundResponse response = refundService.getRefundDetail(refundId);
        return ResponseEntity.ok(response);
    }

    /** [관리자] 환불 승인 - /Refund/admin/approve/{refundId} */
    @PutMapping("/admin/approve/{refundId}")
    public ResponseEntity<RefundResponse> approveRefund(
            @PathVariable int refundId
    ) {
        RefundResponse response = refundService.approveRefund(refundId, getCurrentUserId());
        return ResponseEntity.ok(response);
    }

    /** [관리자] 환불 거절 - /Refund/admin/reject/{refundId} */
    @PutMapping("/admin/reject/{refundId}")
    public ResponseEntity<RefundResponse> rejectRefund(
            @PathVariable int refundId
    ) {
        RefundResponse response = refundService.rejectRefund(refundId, getCurrentUserId());
        return ResponseEntity.ok(response);
    }

    /** [관리자] 환불 완료 처리 - /Refund/admin/complete/{refundId} */
    @PutMapping("/admin/complete/{refundId}")
    public ResponseEntity<RefundResponse> completeRefund(
            @PathVariable int refundId
    ) {
        RefundResponse response = refundService.completeRefund(refundId, getCurrentUserId());
        return ResponseEntity.ok(response);
    }
}
