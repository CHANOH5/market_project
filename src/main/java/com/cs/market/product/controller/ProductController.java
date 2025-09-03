package com.cs.market.product.controller;

import com.cs.market.product.dto.ProductResponseDTO;
import com.cs.market.product.service.ProductService;
import com.cs.market.product.service.ProductServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    } // constructor

    /**
     * 상품 목록 조회 API
     */
    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> findProducts(@RequestParam(required = false) Long categoryId,
                                                                 @RequestParam(required = false) String name,
                                                                 @RequestParam(required = false) BigDecimal minPrice,
                                                                 @RequestParam(required = false) BigDecimal maxPrice,
                                                                 @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ProductResponseDTO> result = productService.findProducts(categoryId, name, minPrice, maxPrice, pageable);

        return ResponseEntity.ok(result);

    } // findProducts()

} // end class
