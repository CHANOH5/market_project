package com.cs.market.cart.entity;

import com.cs.market.product.entity.Product;
import com.cs.market.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="id", nullable = false)
    private Long id;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreatedBy
    @Column(name = "created_by", nullable = false, length = 255)
    private String createdBy;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedBy
    @Column(name = "updated_by", nullable = false, length = 255)
    private String updatedBy;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    private Cart(User user) {
        this.user = user;
    } // Cart


    // ======================= 정적 팩토리 메서드 (비즈니스 로직) - 생성과 검증 =====================

    public static Cart from(User user) {
        return new Cart(user);
    } // of

    /** 동일 상품 있으면 수량 증가, 없으면 새 아이템 추가 */
    public CartItem addOrIncrease(Product product, int quantity, int currentStock) {

        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        } // if

        CartItem existing = findItem(product.getId());

        if (existing != null) {
            // 합산 수량 검증은 CartItem.changeQuantity가 수행
            existing.changeQuantity(existing.getQuantity() + quantity, currentStock);
            return existing;
        } // if

        if (quantity > currentStock) {
            throw new IllegalArgumentException("상품의 재고가 부족합니다. 현재 재고: " + currentStock);
        }

        CartItem created = CartItem.of(this, product, quantity); // 양방향 연결
        items.add(created);

        return created;

    } // addOrIncrease

    public CartItem findItem(Long productId) {
        return items.stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);
    }

} // end class
