package com.sesac.fmmall.Controller;

import com.sesac.fmmall.DTO.Inquiry.InquiryAnswerRequestDTO;
import com.sesac.fmmall.DTO.Inquiry.InquiryAnswerResponseDTO;
import com.sesac.fmmall.DTO.User.UserResponseDto;
import com.sesac.fmmall.Service.InquiryAnswerService;
import com.sesac.fmmall.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "InquiryAnswer", description = "상품 문의 답변 API")
@RestController
@RequestMapping("/InquiryAnswer")
@RequiredArgsConstructor
public class InquiryAnswerController {

    private final InquiryAnswerService inquiryAnswerService;
    private final UserService userService;

    /* 1. 특정 아이디로 조회 */
    @Operation(summary = "문의 답변 단건 조회", description = "문의 답변 ID로 특정 답변을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "답변 조회 성공"),
            @ApiResponse(responseCode = "404", description = "답변을 찾을 수 없음")
    })
    @GetMapping("/find/{inquiryAnswerId}")
    public ResponseEntity<InquiryAnswerResponseDTO> findInquiryAnswerById(@PathVariable int inquiryAnswerId) {
        InquiryAnswerResponseDTO resultInquiryAnswer = inquiryAnswerService.findInquiryAnswerByInquiryAnswerId(inquiryAnswerId);
        // 상태 코드 200(ok)와 함께 JSON 반환
        return ResponseEntity.ok(resultInquiryAnswer);
    }

//    /* 2. 최신순 정렬(페이징) -> 문의, 유저별로 변경해야함. */
//    @GetMapping("/findAll")
//    public ResponseEntity<Page<InquiryAnswerResponseDTO>> findAllByOrderByUpdatedAt(Pageable pageable) {
//        Page<InquiryAnswerResponseDTO> resultInquiryAnswer = inquiryAnswerService.findAllSortedUpdatedAt(pageable);
//        // 상태 코드 200(ok)와 함께 JSON 반환
//        return ResponseEntity.ok(resultInquiryAnswer);
//    }
    /* 2. 최신순 정렬(페이징) -> 유저, 문의 */
    @Operation(summary = "사용자별 답변 목록 조회", description = "특정 사용자가 작성한 모든 답변을 최신순으로 페이징하여 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "답변 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/findAll/user/{userId}/{curPage}")
    public ResponseEntity<Page<InquiryAnswerResponseDTO>> findInquiryAnswerByUserIdSortedUpdatedAt(@PathVariable int userId, @PathVariable int curPage) {
        Page<InquiryAnswerResponseDTO> resultInquiryAnswer = inquiryAnswerService.findInquiryAnswerByUserIdSortedUpdatedAt(userId, curPage);
        // 상태 코드 200(ok)와 함께 JSON 반환
        return ResponseEntity.ok(resultInquiryAnswer);
    }

    @Operation(summary = "문의별 답변 목록 조회", description = "특정 문의에 달린 모든 답변을 최신순으로 페이징하여 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "답변 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "문의를 찾을 수 없음")
    })
    @GetMapping("/findAll/inquiry/{inquiryId}/{curPage}")
    public ResponseEntity<Page<InquiryAnswerResponseDTO>> findInquiryAnswerByInquiryIdSortedUpdatedAt(@PathVariable int inquiryId, @PathVariable int curPage) {
        Page<InquiryAnswerResponseDTO> resultInquiryAnswer = inquiryAnswerService.findInquiryAnswerByInquiryIdSortedUpdatedAt(inquiryId, curPage);
        // 상태 코드 200(ok)와 함께 JSON 반환
        return ResponseEntity.ok(resultInquiryAnswer);
    }

    /* 3. 문의 답변 등록 */
    @Operation(summary = "문의 답변 등록", description = "상품 문의에 대한 답변을 작성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "답변 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 이미 답변이 완료된 문의)"),
            @ApiResponse(responseCode = "404", description = "문의를 찾을 수 없음")
    })
    @PostMapping("/insert")
//    , @AuthenticationPrincipal int userId 매개변수
    public ResponseEntity<InquiryAnswerResponseDTO> insertInquiryAnswer(@RequestBody InquiryAnswerRequestDTO requestDTO) {
//        UserResponseDto dto = userService.getUserInfo(userId);
//        dto.getId();
        InquiryAnswerResponseDTO newInquiryAnswer = inquiryAnswerService.insertInquiryAnswer(requestDTO);
        // 신규 리소스 생성 시 201 Created 상태 코드 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(newInquiryAnswer);
    }

    /* 4. 문의 답변 수정 */
    @Operation(summary = "문의 답변 수정", description = "기존 답변의 내용을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "답변 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 필수 필드 누락)"),
            @ApiResponse(responseCode = "404", description = "답변을 찾을 수 없음")
    })
    @PutMapping("/modify/{inquiryAnswerId}")
    public ResponseEntity<InquiryAnswerResponseDTO> modifyInquiryAnswer(@PathVariable int inquiryAnswerId, @RequestBody InquiryAnswerRequestDTO requestDTO) {
        InquiryAnswerResponseDTO updatedInquiryAnswer = inquiryAnswerService.modifyInquiryAnswerContent(inquiryAnswerId, requestDTO);
//        신규 리소스 생성 시 201 CREATED 상태 코드 반환
        return ResponseEntity.ok(updatedInquiryAnswer);
    }

    /* 5. 문의 답변 삭제 */
    @Operation(summary = "문의 답변 삭제", description = "문의 답변을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "답변 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "답변을 찾을 수 없음")
    })
    @DeleteMapping("/delete/{inquiryAnswerId} ")
    public ResponseEntity<Void> deleteInquiryAnswer(@PathVariable int inquiryAnswerId) {

        inquiryAnswerService.deleteInquiryAnswer(inquiryAnswerId);

//        삭제 성공 시 내용 없이 204 반환
        return ResponseEntity.noContent().build();
    }
}
