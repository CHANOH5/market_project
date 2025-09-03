package com.cs.market.product.entity;

import com.cs.market.categories.entity.Category;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description",  columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProductStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Version
    private Long version;

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

    private Product(String name, String description, BigDecimal price,
                    Integer stock, Category category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.status = (stock != null && stock > 0) ? ProductStatus.FOR_SALE : ProductStatus.SOLD_OUT;
    }

    public static Product of(String name, String description, BigDecimal price, Integer stock, Category category) {
        return new Product(name, description, price, stock, category);
    }

    // ======================= 정적 팩토리 메서드 (비즈니스 로직) - 생성과 검증 =====================

    public boolean isOutOfStock() {
        return stock == null || stock <= 0;
    }

    // 상품의 추가, 재고 추가, 상품 변경은 이후에 추가 예정
    // 현재 DB에 직접 상품 데이터를 넣으면서 "FOR_SALE" 넣는 중
    // 재고 없다가 재고 추가 시 "FOR_SALE"로 변경돼야 하고
    // 주문 시 재고 차감이 돼야하는데 이 경우 상품의 재고를 차감시켜야함(order에서)

    /** 재고 선예약(차감), 품절/삭제/재고부족 검사 포함 */
    public void reserve(int quantity) {

        if(quantity <= 0) {
            throw new IllegalArgumentException("요청 수량은 양수이어야 합니다.");
        } // if

        if(this.status == ProductStatus.DELETED) {
            throw new IllegalStateException("삭제된 상품입니다.");
        } // if

        int currentStock = this.stock == null ? 0 : this.stock;
        if(currentStock < quantity) {
            throw new IllegalStateException("재고가 부족합니다. 현재 재고: " + currentStock);
        } // if

        this.stock = currentStock - quantity;

        if(this.stock == 0) {
            this.status = ProductStatus.SOLD_OUT;
        } // if
    } // reserve

    /** 결제 실패/취소 시 재고 복원 */
    public void restore(int quantity) {
        if(quantity <= 0) {
            throw new IllegalArgumentException("요청 수량은 양수이어야 합니다.");
        } // if

        int currentStock = this.stock == null ? 0 : this.stock;

        this.stock = currentStock + quantity;

        if(this.stock > 0 && this.status == ProductStatus.SOLD_OUT) {
            this.status = ProductStatus.FOR_SALE;
        } // if
    } // restore




} // end class
