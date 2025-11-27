package com.market.market.product.service;

import com.market.market.product.dto.ProductResponseDTO;
import com.market.market.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findAll() {
        return productRepository.findAll()
                .stream()
                .map(ProductResponseDTO::from)
                .toList();
    }

} // end class
