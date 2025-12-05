package com.sesac.fmmall.DTO.Inquiry;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@ToString
@NoArgsConstructor
public class InquiryAnswerRequestDTO {
    private String inquiryContent;
    private int userId;
    private int inquiryId;
}
