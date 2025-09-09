package com.cs.market.order;

import com.cs.market.cart.dto.CartLine;
import com.cs.market.order.entity.Order;
import com.cs.market.order.entity.OrderStatus;
import com.cs.market.order.repository.OrderRepository;
import com.cs.market.order.service.OrderServiceImpl;
import com.cs.market.product.entity.Product;
import com.cs.market.product.entity.ProductStatus;
import com.cs.market.product.repository.ProductRepository;
import com.cs.market.user.entity.User;
import com.cs.market.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // JUnit 5에서 Mockito를 사용하기 위한 확장
class OrderServiceImplTest {

    @Mock // 가짜 객체(Mock) 생성
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks // @Mock으로 생성된 객체들을 주입
    private OrderServiceImpl orderServiceImpl;

    private User testUser;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        // 각 테스트 실행 전에 공통적으로 사용할 객체들을 초기화합니다.
        testUser = User.from(new com.cs.market.user.dto.UserRequestDTO("testUser", "password", "test@email.com"));
        // ReflectionTestUtils.setField(testUser, "id", 1L); // 실제 ID가 필요하다면 사용

        product1 = Product.of(
                "상품1",
                "상품1 설명",
                new BigDecimal("10000"),
                10,
                null // 테스트 편의상 category는 null로 전달
        );
        ReflectionTestUtils.setField(product1, "id", 101L);

        product2 = Product.of(
                "상품2",
                "상품2 설명",
                new BigDecimal("25000"),
                5,
                null
        );
        ReflectionTestUtils.setField(product2, "id", 102L);

    }

    // ======================== 성공 케이스 ========================

    @Test
    @DisplayName("주문 생성 성공")
    void createOrder_Success() {
        // Given (준비)
        Long userId = 1L;
        List<CartLine> cartLines = List.of(
                new CartLine(101L, 2), // 상품1 2개
                new CartLine(102L, 1)  // 상품2 1개
        );

        // Mock 객체들의 행동 정의
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(productRepository.findAllById(anyList())).thenReturn(List.of(product1, product2));
        // orderRepository.save가 호출될 때, 저장되는 Order 객체에 ID를 부여하는 것처럼 시뮬레이션
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            // 실제 ID가 필요하다면 ReflectionTestUtils.setField(savedOrder, "id", 999L);
            return savedOrder;
        });

        // When (실행)
        orderServiceImpl.createOrder(userId, cartLines);

        // Then (검증)
        // 1. orderRepository의 save 메소드가 1번 호출되었는지 검증
        ArgumentCaptor<Order> orderArgumentCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(1)).save(orderArgumentCaptor.capture());

        // 2. 저장된 Order 객체의 상태 검증
        Order capturedOrder = orderArgumentCaptor.getValue();
        assertEquals(testUser.getUserName(), capturedOrder.getUser().getUserName());
        assertEquals(OrderStatus.PAYMENT_PENDING, capturedOrder.getStatus());
//        assertEquals(2, capturedOrder.getItems().size());
        // 총 금액 계산: (10000 * 2) + (25000 * 1) = 45000
        assertEquals(0, new BigDecimal("45000").compareTo(capturedOrder.getTotalAmount()));

        // 3. 상품 재고가 정상적으로 차감되었는지 검증
        assertEquals(8, product1.getStock()); // 10 - 2
        assertEquals(4, product2.getStock()); // 5 - 1
    }

    // ======================== 실패 케이스 ========================

    @Test
    @DisplayName("실패: 사용자를 찾을 수 없음")
    void createOrder_Fail_UserNotFound() {
        // Given
        Long userId = 99L; // 존재하지 않는 사용자 ID
        List<CartLine> cartLines = List.of(new CartLine(101L, 1));

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NoSuchElementException.class, () -> {
            orderServiceImpl.createOrder(userId, cartLines);
        });

        // save 메소드가 절대 호출되지 않았는지 검증
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("실패: 주문 상품 목록이 비어있음")
    void createOrder_Fail_EmptyCartLines() {
        // Given
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        //Then
        assertThrows(IllegalArgumentException.class, () -> {
            orderServiceImpl.createOrder(userId, List.of()); // 비어있는 리스트
        });
    }

    @Test
    @DisplayName("실패: 상품 ID가 없는 상품이 포함됨")
    void createOrder_Fail_ProductNotFound() {
        // Given
        Long userId = 1L;
        List<CartLine> cartLines = List.of(
                new CartLine(101L, 1),
                new CartLine(999L, 1) // 존재하지 않는 상품 ID
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        // DB 조회 결과, 요청한 상품 중 일부만 반환되는 상황 시뮬레이션
        when(productRepository.findAllById(anyList())).thenReturn(List.of(product1));

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            orderServiceImpl.createOrder(userId, cartLines);
        });
        assertEquals("하나 이상의 상품을 찾을 수 없거나 삭제되었습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("실패: 재고 부족")
    void createOrder_Fail_InsufficientStock() {
        // Given
        Long userId = 1L;
        ReflectionTestUtils.setField(product1, "stock", 5);
        List<CartLine> cartLines = List.of(new CartLine(101L, 6));  // 재고보다 1개 더 주문

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(productRepository.findAllById(anyList())).thenReturn(List.of(product1));

        // When & Then
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            orderServiceImpl.createOrder(userId, cartLines);
        });
        assertTrue(exception.getMessage().contains("재고가 부족합니다."));
    }

    @Test
    @DisplayName("실패: 삭제된 상품 주문")
    void createOrder_Fail_OrderDeletedProduct() {
        // Given
        Long userId = 1L;
        ReflectionTestUtils.setField(product1, "status", ProductStatus.DELETED);

        List<CartLine> cartLines = List.of(new CartLine(101L, 1));

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(productRepository.findAllById(anyList())).thenReturn(List.of(product1));

        // When & Then
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            orderServiceImpl.createOrder(userId, cartLines);
        });
        assertTrue(exception.getMessage().contains("삭제된 상품입니다"));
    }

    @Test
    @DisplayName("실패: 주문 수량이 0 이하인 경우")
    void createOrder_Fail_InvalidQuantity() {
        // Given
        Long userId = 1L;
        List<CartLine> cartLines = List.of(new CartLine(101L, 0)); // 잘못된 수량

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Then
        assertThrows(IllegalArgumentException.class, () -> {
            orderServiceImpl.createOrder(userId, cartLines);
        });
    }
}