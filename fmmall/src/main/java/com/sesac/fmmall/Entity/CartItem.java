package com.sesac.fmmall.Entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "cart_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cartItemId;

    private int quantity;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime addDate;

    @Column(length = 1)
    private String checkStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    public static CartItem createCartItem(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("상품은 필수입니다.");
        }
        if (quantity < 1) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        }
        // 생성 시점에도 재고 확인
        if (quantity > product.getStockQuantity()) {
            throw new IllegalArgumentException("상품의 재고가 부족합니다.");
        }
        CartItem cartItem = new CartItem();
        cartItem.product = product;
        cartItem.quantity = quantity;
        cartItem.checkStatus = "N";
        return cartItem;
    }

    /**
     * 수량을 업데이트합니다. 수량은 1 이상이어야 하며, 상품 재고를 초과할 수 없습니다.
     * @param quantity 새로운 수량
     */
    public void updateQuantity(int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("상품 수량은 1개 이상이어야 합니다.");
        }
        // 재고 확인 로직
        if (this.product != null && quantity > this.product.getStockQuantity()) {
            throw new IllegalArgumentException("상품의 재고가 부족합니다.");
        }
        this.quantity = quantity;
    }

    public void updateCheckStatus(String checkStatus) {
        this.checkStatus = checkStatus;
    }

    public void associateWithCart(Cart cart) {
        this.cart = cart;
    }
}
