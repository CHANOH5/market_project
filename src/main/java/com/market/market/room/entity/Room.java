package com.market.market.room.entity;

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
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(name = "room_type", nullable = false)
    private Long roomType;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "standard_capacity", nullable = false)
    private Integer standardCapacity;                   // 기준인원

    @Column(name = "max_capacity", nullable = false)
    private Integer maxCapacity;                        // 최대인원

    @Column(name = "extra_person_price", nullable = false)
    private BigDecimal extraPersonPrice;                // 추가 인원 1인당 요금

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RoomStatus status;

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

    private Room(String name, String description, BigDecimal price, Integer standardCapacity, Integer maxCapacity, BigDecimal extraPersonPrice) {
        this.roomType = 1L;
        this.name = name;
        this.description = description;
        this.price = price;
        this.standardCapacity = standardCapacity;
        this.maxCapacity = maxCapacity;
        this.extraPersonPrice = extraPersonPrice;
        this.status = RoomStatus.OPEN;
    } // constrcutor

    /**
     * 생성자
     */
    public static Room of(String name, String description, BigDecimal price, Integer standardCapacity, Integer maxCapacity, BigDecimal extraPersonPrice) {
        return new Room(name, description, price, standardCapacity, maxCapacity, extraPersonPrice);
    }

    // ======================= 정적 팩토리 메서드 (비즈니스 로직) - 생성과 검증 =====================

    public void updateInfo(Long roomType, String name, String description, BigDecimal price, Integer standardCapacity, Integer maxCapacity, BigDecimal extraPersonPrice, RoomStatus status) {

        if(standardCapacity > maxCapacity) {
            throw new IllegalArgumentException("기준 인원은 최대 인원보다 클 수 없습니다.");
        } // if
        if(price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("가격은 0보다 작을 수 없습니다.");
        }

        this.roomType = roomType;
        this.name = name;
        this.description = description;
        this.price = price;
        this.standardCapacity = standardCapacity;
        this.maxCapacity = maxCapacity;
        this.extraPersonPrice = extraPersonPrice;
        this.status = status;
    } // updateInfo()

    public void withdraw() {
        this.status = RoomStatus.CLOSE;
    } // withdraw()


    // TODO: 객실 추가/수정/삭제
    // ======================= 세터 =======================



} // end class
