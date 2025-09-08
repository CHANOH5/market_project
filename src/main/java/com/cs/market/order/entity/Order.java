package com.cs.market.order.entity;

import com.cs.market.user.entity.User;
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
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 255)
    private OrderStatus status;

    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

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

//    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
//    @JoinColumn(name = "order_id")
//    private List<OrderItem> items = new java.util.ArrayList<>();

    public  Order(User user, BigDecimal totalAmount) {
        this.user = user;
        this.status = OrderStatus.CREATED;
        this.totalAmount = totalAmount;
    }

    /** 주문 생성 시 사용하는 팩토리 */
    public static Order create(User user) {
        Order order = new Order();
        order.user = user;
        order.status = OrderStatus.CREATED;
        order.totalAmount = BigDecimal.ZERO;
        return order;
    }

    /** 결제 진행 단계 진입(재고 선예약 완료 시점) */
    public void markPaymentPending() {
        if(this.status != OrderStatus.CREATED) {
            throw new IllegalStateException("결제 진행 단계로 갈 수 없습니다.");
        }
        this.status = OrderStatus.PAYMENT_PENDING;
    } // markPaymentPending

    /** 결제 성공 -> 확정(복원불가) */
    public void markPaid() {
        if(this.status != OrderStatus.PAYMENT_PENDING) {
            throw new IllegalStateException("결제 단계로 갈 수 없습니다.");
        }
        this.status = OrderStatus.PAID;
    } // markPaid

    /** 결제 실패 -> 복원 대상 */
    public void markPaymentFailed() {
        if(this.status != OrderStatus.PAYMENT_PENDING) {
            throw new IllegalStateException("결제 실패 단계로 갈 수 없습니다.");
        }
        this.status = OrderStatus.PAYMENT_FAILED;
    }

    /** 취소(허용 상태에서만) */
    public void cancel() {

        if(this.status == OrderStatus.PAID) {
            throw new IllegalStateException("결제된 상품을 취소할 수 없습니다.");
        }
        if (this.status == OrderStatus.CANCELLED) {
            return; // 멱등 취소
        }
        this.status = OrderStatus.CANCELLED;
    }

    /** 아이템 합계를 반영할 때 사용할 증가 메서드 */
    public void increaseTotalAmount(BigDecimal delta) {
        if (delta == null || delta.signum() < 0) {
            throw new IllegalArgumentException("amount must be non-negative");
        }
        this.totalAmount = this.totalAmount.add(delta);
    }

    public void addItem(OrderItem item) {
        if (item == null) throw new IllegalArgumentException("item required");
        if (this.status != OrderStatus.CREATED) {
            throw new IllegalStateException("아이템 추가는 CREATED 상태에서만 가능합니다.");
        }
        item.setOrder(this);
//        this.items.add(item);
        increaseTotalAmount(item.getLineTotal());
    }


} // end class
