package com.sesac.fmmall.DTO.Inquiry;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sesac.fmmall.Entity.Inquiry;
import com.sesac.fmmall.Entity.InquiryAnswer;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InquiryAnswerResponseDTO {
    private int inquiryAnswerId;
    private String inquiryAnswerContent;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private int userId;
    private int inquiryId;

//    public static InquiryAnswerResponseDTO from(InquiryAnswer entity) {
//        return InquiryAnswerResponseDTO.builder()
//                .userId(entity.getUser().getId())
//                .inquiryId(entity.getInquiry().getId())
//                .inquiryAnswerContent(entity.getContent())
//                .build();
//    }

}
