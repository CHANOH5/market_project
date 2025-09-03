package com.cs.market.order.service;

import com.cs.market.cart.dto.CartLine;
import com.cs.market.order.entity.Order;
import com.cs.market.order.entity.OrderItem;
import com.cs.market.order.repository.OrderRepository;
import com.cs.market.product.entity.Product;
import com.cs.market.product.entity.ProductStatus;
import com.cs.market.product.repository.ProductRepository;
import com.cs.market.user.entity.User;
import com.cs.market.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderServiceImpl(UserRepository userRepository, OrderRepository orderRepository, ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    } // constructor

    @Transactional
    @Override
    public Long createOrder(Long userId, List<CartLine> lines) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("유효한 사용자가 아닙니다."));

        if (lines == null || lines.isEmpty()) {
            throw new IllegalArgumentException("주문 요청이 비어있습니다.");
        }

        for (CartLine line : lines) {
            if (line == null || line.getProductId() == null) {
                throw new IllegalArgumentException("주문 요청의 상품이 정보가 비어있습니다.");
            }
            if (line.getQuantity() == null || line.getQuantity() <= 0 || line.getQuantity() > 10) {
                throw new IllegalArgumentException("상품은 최소 1개에서 10개까지 주문할 수 있습니다.");
            }
        } // for

        // 2) Product 일괄 조회(가능한 N+1 방지)
        List<Long> productIds = lines.stream().map(CartLine::getProductId).toList();

        Map<Long, Product> productMap = productRepository.findAllById(productIds)
                .stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // DB에서 실제로 찾아온 상품의 개수와, 고객이 요청한 (중복을 제거한) 상품 ID의 개수가 같은지 비교
        if (productMap.size() != new HashSet<>(productIds).size()) {
            throw new IllegalArgumentException("하나 이상의 상품을 찾을 수 없거나 삭제되었습니다.");
        } // if

        // 3) 주문 생성
        Order order = Order.create(user);

        // 4) 선예약 + 스냅샷 아이템 추가
        for (CartLine line : lines) {
            Product p = productMap.get(line.getProductId());

            if (p.getStatus() == ProductStatus.DELETED) {
                throw new IllegalStateException("삭제된 상품입니다: " + p.getId());
            } // if

            // 선예약 - 낙관적 락: 동시 감액 충돌 시 OptimisticLockException 발생
            p.reserve(line.getQuantity());

            // 스냅샷 아이템 추가
            OrderItem item = OrderItem.of(
                    p.getId(),
                    p.getName(),
                    p.getPrice(),
                    line.getQuantity()
            );
            order.addItem(item);
        } // for

        // 5) 결제 대기 상태로 전이(선예약 완료 의미)
        order.markPaymentPending();

        // 6) 저장(더티체킹으로 Product.stock/version도 반영)
        // order 객체는 DB에서 조회해온 '기존' 엔티티가 아니라, Order.create(user)를 통해 메소드 내부에서 새로 생성된(New/Transient) 엔티티이기 때문에 반드시 save 필요함
        orderRepository.save(order);

        return order.getId();

    } // createOrder

} // end class
