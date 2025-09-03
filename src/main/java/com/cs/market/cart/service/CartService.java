package com.cs.market.cart.service;

import com.cs.market.cart.dto.CartDetailResponseDTO;
import com.cs.market.cart.dto.CartItemRequestDTO;

import java.util.List;

public interface CartService {

    // 장바구니에 상품 추가
    void addItem(Long userId, Long productId, int quantity);

    // 장바구니 조회
    CartDetailResponseDTO findCartWithItems(Long userId);

    // 장바구니에서 삼풍 수량 변경 (팝업에서 한번에 수량 변경할 때)
    void setQuantity(Long userId, Long productId, int quantity);

    // 장바구니에서 상품 수량 변경 (수량 한개씩 차감)
    void changeQuantityByProduct(Long userId, Long productId, boolean increase);

    // 장바구니 전체 상품 삭제
    void deleteCartItemsByUserId(Long userId);

    // 장바구니에서 상품 삭제
    void withdraw(Long userId, Long productId);

} // end class
