package com.sesac.fmmall.Controller;

import com.sesac.fmmall.DTO.CartItem.CartItemCheckStatusRequestDTO;
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
public class CartController extends BaseController {

    private final CartService cartService;

    // 장바구니 상품 추가
    @PostMapping("/insert/")
    public ResponseEntity<CartResponseDTO> addCartItem(
            @RequestBody CartItemCreateRequestDTO requestDTO
    ) {
        CartResponseDTO response = cartService.createCartItem(getCurrentUserId(), requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 장바구니 상품 수량 변경
    @PutMapping("/modify/{cartItemId}")
    public ResponseEntity<CartResponseDTO> modifyCartItem(
            @PathVariable int cartItemId,
            @RequestBody CartItemUpdateRequestDTO requestDTO
    ) {
        CartResponseDTO response = cartService.updateCartItemQuantity(getCurrentUserId(), cartItemId, requestDTO);
        return ResponseEntity.ok(response);
    }

    // 장바구니 상품 체크상태 변경
    @PutMapping("/check/{cartItemId}")
    public ResponseEntity<CartResponseDTO> checkCartItem(
            @PathVariable int cartItemId,
            @RequestBody CartItemCheckStatusRequestDTO requestDTO
    ) {
        CartResponseDTO response = cartService.updateCartItemCheckStatus(getCurrentUserId(), cartItemId, requestDTO);
        return ResponseEntity.ok(response);
    }

    // 장바구니 상품 삭제
    @DeleteMapping("/delete/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(
            @PathVariable int cartItemId
    ) {
        cartService.removeCartItem(getCurrentUserId(), cartItemId);
        return ResponseEntity.noContent().build();
    }

    // 장바구니 전체 삭제 (유저 기준)
    @DeleteMapping("/deleteAll")
    public ResponseEntity<Void> clearCart() {
        cartService.clearCart(getCurrentUserId());
        return ResponseEntity.noContent().build();
    }

    // 장바구니 목록 조회 (유저 기준)
    @GetMapping("/findAll")
    public ResponseEntity<CartResponseDTO> findAllCartItems() {

        CartResponseDTO response = cartService.findAllCartItems(getCurrentUserId());
        return ResponseEntity.ok(response);
    }
}
