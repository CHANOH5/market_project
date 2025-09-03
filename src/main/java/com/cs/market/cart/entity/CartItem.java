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

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

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

    @Builder
    public CartItem(Cart cart, Product product, int quantity) {
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
    } // Constructor

    public void changeQuantity(int quantity, int currentStock) {

        if(quantity <= 0) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        }

        if (currentStock < 0) currentStock = 0; // 방어 코드

        if(quantity > currentStock) {
            throw new IllegalArgumentException("상품의 재고가 부족합니다. 현재 재고 : " + currentStock);
        }

        this.quantity = quantity;
    } // changeQuantity

    public void chnageQuantityByOne(boolean isIncrease, int currentStock) {

        int base = (this.quantity == null ? 0 : this.quantity);
        int next = base + (isIncrease ? 1 : -1);

        changeQuantity(next, currentStock);

    } // chnageQuantityByOne

    /** Cart가 양방향 관계를 세팅할 때 사용할 팩토리 */
    public static CartItem of(Cart cart, Product product, int quantity) {
        return CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(quantity)
                .build();
    }

} // end class
