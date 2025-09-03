package com.cs.market.product.service;

import com.cs.market.categories.entity.Category;
import com.cs.market.product.dto.ProductResponseDTO;
import com.cs.market.product.entity.Product;
import com.cs.market.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    private ProductRepository productRepository;

    private ProductServiceImpl productServiceImpl;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        productServiceImpl = new ProductServiceImpl(productRepository);
    }

    @Test
    void 상품목록조회_카테고리_포함() {
        // given
        Long categoryId = 1L;
        String name = "Iphone";
        BigDecimal minPrice = new BigDecimal("100");
        BigDecimal maxPrice = new BigDecimal("1000");
        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "id"));

        Product product1 = mock(Product.class);
        Category category1 = mock(Category.class);

        when(product1.getId()).thenReturn(1L);
        when(product1.getName()).thenReturn("Iphone A");
        when(product1.getDescription()).thenReturn("desc");
        when(product1.getPrice()).thenReturn(new BigDecimal("300.00"));
        when(product1.getStock()).thenReturn(5);
        when(product1.getUpdatedBy()).thenReturn("admin");
        when(product1.getUpdatedAt()).thenReturn(LocalDateTime.of(2025, 8, 28, 10, 0));
        when(product1.getCategory()).thenReturn(category1);
        when(product1.isOutOfStock()).thenReturn(false);

        when(category1.getId()).thenReturn(10L);
        when(category1.getName()).thenReturn("Electronics");

        Page<Product> repoPage = new PageImpl<Product>(List.of(product1), pageable, 1L);

        when(productRepository.search(categoryId, name, minPrice, maxPrice, pageable))
                .thenReturn(repoPage);

        // when
        Page<ProductResponseDTO> result =
                productServiceImpl.findProducts(categoryId, name, minPrice, maxPrice, pageable);

        // then
        assertEquals(1, result.getTotalElements());
        ProductResponseDTO dto = result.getContent().get(0);
        assertEquals(1L, dto.getId());
        assertEquals("Iphone A", dto.getName());
        assertEquals(new BigDecimal("300.00"), dto.getPrice());
        assertEquals(Integer.valueOf(5), dto.getStock());
        assertFalse(dto.isOutOfStock());
        assertEquals(Long.valueOf(10L), dto.getCategoryId());
        assertEquals("Electronics", dto.getCategoryName());
        assertEquals("admin", dto.getUpdatedBy());
        assertEquals(LocalDateTime.of(2025, 8, 28, 10, 0), dto.getUpdatedAt());

        verify(productRepository, times(1))
                .search(categoryId, name, minPrice, maxPrice, pageable);
    }

} // end class