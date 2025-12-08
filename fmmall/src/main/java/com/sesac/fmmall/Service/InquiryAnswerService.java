package com.sesac.fmmall.Service;

import com.sesac.fmmall.DTO.Inquiry.InquiryAnswerRequestDTO;
import com.sesac.fmmall.DTO.Inquiry.InquiryAnswerResponseDTO;
import com.sesac.fmmall.Entity.Inquiry;
import com.sesac.fmmall.Entity.InquiryAnswer;
import com.sesac.fmmall.Entity.User;
import com.sesac.fmmall.Repository.InquiryAnswerRepository;
import com.sesac.fmmall.Repository.InquiryRepository;
import com.sesac.fmmall.Repository.UserRepository;
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
public class InquiryAnswerService {
    private final InquiryAnswerRepository inquiryAnswerRepository;
    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    /* 1. 문의 답변 코드로 상세 조회 */
    public InquiryAnswerResponseDTO findInquiryAnswerByInquiryAnswerId(int inquiryAnswerId) {
        InquiryAnswer foundInquiryAnswer = inquiryAnswerRepository.findById(inquiryAnswerId).orElseThrow(
                () -> new IllegalArgumentException("해당 ID를 가진 문의 답변이 존재하지 않습니다."));

//        return new InquiryResponseDTO(foundInquiry);
        return InquiryAnswerResponseDTO.from(foundInquiryAnswer);
    }
//    /* 2. 문의 답변 최신순 상세 조회 */
//    public Page<InquiryAnswerResponseDTO> findAllSortedUpdatedAt(Pageable pageable) {
//        int page = pageable.getPageNumber() <= 0 ? 0 : pageable.getPageNumber() - 1;
//        int size = pageable.getPageSize();
////        Sort sort = pageable.getSort();
//        String sortDir = "updatedAt";
//
//        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDir).descending());
//        Page<InquiryAnswer> InquiryAnswerList = inquiryAnswerRepository.findAllByOrderByUpdatedAtDesc(pageRequest);
////        return InquiryList.map(InquiryResponseDTO::new);
//        return InquiryAnswerList.map(InquiryAnswerResponseDTO::from);
////        return modelMapper.map(foundInquiry, InquiryResponseDTO.class);
//    }

    /* 2. 문의 답변 최신순 상세 조회(유저, 문의별) */
    public Page<InquiryAnswerResponseDTO> findInquiryAnswerByUserIdSortedUpdatedAt(int userId, Pageable pageable) {

        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }

        // 2. 페이징 및 정렬 설정 (기존 로직과 동일: 0페이지 보정 + 최신순 정렬)
        int page = pageable.getPageNumber() <= 0 ? 0 : pageable.getPageNumber() - 1;
        int size = 5;   // 리뷰는 한 페이지에 10개씩만
        String sortDir = "updatedAt";

        // Sort.by(sortDir).descending() -> 최신순(내림차순)
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDir).descending());

        // 3. 리포지토리 호출 (문의 ID로 필터링 + 페이징/정렬 적용)
        Page<InquiryAnswer> inquiryAnswerList = inquiryAnswerRepository.findAllByUser_UserId(userId, pageRequest);

        // 4. Entity -> DTO 변환 후 반환
        return inquiryAnswerList.map(InquiryAnswerResponseDTO::from);
    }

    public Page<InquiryAnswerResponseDTO> findInquiryAnswerByInquiryIdSortedUpdatedAt(int inquiryId, Pageable pageable) {

        if (!inquiryRepository.existsById(inquiryId)) {
            throw new IllegalArgumentException("존재하지 않는 상품입니다.");
        }

        // 2. 페이징 및 정렬 설정 (기존 로직과 동일: 0페이지 보정 + 최신순 정렬)
        int page = pageable.getPageNumber() <= 0 ? 0 : pageable.getPageNumber() - 1;
        int size = 5;   // 문의 답변은 한 페이지에 5개씩만
        String sortDir = "updatedAt";

        // Sort.by(sortDir).descending() -> 최신순(내림차순)
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDir).descending());

        // 3. 리포지토리 호출 (문의 ID로 필터링 + 페이징/정렬 적용)
        Page<InquiryAnswer> inquiryAnswerList = inquiryAnswerRepository.findAllByInquiry_InquiryId(inquiryId, pageRequest);

        // 4. Entity -> DTO 변환 후 반환
        return inquiryAnswerList.map(InquiryAnswerResponseDTO::from);
    }

    /* 3. 문의 답변 등록 */
    @Transactional
    public InquiryAnswerResponseDTO insertInquiryAnswer(InquiryAnswerRequestDTO requestDTO) {
        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        Inquiry inquiry = inquiryRepository.findById(requestDTO.getInquiryId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문의입니다."));

        // DTO -> Entity 변환 (builder 패턴 사용)
        InquiryAnswer newInquiryAnswer = InquiryAnswer.builder()
                .inquiryAnswerContent(requestDTO.getInquiryAnswerContent())
                .user(user)
                .inquiry(inquiry)
                .build();

        // 내부적으로 EntityManager.persist() 호출되어 영속성 컨텍스트로 들어간다.
        InquiryAnswer savedInquiryAnswer = inquiryAnswerRepository.save(newInquiryAnswer);

        // 저장 후, 생성된 Entity를 다시 DTO로 변환하여 반환

        return InquiryAnswerResponseDTO.from(savedInquiryAnswer);

//            return InquiryAnswerResponseDTO.builder()
//                    .userId(savedInquiryAnswer.getUser().getUserId())       // user.getUserId()로 해도 되지만, 그냥 안전하게 save한 걸로 받아옴.
//                    .inquiryId(savedInquiryAnswer.getInquiry().getInquiryId())
//                    .inquiryAnswerContent(savedInquiryAnswer.getInquiryAnswerContent())
//                    .createdAt(savedInquiryAnswer.getCreatedAt())
//                    .updatedAt(savedInquiryAnswer.getUpdatedAt())
//                    .build();

//        return modelMapper.map(savedInquiryAnswer, InquiryAnswerResponseDTO.class);
//        return new InquiryResponseDTO(savedInquiry);
    }

    /* 4. 문의 답변 수정 */
    @Transactional
    public InquiryAnswerResponseDTO modifyInquiryAnswerContent(int inquiryAnswerId, InquiryAnswerRequestDTO requestDTO) {

        InquiryAnswer foundInquiryAnswer = inquiryAnswerRepository.findById(inquiryAnswerId)
                .orElseThrow(() -> new IllegalArgumentException("수정할 문의가 존재하지 않습니다."));

        foundInquiryAnswer.modifyContent(
            requestDTO.getInquiryAnswerContent()
        );

        return InquiryAnswerResponseDTO.from(foundInquiryAnswer);
//        return modelMapper.map(foundInquiryAnswer, InquiryAnswerResponseDTO.class);
//        return new InquiryResponseDTO(foundInquiry);
    }

    /* 5. 문의 답변 삭제 */
    @Transactional
    public void deleteInquiryAnswer(int inquiryAnswerId) {

        if (!inquiryAnswerRepository.existsById(inquiryAnswerId)) {
            throw new IllegalArgumentException("삭제할 문의 답변이 존재하지 않습니다.");

        }

        inquiryAnswerRepository.deleteById(inquiryAnswerId);
    }


}
