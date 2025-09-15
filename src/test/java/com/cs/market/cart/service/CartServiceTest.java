package com.cs.market.cart.service;

import com.cs.market.cart.dto.CartDetailResponseDTO;
import com.cs.market.cart.entity.Cart;
import com.cs.market.cart.entity.CartItem;
import com.cs.market.cart.repository.CartItemRepository;
import com.cs.market.cart.repository.CartRepository;
import com.cs.market.categories.entity.Category;
import com.cs.market.product.entity.Product;
import com.cs.market.product.entity.ProductStatus;
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
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
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
        testProduct = Product.of("testProduct", "test", new BigDecimal("1"), 10, mockCategory);

        ReflectionTestUtils.setField(testUser, "id", 1L);
        ReflectionTestUtils.setField(testProduct, "id", 10L);
        ReflectionTestUtils.setField(mockCategory, "id", 1L);

        // 제품 상태를 명시적으로 판매중으로 설정
        ReflectionTestUtils.setField(testProduct, "status", ProductStatus.FOR_SALE);
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

    @Test
    void 장바구니_조회_이미있음() {

        // given
        Long userId = testUser.getId();

        Cart cart = Cart.from(testUser);
        cart.addOrIncrease(testProduct, 2, 10);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(cartRepository.findWithItemsByUserId(userId)).thenReturn(Optional.of(cart));

        // when
        CartDetailResponseDTO result = cartService.findCartWithItems(userId);

        // then
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getItemCount()).isEqualTo(1);
        assertThat(result.getItems().get(0).getProductId()).isEqualTo(10);

    }

    @Test
    void 상품_수량_설정_성공() {

        // given
        Long userId = testUser.getId();
        Long productId = testProduct.getId();

        Cart cart = Cart.from(testUser);
        cart.addOrIncrease(testProduct, 2, 10);
        ReflectionTestUtils.setField(cart, "id", 100L);
        CartItem item = cart.getItems().get(0);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(cartRepository.findWithItemsByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndProductId(100L, productId)).thenReturn(Optional.of(item));
        // when
        cartService.setQuantity(userId, productId, 5);

        // then
        assertThat(item.getQuantity()).isEqualTo(5);
    }

    @Test
    void 상품_수량_설정_재고초과면_예외() {
        // given
        Long userId = testUser.getId();
        Long productId = testProduct.getId();
        ReflectionTestUtils.setField(testProduct, "stock", 3);

        Cart cart = Cart.from(testUser);
        cart.addOrIncrease(testProduct, 2, 10);
        ReflectionTestUtils.setField(cart, "id", 200L);
        CartItem item = cart.getItems().get(0);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(cartRepository.findWithItemsByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndProductId(200L, productId))
                .thenReturn(Optional.of(item));

        // when & then
        assertThatThrownBy(() -> cartService.setQuantity(userId, productId, 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("재고");
    }

    @Test
    void 상품_수량_증가() {
        // given
        Long userId = testUser.getId();
        Long productId = testProduct.getId();

        Cart cart = Cart.from(testUser);
        cart.addOrIncrease(testProduct, 1, 10);
        ReflectionTestUtils.setField(cart, "id", 100L);
        CartItem item = cart.getItems().get(0);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(cartRepository.findWithItemsByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndProductId(100L, productId))
                .thenReturn(Optional.of(item));

        // when
        cartService.changeQuantityByProduct(userId, productId, true);

        // then
        assertThat(item.getQuantity()).isEqualTo(2);
    }

    @Test
    void 상품_수량_감소_최소1이하로_내리면_예외() {
        // given
        Long userId = testUser.getId();
        Long productId = testProduct.getId();

        Cart cart = Cart.from(testUser);
        cart.addOrIncrease(testProduct, 1, 10);
        ReflectionTestUtils.setField(cart, "id", 301L);
        CartItem item = cart.getItems().get(0);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(cartRepository.findWithItemsByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndProductId(301L, productId))
                .thenReturn(Optional.of(item));

        // when & then (수량 1에서 -1 하면 CartItem 내부 검증 로직에 따라 예외)
        assertThatThrownBy(() -> cartService.changeQuantityByProduct(userId, productId, false))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 장바구니_전체비우기() {
        // given
        Long userId = testUser.getId();

        Cart cart = Cart.from(testUser);
        cart.addOrIncrease(testProduct, 3, 10);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(cartRepository.findWithItemsByUserId(userId)).thenReturn(Optional.of(cart));

        // when
        cartService.deleteCartItemsByUserId(userId);

        // then
        verify(cartItemRepository, times(1)).deleteAll(cart.getItems());
    }

    @Test
    void 장바구니_특정상품_삭제() {
        // given
        Long userId = testUser.getId();
        Long productId = testProduct.getId();

        Cart cart = Cart.from(testUser);
        cart.addOrIncrease(testProduct, 3, 10);
        ReflectionTestUtils.setField(cart, "id", 400L);
        CartItem item = cart.getItems().get(0);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(cartRepository.findWithItemsByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndProductId(400L, productId))
                .thenReturn(Optional.of(item));

        // when
        cartService.withdraw(userId, productId);

        // then
        verify(cartItemRepository, times(1)).delete(item);
    }

} // end class
