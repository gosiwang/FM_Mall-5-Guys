package com.sesac.fmmall.Controller;

import com.sesac.fmmall.DTO.Review.ReviewModifyRequestDTO;
import com.sesac.fmmall.DTO.Review.ReviewRequestDTO;
import com.sesac.fmmall.DTO.Review.ReviewResponseDTO;
import com.sesac.fmmall.Service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Review")
@RequiredArgsConstructor
public class ReviewController extends BaseController {

    private final ReviewService reviewService;

    /* 1. 특정 아이디로 조회 */
    @GetMapping("/find/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> findReviewById(@PathVariable int reviewId) {
        ReviewResponseDTO resultReview = reviewService.findReviewByReviewId(reviewId);

        return ResponseEntity.ok(resultReview);
    }

    @GetMapping("/findByUser/{userId}")
    public ResponseEntity<Page<ReviewResponseDTO>> findReviewByUserIdSortedUpdatedAt(@PathVariable int userId, @RequestParam(defaultValue = "1") int curPage) {
        Page<ReviewResponseDTO> resultInquiryAnswer = reviewService.findReviewByUserIdSortedUpdatedAt(userId, curPage);

        return ResponseEntity.ok(resultInquiryAnswer);
    }

    @GetMapping("/findByOrderItem/{orderItemId}")
    public ResponseEntity<Page<ReviewResponseDTO>> findReviewByOrderItemIdSortedUpdatedAt(@PathVariable int orderItemId, @RequestParam(defaultValue = "1") int curPage) {
        Page<ReviewResponseDTO> resultInquiryAnswer = reviewService.findReviewByOrderItemIdSortedUpdatedAt(orderItemId, curPage);

        return ResponseEntity.ok(resultInquiryAnswer);
    }

    @GetMapping("/findByUser/me")
    public ResponseEntity<Page<ReviewResponseDTO>> findReviewByUserIdSortedUpdatedAt(@RequestParam(defaultValue = "1") int curPage) {
        Page<ReviewResponseDTO> resultInquiryAnswer = reviewService.findReviewByUserIdSortedUpdatedAt(getCurrentUserId(), curPage);

        return ResponseEntity.ok(resultInquiryAnswer);
    }

    /* 3. 리뷰 등록 */
    @PostMapping("/insert")
    public ResponseEntity<ReviewResponseDTO> insertReview(@RequestBody ReviewRequestDTO requestDTO) {
        ReviewResponseDTO newReview = reviewService.insertReview(getCurrentUserId(), requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(newReview);
    }

    /* 4. 리뷰 수정 */
    @PutMapping("/modify/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> modifyReview(@PathVariable int reviewId, @RequestBody ReviewModifyRequestDTO requestDTO) {
        ReviewResponseDTO updatedReview = reviewService.modifyReviewContent(reviewId, getCurrentUserId(), requestDTO);

        return ResponseEntity.ok(updatedReview);
    }

    /* 5. 리뷰 삭제 */
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
