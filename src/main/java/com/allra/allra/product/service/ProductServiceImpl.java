package com.allra.allra.product.service;

import com.allra.allra.product.dto.ProductResponseDTO;
import com.allra.allra.product.entity.Product;
import com.allra.allra.product.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    } // constructor

    /**
     * 카테고리, 상품 이름, 가격 범위 조건을 기준으로 상품 목롣을 조회합니다.
     *
     * @param categoryId    카테고리ID (nullable)
     * @param name          상품 이름 (nullable)
     * @param minPrice      최소 금액 (nullable)
     * @param maxPrice      최대 금액 (nullable)
     * @param pageable      요청 페이지 (page, size, sort)
     * @return              요청한 조건에 일치하는 상품 목록을 반환합니다.
     */
    @Override
    public Page<ProductResponseDTO> findProducts(Long categoryId,
                                                 String name,
                                                 BigDecimal minPrice,
                                                 BigDecimal maxPrice,
                                                 Pageable pageable) {

        validate(minPrice, maxPrice);

        Page<Product> page = productRepository.search(categoryId, name, minPrice, maxPrice, pageable);

        return page.map(ProductResponseDTO::from);

    } // findProducts();

    // ======================== 헬퍼메서드 ========================

    private void validate(BigDecimal minPrcie, BigDecimal maxPrice) {
        if(minPrcie != null && maxPrice != null && minPrcie.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("minPrice must be <= maxPrice");
        } // if
    } // validate

} // end class
