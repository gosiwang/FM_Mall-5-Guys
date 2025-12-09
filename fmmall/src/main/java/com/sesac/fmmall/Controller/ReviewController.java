package com.sesac.fmmall.Controller;

import com.sesac.fmmall.DTO.Review.ReviewModifyRequestDTO;
import com.sesac.fmmall.DTO.Review.ReviewRequestDTO;
import com.sesac.fmmall.DTO.Review.ReviewResponseDTO;
import com.sesac.fmmall.Service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Review", description = "리뷰 API")
@RestController
@RequestMapping("/Review")
@RequiredArgsConstructor
public class ReviewController extends BaseController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 단건 조회", description = "리뷰 ID로 특정 리뷰를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리뷰 조회 성공"),
            @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음")
    })
    @GetMapping("/find/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> findReviewById(@PathVariable int reviewId) {
        ReviewResponseDTO resultReview = reviewService.findReviewByReviewId(reviewId);

        return ResponseEntity.ok(resultReview);
    }

    @Operation(summary = "사용자별 리뷰 목록 조회", description = "특정 사용자가 작성한 모든 리뷰를 최신순으로 페이징하여 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리뷰 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/findByUser/{userId}")
    public ResponseEntity<Page<ReviewResponseDTO>> findReviewByUserIdSortedUpdatedAt(@PathVariable int userId, @RequestParam(defaultValue = "1") int curPage) {
        Page<ReviewResponseDTO> resultInquiryAnswer = reviewService.findReviewByUserIdSortedUpdatedAt(userId, curPage);

        return ResponseEntity.ok(resultInquiryAnswer);
    }

    @Operation(summary = "주문 상품별 리뷰 목록 조회", description = "특정 주문 상품에 대한 모든 리뷰를 최신순으로 페이징하여 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리뷰 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "주문 상품을 찾을 수 없음")
    })
    @GetMapping("/findByOrderItem/{orderItemId}")
    public ResponseEntity<Page<ReviewResponseDTO>> findReviewByOrderItemIdSortedUpdatedAt(@PathVariable int orderItemId, @RequestParam(defaultValue = "1") int curPage) {
        Page<ReviewResponseDTO> resultInquiryAnswer = reviewService.findReviewByOrderItemIdSortedUpdatedAt(orderItemId, curPage);

        return ResponseEntity.ok(resultInquiryAnswer);
    }

    @Operation(summary = "자신의 리뷰 목록 조회", description = "자신이 작성한 모든 리뷰를 최신순으로 페이징하여 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리뷰 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "주문 상품을 찾을 수 없음")
    })
    @GetMapping("/findByUser/me")
    public ResponseEntity<Page<ReviewResponseDTO>> findReviewByUserIdSortedUpdatedAt(@RequestParam(defaultValue = "1") int curPage) {
        Page<ReviewResponseDTO> resultInquiryAnswer = reviewService.findReviewByUserIdSortedUpdatedAt(getCurrentUserId(), curPage);

        return ResponseEntity.ok(resultInquiryAnswer);
    }

    @Operation(summary = "리뷰 등록", description = "새로운 리뷰를 작성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "리뷰 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 필수 필드 누락)"),
            @ApiResponse(responseCode = "404", description = "사용자 또는 주문 상품을 찾을 수 없음")
    })
    @PostMapping("/insert")
    public ResponseEntity<ReviewResponseDTO> insertReview(@RequestBody ReviewRequestDTO requestDTO) {
        ReviewResponseDTO newReview = reviewService.insertReview(getCurrentUserId(), requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(newReview);
    }

    @Operation(summary = "리뷰 수정", description = "기존 리뷰의 내용을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리뷰 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 필수 필드 누락)"),
            @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음")
    })
    @PutMapping("/modify/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> modifyReview(@PathVariable int reviewId, @RequestBody ReviewModifyRequestDTO requestDTO) {
        ReviewResponseDTO updatedReview = reviewService.modifyReviewContent(reviewId, getCurrentUserId(), requestDTO);

        return ResponseEntity.ok(updatedReview);
    }

    @Operation(summary = "리뷰 삭제", description = "리뷰를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "리뷰 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음")
    })
    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable int reviewId) {
        reviewService.deleteReview(reviewId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<Void> deleteAllReview() {
        reviewService.deleteAllReview();
        return ResponseEntity.noContent().build();
    }
}
