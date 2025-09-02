package com.allra.allra.cart.dto;

import com.allra.allra.cart.entity.Cart;
import com.allra.allra.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CartResponseDTO {

    private Long id;

    private Long userId;

    private String userName;

    private LocalDateTime updatedAt;

    @Builder
    public CartResponseDTO(Long id, Long userId, String userName, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.updatedAt = updatedAt;
    } // constructor

    public static CartResponseDTO from(Cart cart) {
        return CartResponseDTO.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .userName(cart.getUser().getUserName())
                .updatedAt(cart.getUpdatedAt())
                .build();
    } // from

} // end class
