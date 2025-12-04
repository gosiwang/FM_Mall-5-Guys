package com.sesac.fmmall.DTO;

import com.sesac.fmmall.DTO.CartItem.CartItemResponseDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponseDTO {

    private int cartId;
    private List<CartItemResponseDTO> itemList;
    private int totalItemCount;
    private int totalPrice;

}
