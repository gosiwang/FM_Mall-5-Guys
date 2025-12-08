package com.sesac.fmmall.Controller;

import com.sesac.fmmall.DTO.Inquiry.InquiryAnswerResponseDTO;
import com.sesac.fmmall.DTO.Inquiry.InquiryRequestDTO;
import com.sesac.fmmall.DTO.Inquiry.InquiryResponseDTO;
import com.sesac.fmmall.Service.InquiryService;
import com.sesac.fmmall.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Inquiry")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;
    private final UserService userService;

    /* 1. 특정 아이디로 조회 */
    @GetMapping("/find/{inquiryId}")
    public ResponseEntity<InquiryResponseDTO> findInquiryById(@PathVariable int inquiryId) {
        InquiryResponseDTO resultInquiry = inquiryService.findInquiryByInquiryId(inquiryId);
        // 상태 코드 200(ok)와 함께 JSON 반환
        return ResponseEntity.ok(resultInquiry);
    }

//    /* 2. 최신순 정렬(페이징) -> 상품, 유저별로 변경해야함. */
//    @GetMapping("/findAll")
//    public ResponseEntity<Page<InquiryResponseDTO>> findAllByOrderByUpdatedAt(Pageable pageable) {
//        Page<InquiryResponseDTO> resultInquiry = inquiryService.findAllSortedUpdatedAt(pageable);
//        // 상태 코드 200(ok)와 함께 JSON 반환
//        return ResponseEntity.ok(resultInquiry);
//    }
    /* 2. 최신순 정렬(페이징) -> 유저, 상품 */
    @GetMapping("/findAll/user/{userId}/{curPage}")
    public ResponseEntity<Page<InquiryResponseDTO>> findInquiryByUserIdSortedUpdatedAt(@PathVariable int userId, @PathVariable int curPage) {
        Page<InquiryResponseDTO> resultInquiryAnswer = inquiryService.findInquiryByUserIdSortedUpdatedAt(userId, curPage);
        // 상태 코드 200(ok)와 함께 JSON 반환
        return ResponseEntity.ok(resultInquiryAnswer);
    }

    @GetMapping("/findAll/product/{productId}/{curPage}")
    public ResponseEntity<Page<InquiryResponseDTO>> findInquiryByProductIdSortedUpdatedAt(@PathVariable int productId, @PathVariable int curPage) {
        Page<InquiryResponseDTO> resultInquiryAnswer = inquiryService.findInquiryByProductIdSortedUpdatedAt(productId, curPage);
        // 상태 코드 200(ok)와 함께 JSON 반환
        return ResponseEntity.ok(resultInquiryAnswer);
    }

    /* 3. 문의 등록 */
    @PostMapping("/insert")
    public ResponseEntity<InquiryResponseDTO> insertInquiry(@RequestBody InquiryRequestDTO requestDTO) {
        InquiryResponseDTO newInquiry = inquiryService.insertInquiry(requestDTO);
        // 신규 리소스 생성 시 201 Created 상태 코드 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(newInquiry);
    }

    /* 4. 문의 수정 */
    @PutMapping("/modify/{inquiryId}")
    public ResponseEntity<InquiryResponseDTO> modifyInquiry(@PathVariable int inquiryId, @RequestBody InquiryRequestDTO requestDTO) {
        InquiryResponseDTO updatedInquiry = inquiryService.modifyInquiryContent(inquiryId, requestDTO);
//        신규 리소스 생성 시 201 CREATED 상태 코드 반환
        return ResponseEntity.ok(updatedInquiry);
    }

    /* 5. 문의 삭제 */
    @DeleteMapping("/delete/{inquiryId} ")
    public ResponseEntity<Void> deleteInquiry(@PathVariable int inquiryId) {

        inquiryService.deleteInquiry(inquiryId);

//        삭제 성공 시 내용 없이 204 반환
        return ResponseEntity.noContent().build();
    }
}
