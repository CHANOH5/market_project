package com.market.market.product.dto;

import com.market.market.product.entity.Product;
import com.market.market.product.entity.ProductStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductResponseDTO {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private ProductStatus status;

    private Long categoryId;
    private String categoryName;

    private String updatedBy;
    private LocalDateTime updatedAt;

    private boolean isOutOfStock; // 품절 여부 , null이거나 0보다 같거나 작으면 true 반환

    //  EntityToDTO
    public static ProductResponseDTO from(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .status(product.getStatus())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .updatedBy(product.getUpdatedBy())
                .updatedAt(product.getUpdatedAt())
                .isOutOfStock(product.isOutOfStock())
                .build();
    } // from()

} // end class
