package com.sesac.fmmall.Controller;

import com.sesac.fmmall.DTO.Inquiry.InquiryAnswerRequestDTO;
import com.sesac.fmmall.DTO.Inquiry.InquiryAnswerResponseDTO;
import com.sesac.fmmall.DTO.Inquiry.InquiryRequestDTO;
import com.sesac.fmmall.DTO.Inquiry.InquiryResponseDTO;
import com.sesac.fmmall.Repository.InquiryAnswerRepository;
import com.sesac.fmmall.Repository.InquiryRepository;
import com.sesac.fmmall.Service.InquiryAnswerService;
import com.sesac.fmmall.Service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/InquiryAnswer")
@RequiredArgsConstructor
public class InquiryAnswerController {

    private final InquiryAnswerService inquiryAnswerService;

    /* 1. 특정 아이디로 조회 */
    @GetMapping("/find/{inquiryAnswerId}")
    public ResponseEntity<InquiryAnswerResponseDTO> findInquiryAnswerById(@PathVariable int inquiryAnswerId) {
        InquiryAnswerResponseDTO resultInquiryAnswer = inquiryAnswerService.findInquiryByInquiryId(inquiryAnswerId);
        // 상태 코드 200(ok)와 함께 JSON 반환
        return ResponseEntity.ok(resultInquiryAnswer);
    }

    /* 2. 최신순 정렬(페이징) -> 문의, 유저별로 변경해야함. */
    @GetMapping("/findAll")
    public ResponseEntity<Page<InquiryAnswerResponseDTO>> findAllByOrderByUpdatedAt(Pageable pageable) {
        Page<InquiryAnswerResponseDTO> resultInquiryAnswer = inquiryAnswerService.findAllSortedUpdatedAt(pageable);
        // 상태 코드 200(ok)와 함께 JSON 반환
        return ResponseEntity.ok(resultInquiryAnswer);
    }

    /* 3. 문의 답변 등록 */
    @PostMapping("/insert")
    public ResponseEntity<InquiryAnswerResponseDTO> registInquiry(@RequestBody InquiryAnswerRequestDTO requestDTO) {
        InquiryAnswerResponseDTO newInquiryAnswer = inquiryAnswerService.registInquiryAnswer(requestDTO);
        // 신규 리소스 생성 시 201 Created 상태 코드 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(newInquiryAnswer);
    }

    /* 4. 문의 답변 수정 */
    @PutMapping("/modify/{inquiryAnswerId}")
    public ResponseEntity<InquiryAnswerResponseDTO> modifyInquiry(@PathVariable int inquiryAnswerId, @RequestBody InquiryAnswerRequestDTO requestDTO) {
        InquiryAnswerResponseDTO updatedInquiryAnswer = inquiryAnswerService.modifyInquiryAnswerContent(inquiryAnswerId, requestDTO);
//        신규 리소스 생성 시 201 CREATED 상태 코드 반환
        return ResponseEntity.ok(updatedInquiryAnswer);
    }

    /* 5. 문의 답변 삭제 */
    @DeleteMapping("/delete/{inquiryAnswerId} ")
    public ResponseEntity<Void> deleteinquiry(@PathVariable int inquiryAnswerId) {

        inquiryAnswerService.deleteInquiryAnswer(inquiryAnswerId);

//        삭제 성공 시 내용 없이 204 반환
        return ResponseEntity.noContent().build();
    }
}
