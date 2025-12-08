package com.sesac.fmmall.Controller;

import com.sesac.fmmall.DTO.Inquiry.InquiryResponseDTO;
import com.sesac.fmmall.DTO.Review.ReviewRequestDTO;
import com.sesac.fmmall.DTO.Review.ReviewResponseDTO;
import com.sesac.fmmall.Service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /* 1. 특정 아이디로 조회 */
    @GetMapping("/find/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> findReviewById(@PathVariable int reviewId) {
        ReviewResponseDTO resultReview = reviewService.findReviewByReviewId(reviewId);
        // 상태 코드 200(ok)와 함께 JSON 반환
        return ResponseEntity.ok(resultReview);
    }

//    /* 2. 최신순 정렬(페이징) -> 주문상품, 유저별로 변경해야함. */
//    @GetMapping("/findAll")
//    public ResponseEntity<Page<ReviewResponseDTO>> findAllByOrderByUpdatedAt(@PathVariable int userId, Pageable pageable) {
//        Page<ReviewResponseDTO> resultReview = reviewService.findReviewByUserIdSortedUpdatedAt(userId, pageable);
//        // 상태 코드 200(ok)와 함께 JSON 반환
//        return ResponseEntity.ok(resultReview);
//    }
    @GetMapping("/findAll/user/{userId}")
    public ResponseEntity<Page<ReviewResponseDTO>> findReviewByUserIdSortedUpdatedAt(@PathVariable int userId, Pageable pageable) {
        Page<ReviewResponseDTO> resultInquiryAnswer = reviewService.findReviewByUserIdSortedUpdatedAt(userId, pageable);
        // 상태 코드 200(ok)와 함께 JSON 반환
        return ResponseEntity.ok(resultInquiryAnswer);
    }

    @GetMapping("/findAll/orderItem/{orderItemId}")
    public ResponseEntity<Page<ReviewResponseDTO>> findReviewByOrderItemIdSortedUpdatedAt(@PathVariable int orderItemId, Pageable pageable) {
        Page<ReviewResponseDTO> resultInquiryAnswer = reviewService.findReviewByOrderItemIdSortedUpdatedAt(orderItemId, pageable);
        // 상태 코드 200(ok)와 함께 JSON 반환
        return ResponseEntity.ok(resultInquiryAnswer);
    }

    /* 3. 리뷰 등록 */
    @PostMapping("/insert")
    public ResponseEntity<ReviewResponseDTO> insertReview(@RequestBody ReviewRequestDTO requestDTO) {
        ReviewResponseDTO newReview = reviewService.insertReview(requestDTO);
        // 신규 리소스 생성 시 201 Created 상태 코드 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(newReview);
    }

    /* 4. 리뷰 수정 */
    @PutMapping("/modify/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> modifyReview(@PathVariable int reviewId, @RequestBody ReviewRequestDTO requestDTO) {
        ReviewResponseDTO updatedReview = reviewService.modifyReviewContent(reviewId, requestDTO);
//        신규 리소스 생성 시 201 CREATED 상태 코드 반환
        return ResponseEntity.ok(updatedReview);
    }

    /* 5. 리뷰 삭제 */
    @DeleteMapping("/delete/{reviewId} ")
    public ResponseEntity<Void> deleteReview(@PathVariable int reviewId) {

        reviewService.deleteReview(reviewId);

//        삭제 성공 시 내용 없이 204 반환
        return ResponseEntity.noContent().build();
    }
}
