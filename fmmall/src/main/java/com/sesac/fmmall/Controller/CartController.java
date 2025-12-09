package com.sesac.fmmall.Controller;

import com.sesac.fmmall.DTO.CartItem.CartItemCreateRequestDTO;
import com.sesac.fmmall.DTO.CartItem.CartItemUpdateRequestDTO;
import com.sesac.fmmall.DTO.CartResponseDTO;
import com.sesac.fmmall.Service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Cart", description = "장바구니 API")
@RestController
@RequestMapping("/Cart")
@RequiredArgsConstructor
public class CartController extends BaseController {

    private final CartService cartService;

    // 장바구니 상품 추가
    @Operation(summary = "장바구니 상품 추가", description = "장바구니에 상품을 추가합니다.")
    @PostMapping("/insert/")
    public ResponseEntity<CartResponseDTO> addCartItem(
            @RequestBody CartItemCreateRequestDTO requestDTO
    ) {
        CartResponseDTO response = cartService.createCartItem(getCurrentUserId(), requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 장바구니 상품 수량 변경
    @Operation(summary = "장바구니 상품 수량 변경", description = "장바구니에 상품의 수량을 변경합니다.")
    @PutMapping("/modify/{cartItemId}")
    public ResponseEntity<CartResponseDTO> modifyCartItem(
            @PathVariable int cartItemId,
            @RequestBody CartItemUpdateRequestDTO requestDTO
    ) {
        CartResponseDTO response = cartService.updateCartItemQuantity(getCurrentUserId(), cartItemId, requestDTO);
        return ResponseEntity.ok(response);
    }

    // 장바구니 상품 삭제
    @Operation(summary = "장바구니 상품 삭제", description = "장바구니에서 상품을 삭제합니다.")
    @DeleteMapping("/delete/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(
            @PathVariable int cartItemId
    ) {
        cartService.removeCartItem(getCurrentUserId(), cartItemId);
        return ResponseEntity.noContent().build();
    }

    // 장바구니 전체 삭제 (유저 기준)
    @Operation(summary = "장바구니 전체 삭제", description = "장바구니의 모든 상품을 삭제합니다.")
    @DeleteMapping("/deleteAll")
    public ResponseEntity<Void> clearCart() {
        cartService.clearCart(getCurrentUserId());
        return ResponseEntity.noContent().build();
    }

    // 장바구니 목록 조회 (유저 기준)
    @Operation(summary = "장바구니 목록 조회", description = "장바구니에 담긴 상품 목록을 조회합니다.")
    @GetMapping("/findAll")
    public ResponseEntity<CartResponseDTO> findAllCartItems() {

        CartResponseDTO response = cartService.findAllCartItems(getCurrentUserId());
        return ResponseEntity.ok(response);
    }
}
