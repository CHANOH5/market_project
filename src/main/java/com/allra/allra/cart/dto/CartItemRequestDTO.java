package com.allra.allra.cart.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CartItemRequestDTO {

    private Long productId;

    private int quantity;
    
} // end class
