package com.market.market.product.service;

import com.market.market.product.dto.ProductResponseDTO;

import java.util.List;

public interface ProductService {

    /**
     *
     * TODO: 페이징 처리, 조회 조건 설정
     */
   List<ProductResponseDTO> findAll();

} // end class
