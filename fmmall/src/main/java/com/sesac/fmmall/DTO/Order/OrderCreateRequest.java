package com.sesac.fmmall.DTO.Order;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreateRequest {

    private String receiverName;
    private String receiverPhone;
    private String zipcode;
    private String address1;
    private String address2;


    private List<OrderItemCreateRequest> items;


    private String paymentMethodType;


}