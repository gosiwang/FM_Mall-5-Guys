package com.sesac.fmmall.Controller;

import com.sesac.fmmall.DTO.Inquiry.InquiryModifyRequestDTO;
import com.sesac.fmmall.DTO.Inquiry.InquiryRequestDTO;
import com.sesac.fmmall.DTO.Inquiry.InquiryResponseDTO;
import com.sesac.fmmall.Service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Inquiry")
@RequiredArgsConstructor
public class InquiryController extends BaseController {

    private final InquiryService inquiryService;

    /* 1. 특정 아이디로 조회 */
    @GetMapping("/find/{inquiryId}")
    public ResponseEntity<InquiryResponseDTO> findInquiryById(@PathVariable int inquiryId) {
        InquiryResponseDTO resultInquiry = inquiryService.findInquiryByInquiryId(inquiryId);

        return ResponseEntity.ok(resultInquiry);
    }

    /* 2. 최신순 정렬(페이징) -> 유저, 상품 */
    @GetMapping("/findByUser/{userId}/{curPage}")
    public ResponseEntity<Page<InquiryResponseDTO>> findInquiryByUserIdSortedUpdatedAt(@PathVariable int userId, @PathVariable int curPage) {
        Page<InquiryResponseDTO> resultInquiryAnswer = inquiryService.findInquiryByUserIdSortedUpdatedAt(userId, curPage);

        return ResponseEntity.ok(resultInquiryAnswer);
    }

    @GetMapping("/findByProduct/{productId}/{curPage}")
    public ResponseEntity<Page<InquiryResponseDTO>> findInquiryByProductIdSortedUpdatedAt(@PathVariable int productId, @PathVariable int curPage) {
        Page<InquiryResponseDTO> resultInquiryAnswer = inquiryService.findInquiryByProductIdSortedUpdatedAt(productId, curPage);

        return ResponseEntity.ok(resultInquiryAnswer);
    }

    /* 3. 문의 등록 */
    @PostMapping("/insert")
    public ResponseEntity<InquiryResponseDTO> insertInquiry(@RequestBody InquiryRequestDTO requestDTO) {
        InquiryResponseDTO newInquiry = inquiryService.insertInquiry(getCurrentUserId(), requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(newInquiry);
    }

    /* 4. 문의 수정 */
    @PutMapping("/modify/{inquiryId}")
    public ResponseEntity<InquiryResponseDTO> modifyInquiry(@PathVariable int inquiryId, @RequestBody InquiryModifyRequestDTO requestDTO) {
        InquiryResponseDTO updatedInquiry = inquiryService.modifyInquiryContent(inquiryId, getCurrentUserId(), requestDTO);

        return ResponseEntity.ok(updatedInquiry);
    }

    /* 5. 문의 삭제 */
    @DeleteMapping("/delete/{inquiryId}")
    public ResponseEntity<Void> deleteInquiry(@PathVariable int inquiryId) {

        inquiryService.deleteInquiry(inquiryId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<Void> deleteAllInquiry() {
        inquiryService.deleteAllInquiry();
        return ResponseEntity.noContent().build();
    }
}
