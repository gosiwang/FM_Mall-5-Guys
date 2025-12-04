package com.sesac.fmmall.Controller;

import com.sesac.fmmall.DTO.Inquiry.InquiryRequestDTO;
import com.sesac.fmmall.DTO.Inquiry.InquiryResponseDTO;
import com.sesac.fmmall.Repository.InquiryRepository;
import com.sesac.fmmall.Service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inquiries")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryRepository inquiryRepository;
    private final InquiryService inquiryService;

    /* 1. 특정 아이디로 조회 */
    @GetMapping("/{inquiryId}")
    public ResponseEntity<InquiryResponseDTO> findInquiryById(@PathVariable int inquiryId) {
        InquiryResponseDTO resultInquiry = inquiryService.findInquiryByInquiryId(inquiryId);
        // 상태 코드 200(ok)와 함께 JSON 반환
        return ResponseEntity.ok(resultInquiry);
    }

    /* 2. 최신순 정렬(페이징) */
    @GetMapping("/all")
    public ResponseEntity<Page<InquiryResponseDTO>> findAllByOrderByUpdatedAt(Pageable pageable) {
        Page<InquiryResponseDTO> resultInquiry = inquiryService.findAllSortedUpdatedAt(pageable);
        // 상태 코드 200(ok)와 함께 JSON 반환
        return ResponseEntity.ok(resultInquiry);
    }

    /* 3. 문의 등록 */
    @PostMapping
    public ResponseEntity<InquiryResponseDTO> registInquiry(@RequestBody InquiryRequestDTO requestDTO) {
        InquiryResponseDTO newInquiry = inquiryService.registInquiry(requestDTO);
        // 신규 리소스 생성 시 201 Created 상태 코드 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(newInquiry);
    }

    /* 4. 문의 수정 */
    @PutMapping("/{inquiryId}")
    public ResponseEntity<InquiryResponseDTO> modifyInquiry(@PathVariable int inquiryId, @RequestBody InquiryRequestDTO requestDTO) {
        InquiryResponseDTO updatedInquiry = inquiryService.modifyInquiryContent(inquiryId, requestDTO);
//        신규 리소스 생성 시 201 CREATED 상태 코드 반환
        return ResponseEntity.ok(updatedInquiry);
    }

    /* 5. 문의 삭제 */
    @DeleteMapping("/{inquiryId}")
    public ResponseEntity<Void> deleteinquiry(@PathVariable int inquiryId) {

        inquiryService.deleteInquiry(inquiryId);

//        삭제 성공 시 내용 없이 204 반환
        return ResponseEntity.noContent().build();
    }
}
