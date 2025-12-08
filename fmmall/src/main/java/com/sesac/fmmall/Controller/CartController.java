package com.sesac.fmmall.Controller;

import com.sesac.fmmall.DTO.CartItem.CartItemCheckStatusRequestDTO;
import com.sesac.fmmall.DTO.CartItem.CartItemCreateRequestDTO;
import com.sesac.fmmall.DTO.CartItem.CartItemUpdateRequestDTO;
import com.sesac.fmmall.DTO.CartResponseDTO;
import com.sesac.fmmall.DTO.User.UserResponseDto;
import com.sesac.fmmall.Service.CartService;
import com.sesac.fmmall.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    // 장바구니 상품 추가
    @PostMapping("/insert/")
    public ResponseEntity<CartResponseDTO> addCartItem(
            @RequestBody CartItemCreateRequestDTO requestDTO,
            @AuthenticationPrincipal int userId
    ) {
        UserResponseDto dto = userService.getUserInfo(userId);

        CartResponseDTO response = cartService.createCartItem(dto.getId(), requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 장바구니 상품 수량 변경
    @PutMapping("/modify/{cartItemId}")
    public ResponseEntity<CartResponseDTO> modifyCartItem(
            @AuthenticationPrincipal int userId,
            @PathVariable int cartItemId,
            @RequestBody CartItemUpdateRequestDTO requestDTO
    ) {
        UserResponseDto dto = userService.getUserInfo(userId);

        CartResponseDTO response = cartService.updateCartItemQuantity(dto.getId(), cartItemId, requestDTO);
        return ResponseEntity.ok(response);
    }

    // [추가될 메서드] 장바구니 상품 체크 상태 변경
    @PutMapping("/check/{cartItemId}")
    public ResponseEntity<CartResponseDTO> checkCartItem(
            @AuthenticationPrincipal int userId,
            @PathVariable int cartItemId,
            @RequestBody CartItemCheckStatusRequestDTO requestDTO
    ) {
        CartResponseDTO response = cartService.updateCartItemCheckStatus(userId, cartItemId, requestDTO);
        return ResponseEntity.ok(response);
    }

    // 장바구니 상품 삭제
    @DeleteMapping("/delete/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(
            @AuthenticationPrincipal int userId,
            @PathVariable int cartItemId
    ) {
        UserResponseDto dto = userService.getUserInfo(userId);
        cartService.removeCartItem(dto.getId(), cartItemId);
        return ResponseEntity.noContent().build();
    }

    // 장바구니 전체 삭제 (유저 기준)
    @DeleteMapping("/deleteAll")
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal int userId) {
        UserResponseDto dto = userService.getUserInfo(userId);

        cartService.clearCart(dto.getId());
        return ResponseEntity.noContent().build();
    }

    // 장바구니 목록 조회 (유저 기준)
    @GetMapping("/findAll")
    public ResponseEntity<CartResponseDTO> findAllCartItems(@AuthenticationPrincipal int userId) {
        UserResponseDto dto = userService.getUserInfo(userId);

        CartResponseDTO response = cartService.findAllCartItems(dto.getId());
        return ResponseEntity.ok(response);
    }
}
