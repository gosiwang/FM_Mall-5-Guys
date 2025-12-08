package com.sesac.fmmall.Service;

import com.sesac.fmmall.DTO.Inquiry.InquiryRequestDTO;
import com.sesac.fmmall.DTO.Inquiry.InquiryResponseDTO;
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
    public ReviewResponseDTO findReviewByInquiryId(int reviewId) {
        Review foundReview = reviewRepository.findById(reviewId).orElseThrow(
                () -> new IllegalArgumentException("해당 ID를 가진 리뷰가 존재하지 않습니다."));

//        return new InquiryResponseDTO(foundInquiry);
        return ReviewResponseDTO.from(foundReview);
    }
    /* 2. 리뷰 최신순 상세 조회 */
    public Page<ReviewResponseDTO> findAllSortedUpdatedAt(Pageable pageable) {
        int page = pageable.getPageNumber() <= 0 ? 0 : pageable.getPageNumber() - 1;
        int size = pageable.getPageSize();
//        Sort sort = pageable.getSort();
        String sortDir = "updatedAt";

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDir).descending());
        Page<Review> ReviewList = reviewRepository.findAllByOrderByUpdatedAtDesc(pageRequest);
//        return InquiryList.map(InquiryResponseDTO::new);
        return ReviewList.map(ReviewResponseDTO::from);
//        return modelMapper.map(foundInquiry, InquiryResponseDTO.class);
    }

    /* 3. 리뷰 등록 */
//    @Transactional
//    public InquiryResponseDTO registInquiry(InquiryRequestDTO requestDTO) {
//        User user = userRepository.findById(requestDTO.getUserId())
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
//        OrderItem orderItem = orderItemRepository.findById(requestDTO.getProductId())
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));
//
//        // DTO -> Entity 변환 (builder 패턴 사용)
//        Inquiry newInquiry = Inquiry.builder()
//                .inquiryContent(requestDTO.getInquiryContent())
//                .user(user)
//                .product(product)
//                .build();
//
//        // 내부적으로 EntityManager.persist() 호출되어 영속성 컨텍스트로 들어간다.
//        Inquiry savedInquiry = inquiryRepository.save(newInquiry);
//
//        // 저장 후, 생성된 Entity를 다시 DTO로 변환하여 반환
//        return InquiryResponseDTO.from(savedInquiry);
////        return modelMapper.map(savedInquiry, InquiryResponseDTO.class);
////        return new InquiryResponseDTO(savedInquiry);
//    }
//
//    /* 4. 리뷰 수정 */
//    @Transactional
//    public InquiryResponseDTO modifyInquiryContent(int inquiryId, InquiryRequestDTO requestDTO) {
//
//        Inquiry foundInquiry = inquiryRepository.findById(inquiryId)
//                .orElseThrow(() -> new IllegalArgumentException("수정할 문의가 존재하지 않습니다."));
//
//        foundInquiry.modifyContent(
//            requestDTO.getInquiryContent()
//        );
//
//        return InquiryResponseDTO.from(foundInquiry);
////        return modelMapper.map(foundInquiry, InquiryResponseDTO.class);
////        return new InquiryResponseDTO(foundInquiry);
//    }
//
//    /* 5. 리뷰 삭제 */
//    @Transactional
//    public void deleteInquiry(int inquiryId) {
//
//        if (!inquiryRepository.existsById(inquiryId)) {
//            throw new IllegalArgumentException("삭제할 문의가 존재하지 않습니다.");
//
//        }
//
//        inquiryRepository.deleteById(inquiryId);
//    }


}
