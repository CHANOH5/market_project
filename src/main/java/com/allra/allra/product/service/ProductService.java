package com.allra.allra.product.service;

import com.allra.allra.product.dto.ProductResponseDTO;
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
