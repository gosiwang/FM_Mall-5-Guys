package com.sesac.fmmall.DTO.Inquiry;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sesac.fmmall.Entity.Inquiry;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class InquiryResponseDTO {
    private int inquiryId;
    private String inquiryContent;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private int userId;
    private int productId;

    public InquiryResponseDTO(Inquiry inquiry) {
        this.inquiryId = inquiry.getInquiryId();
        this.inquiryContent = inquiry.getInquiryContent();
        this.createdAt = inquiry.getCreatedAt();
        this.updatedAt = inquiry.getUpdatedAt();
        this.userId = inquiry.getUser().getUserId();
        this.productId = inquiry.getProduct().getProductId();

    }
}
