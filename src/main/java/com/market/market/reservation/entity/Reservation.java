package com.market.market.reservation.entity;

import com.market.market.room.entity.Room;
import com.market.market.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")   // FK 컬럼 이름
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReservationStatus status;

    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;

    @Column(name = "check_out_date", nullable = false)
    private LocalDate checkOutDate;

    @Column(name = "guest_count", nullable = false)
    private Integer guestCount;

    @Column(name = "channel", nullable = false)
    private String channel; // DIRECT, NAVER, YANOLJA 등..

    @Column(name = "contact_name", nullable = false)
    private String contactName;

    @Column(name = "contact_phone", nullable = false)
    private String contactPhone;

    @Column(name = "memo", nullable = false)
    private String memo;

    @Column(name = "total_amount", nullable = false)
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

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        if (this.createdAt == null) {
            this.createdAt = now;
        }
        if (this.updatedAt == null) {
            this.updatedAt = now;
        }
        if (this.createdBy == null) {
            this.createdBy = "SYSTEM";   // 임시 값
        }
        if (this.updatedBy == null) {
            this.updatedBy = "SYSTEM";   // 임시 값
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
        if (this.updatedBy == null) {
            this.updatedBy = "SYSTEM";
        }
    }

    public Reservation(Long id, User user, Room room,
                       ReservationStatus status, LocalDate checkInDate, LocalDate checkOutDate,
                       Integer guestCount, String channel, String contactName, String contactPhone,
                       String memo, BigDecimal totalAmount) {
        this.id = id;
        this.user = user;
        this.room = room;
        this.status = status;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.guestCount = guestCount;
        this.channel = channel;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.memo = memo;
        this.totalAmount = totalAmount;
    } // constructor

   public static Reservation of(User user, Room room,
                                LocalDate checkInDate, LocalDate checkOutDate, Integer guestCount,
                                String channel, String contactName, String contactPhone,
                                String memo, BigDecimal totalAmount) {

       Reservation reservation = new Reservation();

       reservation.room = room;
       reservation.user = user;
       reservation.status = ReservationStatus.CREATED;
       reservation.checkInDate = checkInDate;
       reservation.checkOutDate = checkOutDate;
       reservation.guestCount = guestCount;
       reservation.channel = channel == null ? "DIRECT" : channel;
       reservation.contactName = contactName;
       reservation.contactPhone = contactPhone;
       reservation.memo = memo;
       reservation.totalAmount = totalAmount;

       return reservation;
   } // of

    // ============= 도메인 메서드 ==============

    /**
     * 예약 취소 메서드
     * @param today 체크인 날짜가 지난 예약은 취소 불가
     */
    public void cancel(LocalDate today) {
        if(this.status == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("이미 취소된 예약입니다.");
        }

        if(this.checkInDate.isBefore(today)) {
            throw new IllegalStateException("체크인 이후에는 예약을 취소할 수 없습니다.");
        }

        this.status = ReservationStatus.CANCELLED;
    }// cancel

    /**
     * 결제 완료 후 상태를 PIAD로 바꾸는 메서드
     */
    public void markPaid() {

        if(this.status != ReservationStatus.PAYMENT_PENDING) {
            throw new IllegalStateException("결제 진행중인 상태가 아닙니다.");
        }

        this.status = ReservationStatus.PAID;

    } // makePaid

    /**
     * 결제 실패 시 상태 변경
     */
    public void markPaymentFailed() {
        if (this.status != ReservationStatus.PAYMENT_PENDING) {
            throw new IllegalStateException("결제 진행 중인 예약만 결제 실패로 변경할 수 있습니다.");
        }
        this.status = ReservationStatus.PAYMENT_FAILED;
    }

    /**
     * 특정 예약과 겹치는지 확인
     * @param start
     * @param end
     * @return
     */
    public boolean isOverlapping(LocalDate start, LocalDate end) {
        return this.checkInDate.isBefore(end) && this.checkOutDate.isAfter(start);
    }

} // end class
