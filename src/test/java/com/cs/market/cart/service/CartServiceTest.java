package com.cs.market.cart.service;

import com.cs.market.cart.entity.Cart;
import com.cs.market.cart.repository.CartItemRepository;
import com.cs.market.cart.repository.CartRepository;
import com.cs.market.categories.entity.Category;
import com.cs.market.product.entity.Product;
import com.cs.market.product.repository.ProductRepository;
import com.cs.market.user.entity.User;
import com.cs.market.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock private CartRepository cartRepository;
    @Mock private CartItemRepository cartItemRepository;
    @Mock private UserRepository userRepository;
    @Mock private ProductRepository productRepository;
    @Mock private Category mockCategory;

    // @InjectMocks는 Mock 객체들을 이 객체에 자동으로 주입
    @InjectMocks
    private CartServiceImpl cartService;

    // 가짜 데이터
    private User testUser;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testUser = User.of("testUser", "1234", "test@test.com", 1);
        testProduct = Product.of("testProduct", "test", new BigDecimal("1"), 1, mockCategory);

        ReflectionTestUtils.setField(testUser, "id", 1L);
        ReflectionTestUtils.setField(testProduct, "id", 10L);
        ReflectionTestUtils.setField(mockCategory, "id", 1L);
    }

    /**
     * 성공 케이스: 정상 추가(기존 장바구니 있음, 기존 장바구니 없음)
     * 실패 케이스: 품절된 상품 추가, 판매 중단된 상품 추가, 재고 부족 상품 추가
     */

    @Test
    void 장바구니_상품추가() {

        // given
        Long userId = testUser.getId();
        Long productId = testProduct.getId();
        int quantity = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(cartRepository.findWithItemsByUserIdForUpdate(userId)).thenReturn(Optional.empty());

        // when
        cartService.addItem(userId, productId, quantity);

        // then

        // Cart 객체를 갭처할 ArgumentCaptort 생성
        ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);

        // save 메서드를 호출했는지 확인
        verify(cartRepository, times(1)).save(cartCaptor.capture());

        // 캡처한 Cart 객체 가져옴
        Cart savedCart = cartCaptor.getValue();

        assertThat(savedCart.getUser()).isEqualTo(testUser);
        assertThat(savedCart.getItems()).hasSize(1);
        assertThat(savedCart.getItems().get(0).getProduct()).isEqualTo(testProduct);
        assertThat(savedCart.getItems().get(0).getQuantity()).isEqualTo(quantity);

    }

    @Test
    void 장바구니_품절된_상품추가() {

        // given
        Long userId = testUser.getId();
        Long productId = testProduct.getId();
        int quantity = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(cartRepository.findWithItemsByUserIdForUpdate(userId)).thenReturn(Optional.empty());

        // when
        cartService.addItem(userId, productId, quantity);

    }



} // end class
