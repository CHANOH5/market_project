package com.cs.market.cart.dto;

import com.cs.market.cart.entity.Cart;
import com.cs.market.cart.entity.CartItem;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class CartDetailResponseDTO {

    private Long cartId;
    private Long userId;
    private List<CartItemResponseDTO> items;
    private Integer itemCount;
    private Integer totalQuantity;
    private BigDecimal totalAmount;

    public CartDetailResponseDTO(Long cartId, Long userId, List<CartItemResponseDTO> items, Integer totalQuantity, BigDecimal totalAmount) {
        this.cartId = cartId;
        this.userId = userId;
        this.items = items;
        this.itemCount = items.size();
        this.totalQuantity = totalQuantity;
        this.totalAmount = totalAmount;
    } // constructor

    public static CartDetailResponseDTO from(Cart cart) {

        List<CartItemResponseDTO> items = cart.getItems()
                .stream()
                .map(CartItemResponseDTO::from)
                .collect(Collectors.toList());

        int totalQuantity = items.stream()
                .mapToInt(CartItemResponseDTO::getQuantity)
                .sum();

        BigDecimal totalAmount = items.stream()
                .map(CartItemResponseDTO::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartDetailResponseDTO(
                cart.getId(),
                cart.getUser().getId(),
                items,
                totalQuantity,
                totalAmount
        );

    } // for


} // end class
