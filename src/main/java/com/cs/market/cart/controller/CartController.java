package com.cs.market.cart.controller;

import com.cs.market.cart.dto.CartDetailResponseDTO;
import com.cs.market.cart.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    } // constructor

    // 카트에 상품 추가
    @PostMapping("/{userId}/items/{productId}")
    public ResponseEntity<Void> addItem(@PathVariable Long userId,
                                        @PathVariable Long productId,
                                        @RequestParam int quantity) {
        cartService.addItem(userId, productId, quantity);
        return ResponseEntity.noContent().build();
    }

    // 카트 조회
    @GetMapping("/{userId}")
    public ResponseEntity<CartDetailResponseDTO> findCart(@PathVariable Long userId) {
        CartDetailResponseDTO dto = cartService.findCartWithItems(userId);
        return ResponseEntity.ok(dto);
    } // findCart

    // 수량 변경 (멱등)
    @PutMapping("/{userId}/itmes/{productId}/quantity")
    public ResponseEntity<Void> setQuantity(@PathVariable Long userId,
                                            @PathVariable Long productId,
                                            @RequestBody int quantity) {
        cartService.setQuantity(userId, productId, quantity);
        return ResponseEntity.noContent().build();
    } // setQuantity

    // 수량 증감(비멱등)
    @PatchMapping("/{userId}/items/{productId}/quantity")
    public ResponseEntity<Void> changeQuantityByProduct(@PathVariable Long userId,
                                                        @PathVariable Long productId,
                                                        @RequestParam("isIncrease") boolean isIncrease) {
        cartService.changeQuantityByProduct(userId, productId, isIncrease);
        return ResponseEntity.noContent().build();
    } // changeQuantityByOne

    // 장바구니 전체 삭제
    @DeleteMapping("/{userId}/items")
    public ResponseEntity<Void> deleteCartItemsByUserId(@PathVariable Long userId) {
        cartService.deleteCartItemsByUserId(userId);
        return ResponseEntity.noContent().build();
    } // deleteCartItemsByUserId

    // 장바구니 특정 상품 삭제
    @DeleteMapping("/{userId}/items/{productId}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long userId,
                                               @PathVariable Long productId) {
        cartService.withdraw(userId, productId);
        return ResponseEntity.noContent().build();
    } // deleteCartItem

} // end class
