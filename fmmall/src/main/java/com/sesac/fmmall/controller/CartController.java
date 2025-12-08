package com.sesac.fmmall.controller;

import com.sesac.fmmall.DTO.CartItem.CartItemCreateRequestDTO;
import com.sesac.fmmall.DTO.CartItem.CartItemUpdateRequestDTO;
import com.sesac.fmmall.DTO.CartResponseDTO;
import com.sesac.fmmall.Service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // 장바구니 상품 추가
    @PostMapping("/insert/{userId}")
    public ResponseEntity<CartResponseDTO> addCartItem(
            @PathVariable int userId,
            @RequestBody CartItemCreateRequestDTO requestDTO
    ) {
        CartResponseDTO response = cartService.createCartItem(userId, requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 장바구니 상품 수량 변경
    @PutMapping("/modify/{userId}/{cartItemId}")
    public ResponseEntity<CartResponseDTO> modifyCartItem(
            @PathVariable int userId,
            @PathVariable int cartItemId,
            @RequestBody CartItemUpdateRequestDTO requestDTO
    ) {
        CartResponseDTO response = cartService.updateCartItemQuantity(userId, cartItemId, requestDTO);
        return ResponseEntity.ok(response);
    }

    // 장바구니 상품 삭제
    @DeleteMapping("/delete/{userId}/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(
            @PathVariable int userId,
            @PathVariable int cartItemId
    ) {
        cartService.removeCartItem(userId, cartItemId);
        return ResponseEntity.noContent().build();
    }

    // 장바구니 전체 삭제 (유저 기준)
    @DeleteMapping("/deleteAll/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable int userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    // 장바구니 목록 조회 (유저 기준)
    @GetMapping("/findAll/{userId}")
    public ResponseEntity<CartResponseDTO> findAllCartItems(@PathVariable int userId) {
        CartResponseDTO response = cartService.findAllCartItems(userId);
        return ResponseEntity.ok(response);
    }
}
