package com.cs.market.cart.dto;

import com.cs.market.cart.entity.CartItem;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class CartItemResponseDTO {


    private Long productId;
    private String productName;
    private BigDecimal price;
    private int quantity;
    private BigDecimal subTotal;  // 개별 합계
    private boolean outOfStock;   // 품절 여부
    public CartItemResponseDTO(Long productId, String productName, BigDecimal price,
                               int quantity, BigDecimal subTotal, boolean outOfStock) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.subTotal = subTotal;
        this.outOfStock = outOfStock;
    } // constructor

    public static CartItemResponseDTO from(CartItem cartItem) {
        BigDecimal price = cartItem.getProduct().getPrice();
        int qty = cartItem.getQuantity();
        BigDecimal subTotal = price.multiply(BigDecimal.valueOf(qty));
        Integer stock = cartItem.getProduct().getStock();
        boolean outOfStock = (stock == null) || (stock <= 0);

        return new CartItemResponseDTO(
                cartItem.getProduct().getId(),
                cartItem.getProduct().getName(),
                price,
                qty,
                subTotal,
                outOfStock
        );
    }

} // end class
