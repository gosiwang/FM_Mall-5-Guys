package com.sesac.fmmall.Service;

import com.sesac.fmmall.DTO.CartItem.CartItemRequestDTO;
import com.sesac.fmmall.DTO.CartItem.CartItemResponseDTO;
import com.sesac.fmmall.DTO.CartResponseDTO;
import com.sesac.fmmall.Entity.Cart;
import com.sesac.fmmall.Entity.CartItem;
import com.sesac.fmmall.Entity.Product;
import com.sesac.fmmall.Entity.User;
import com.sesac.fmmall.Repository.CartItemRepository;
import com.sesac.fmmall.Repository.CartRepository;
import com.sesac.fmmall.Repository.ProductRepository;
import com.sesac.fmmall.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public void addCartItem(int userId, CartItemRequestDTO requestDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Product product = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        Cart cart = cartRepository.findByUser_Id(userId)
                .orElseGet(() -> cartRepository.save(Cart.builder().user(user).build()));

        CartItem newCartItem = CartItem.createCartItem(product, requestDTO.getQuantity());
        cart.addCartItem(newCartItem);
        cartRepository.save(cart);
    }

    @Transactional
    public void updateCartItemQuantity(int cartItemId, CartItemRequestDTO requestDTO) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니 상품을 찾을 수 없습니다."));
        cartItem.updateQuantity(requestDTO.getQuantity());
    }

    @Transactional
    public void removeCartItem(int cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 장바구니 상품을 찾을 수 없습니다."));

        Cart cart = cartItem.getCart();

        cart.removeCartItem(cartItem);
    }

    @Transactional
    public void clearCart(int userId) {
        Cart cart = cartRepository.findByUser_Id(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 장바구니를 찾을 수 없습니다."));
        cart.clearCart();
    }

    public CartResponseDTO findAllCartItems(int userId) {
        Optional<Cart> optCart = cartRepository.findByUser_Id(userId);

        if (optCart.isEmpty()) {
            return CartResponseDTO.builder()
                    .itemList(Collections.emptyList())
                    .totalPrice(0)
                    .build();
        }

        Cart cart = optCart.get();
        List<CartItemResponseDTO> cartItemList = cart.getCartItems().stream()
                .map(cartItem -> {
                    CartItemResponseDTO cartItemResponseDTO = modelMapper.map(cartItem, CartItemResponseDTO.class);
                    Product product = cartItem.getProduct();

                    cartItemResponseDTO.setProductId(product.getProductId());
                    cartItemResponseDTO.setProductName(product.getName());
                    cartItemResponseDTO.setProductPrice(product.getPrice());
                    cartItemResponseDTO.setTotalPrice(product.getPrice() * cartItem.getQuantity());

                    return cartItemResponseDTO;
                })
                .collect(Collectors.toList());

        int totalPrice = cartItemList.stream()
                .mapToInt(CartItemResponseDTO::getTotalPrice)
                .sum();

        return CartResponseDTO.builder()
                .cartId(cart.getCartId())
                .itemList(cartItemList)
                .totalItemCount(cartItemList.size())
                .totalPrice(totalPrice)
                .build();
    }
}
