package com.sesac.fmmall.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "cart")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cartId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    /**
     * 장바구니에 상품을 추가합니다. 중복된 상품일 경우 수량을 증가시킵니다.
     * 이 때, 상품의 재고를 초과할 수 없습니다.
     */
    public void addCartItem(CartItem newCartItem) {
        if (newCartItem == null || newCartItem.getProduct() == null) {
            throw new IllegalArgumentException("장바구니에 추가할 상품 정보가 올바르지 않습니다.");
        }

        Product product = newCartItem.getProduct();
        int stock = product.getStockQuantity(); // 현재 상품 재고

        CartItem existingItem = cartItems.stream()
                .filter(item -> item.getProduct() != null && Objects.equals(item.getProduct().getProductId(), product.getProductId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            // 이미 상품이 존재할 경우: 기존 수량 + 새 수량이 재고보다 적은지 확인
            int totalQuantity = existingItem.getQuantity() + newCartItem.getQuantity();
            if (totalQuantity > stock) {
                throw new IllegalArgumentException("상품의 재고가 부족합니다.");
            }
            existingItem.updateQuantity(totalQuantity);
        } else {
            // 새로운 상품일 경우: 새 수량이 재고보다 적은지 확인 (createCartItem에서 이미 검증했지만, 한번 더 방어)
            if (newCartItem.getQuantity() > stock) {
                throw new IllegalArgumentException("상품의 재고가 부족합니다.");
            }
            cartItems.add(newCartItem);
            newCartItem.associateWithCart(this);
        }
    }

    /**
     * 장바구니에서 상품을 제거합니다.
     */
    public void removeCartItem(CartItem cartItem) {
        cartItems.remove(cartItem);
        cartItem.associateWithCart(null);
    }

    /**
     * 장바구니를 비웁니다.
     */
    public void clearCart() {
        this.cartItems.clear();
    }
}
