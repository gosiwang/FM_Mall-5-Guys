package com.sesac.fmmall.DTO.CartItem;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemCreateRequestDTO {
    private int productId;
    private int quantity;
}
