package com.sesac.fmmall.Service;

import com.sesac.fmmall.DTO.Review.ReviewRequestDTO;
import com.sesac.fmmall.DTO.Review.ReviewResponseDTO;
import com.sesac.fmmall.DTO.Review.ReviewResponseDTO;
import com.sesac.fmmall.Entity.*;
import com.sesac.fmmall.Repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;

    /* 1. 리뷰 코드로 상세 조회 */
    public ReviewResponseDTO findReviewByReviewId(int reviewId) {
        Review foundReview = reviewRepository.findById(reviewId).orElseThrow(
                () -> new IllegalArgumentException("해당 ID를 가진 리뷰가 존재하지 않습니다."));

//        return new ReviewResponseDTO(foundReview);
        return ReviewResponseDTO.from(foundReview);
    }
//    /* 2. 리뷰 최신순 상세 조회 */
//    public Page<ReviewResponseDTO> findAllSortedUpdatedAt(Pageable pageable) {
//        int page = pageable.getPageNumber() <= 0 ? 0 : pageable.getPageNumber() - 1;
//        int size = pageable.getPageSize();
////        Sort sort = pageable.getSort();
//        String sortDir = "updatedAt";
//
//        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDir).descending());
//        Page<Review> ReviewList = reviewRepository.findAllByOrderByUpdatedAtDesc(pageRequest);
////        return ReviewList.map(ReviewResponseDTO::new);
//        return ReviewList.map(ReviewResponseDTO::from);
////        return modelMapper.map(foundReview, ReviewResponseDTO.class);
//    }
    /* 2. 리뷰 최신순 상세 조회(유저, 주문 상품별) */
    public Page<ReviewResponseDTO> findReviewByUserIdSortedUpdatedAt(int userId, Pageable pageable) {

        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }

        // 2. 페이징 및 정렬 설정 (기존 로직과 동일: 0페이지 보정 + 최신순 정렬)
        int page = pageable.getPageNumber() <= 0 ? 0 : pageable.getPageNumber() - 1;
        int size = 10;   // 리뷰는 한 페이지에 10개씩만
        String sortDir = "updatedAt";

        // Sort.by(sortDir).descending() -> 최신순(내림차순)
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDir).descending());

        // 3. 리포지토리 호출 (유저 ID로 필터링 + 페이징/정렬 적용)
        Page<Review> reviewList = reviewRepository.findAllByUserUserId(userId, pageRequest);

        // 4. Entity -> DTO 변환 후 반환
        return reviewList.map(ReviewResponseDTO::from);
    }

    public Page<ReviewResponseDTO> findReviewByOrderItemIdSortedUpdatedAt(int orderItemId, Pageable pageable) {

        if (!orderItemRepository.existsById(orderItemId)) {
            throw new IllegalArgumentException("존재하지 않는 상품입니다.");
        }

        // 2. 페이징 및 정렬 설정 (기존 로직과 동일: 0페이지 보정 + 최신순 정렬)
        int page = pageable.getPageNumber() <= 0 ? 0 : pageable.getPageNumber() - 1;
        int size = 10;   // 리뷰는 한 페이지에 10개씩만
        String sortDir = "updatedAt";

        // Sort.by(sortDir).descending() -> 최신순(내림차순)
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDir).descending());

        // 3. 리포지토리 호출 (주문상품 ID로 필터링 + 페이징/정렬 적용)
        Page<Review> reviewList = reviewRepository.findAllByOrderItemOrderItemId(orderItemId, pageRequest);

        // 4. Entity -> DTO 변환 후 반환
        return reviewList.map(ReviewResponseDTO::from);
    }

    /* 3. 리뷰 등록 */
    @Transactional
    public ReviewResponseDTO insertReview(ReviewRequestDTO requestDTO) {
        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        OrderItem orderItem = orderItemRepository.findById(requestDTO.getOrderItemId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        // DTO -> Entity 변환 (builder 패턴 사용)
        Review newReview = Review.builder()
                .reviewContent(requestDTO.getReviewContent())
                .user(user)
                .orderItem(orderItem)
                .build();

        // 내부적으로 EntityManager.persist() 호출되어 영속성 컨텍스트로 들어간다.
        Review savedReview = reviewRepository.save(newReview);

        // 저장 후, 생성된 Entity를 다시 DTO로 변환하여 반환
        return ReviewResponseDTO.from(savedReview);
//        return modelMapper.map(savedReview, ReviewResponseDTO.class);
//        return new ReviewResponseDTO(savedReview);
    }

    /* 4. 리뷰 수정 */
    @Transactional
    public ReviewResponseDTO modifyReviewContent(int reviewId, ReviewRequestDTO requestDTO) {

        Review foundReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("수정할 리뷰가 존재하지 않습니다."));

        foundReview.modify(
            requestDTO.getReviewContent(),
            requestDTO.getReviewRating()
        );

        return ReviewResponseDTO.from(foundReview);
//        return modelMapper.map(foundReview, ReviewResponseDTO.class);
//        return new ReviewResponseDTO(foundReview);
    }

    /* 5. 리뷰 삭제 */
    @Transactional
    public void deleteReview(int reviewId) {

        if (!reviewRepository.existsById(reviewId)) {
            throw new IllegalArgumentException("삭제할 리뷰가 존재하지 않습니다.");

        }

        reviewRepository.deleteById(reviewId);
    }


}
