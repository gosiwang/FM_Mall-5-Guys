package com.sesac.fmmall.Controller;

import com.sesac.fmmall.DTO.Inquiry.InquiryAnswerModifyRequestDTO;
import com.sesac.fmmall.DTO.Inquiry.InquiryAnswerRequestDTO;
import com.sesac.fmmall.DTO.Inquiry.InquiryAnswerResponseDTO;
import com.sesac.fmmall.Service.InquiryAnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/InquiryAnswer")
@RequiredArgsConstructor
public class InquiryAnswerController extends BaseController {

    private final InquiryAnswerService inquiryAnswerService;

    /* 1. 특정 아이디로 조회 */
    @GetMapping("/findOne/{inquiryAnswerId}")
    public ResponseEntity<InquiryAnswerResponseDTO> findInquiryAnswerById(@PathVariable int inquiryAnswerId) {
        InquiryAnswerResponseDTO resultInquiryAnswer = inquiryAnswerService.findInquiryAnswerByInquiryAnswerId(inquiryAnswerId);
        return ResponseEntity.ok(resultInquiryAnswer);
    }

    /* 2. 최신순 정렬(페이징) -> 유저, 문의, 자기자신 */
    @GetMapping("/findByUser/{userId}")
    public ResponseEntity<Page<InquiryAnswerResponseDTO>> findInquiryAnswerByUserIdSortedUpdatedAt(@PathVariable int userId, @RequestParam(defaultValue = "1") int curPage) {
        Page<InquiryAnswerResponseDTO> resultInquiryAnswer = inquiryAnswerService.findInquiryAnswerByUserIdSortedUpdatedAt(userId, curPage);
        return ResponseEntity.ok(resultInquiryAnswer);
    }

    @GetMapping("/findByInquiry/{inquiryId}")
    public ResponseEntity<Page<InquiryAnswerResponseDTO>> findInquiryAnswerByInquiryIdSortedUpdatedAt(@PathVariable int inquiryId, @RequestParam(defaultValue = "1") int curPage) {
        Page<InquiryAnswerResponseDTO> resultInquiryAnswer = inquiryAnswerService.findInquiryAnswerByInquiryIdSortedUpdatedAt(inquiryId, curPage);
        return ResponseEntity.ok(resultInquiryAnswer);
    }

    @GetMapping("/findByUser/me")
    public ResponseEntity<Page<InquiryAnswerResponseDTO>> findInquiryAnswerByUserIdSortedUpdatedAt(@RequestParam(defaultValue = "1") int curPage) {
        Page<InquiryAnswerResponseDTO> resultInquiryAnswer = inquiryAnswerService.findInquiryAnswerByUserIdSortedUpdatedAt(getCurrentUserId(), curPage);
        return ResponseEntity.ok(resultInquiryAnswer);
    }

    /* 3. 문의 답변 등록 */
    @PostMapping("/insert")
    public ResponseEntity<InquiryAnswerResponseDTO> insertInquiryAnswer(@RequestBody InquiryAnswerRequestDTO requestDTO) {
        InquiryAnswerResponseDTO newInquiryAnswer = inquiryAnswerService.insertInquiryAnswer(getCurrentUserId(), requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newInquiryAnswer);
    }

    /* 4. 문의 답변 수정 */
    @PutMapping("/modify/{inquiryAnswerId}")
    public ResponseEntity<InquiryAnswerResponseDTO> modifyInquiryAnswer(@PathVariable int inquiryAnswerId, @RequestBody InquiryAnswerModifyRequestDTO requestDTO) {
        InquiryAnswerResponseDTO updatedInquiryAnswer = inquiryAnswerService.modifyInquiryAnswerContent(inquiryAnswerId, getCurrentUserId(), requestDTO);
        return ResponseEntity.ok(updatedInquiryAnswer);
    }

    /* 5. 문의 답변 삭제 */
    @DeleteMapping("/delete/{inquiryAnswerId} ")
    public ResponseEntity<Void> deleteInquiryAnswer(@PathVariable int inquiryAnswerId) {

        inquiryAnswerService.deleteInquiryAnswer(inquiryAnswerId);

        return ResponseEntity.noContent().build();
    }
}
