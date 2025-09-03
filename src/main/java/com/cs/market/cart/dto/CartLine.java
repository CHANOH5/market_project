package com.cs.market.cart.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CartLine {

    private Long productId;
    private Integer quantity;

    public CartLine(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

}
