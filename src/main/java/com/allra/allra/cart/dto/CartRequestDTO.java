package com.allra.allra.cart.dto;

import com.allra.allra.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CartRequestDTO {

    private Long id;

    private Long userId;

    @Builder
    public CartRequestDTO(Long id, Long userId) {
        this.id = id;
        this.userId = userId;
    } // constructor

} // end class
