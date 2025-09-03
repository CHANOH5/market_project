package com.cs.market.product.service;

import com.cs.market.product.dto.ProductResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface ProductService {

    Page<ProductResponseDTO> findProducts(Long categoryId,
                                          String name,
                                          BigDecimal minPrice,
                                          BigDecimal maxPrice,
                                          Pageable pageable);

} // end class
