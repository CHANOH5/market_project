package com.market.market.room.dto;

import com.market.market.room.entity.RoomStatus;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RoomRequestDTO {

    private Long roomType;

    private String name;

    private String description;

    private BigDecimal price;

    private Integer standardCapacity;

    private Integer maxCapacity;

    private BigDecimal extraPersonPrice;

    private RoomStatus status;

} // end class
