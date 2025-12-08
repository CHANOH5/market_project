package com.market.market.room.dto;

import com.market.market.room.entity.Room;
import com.market.market.room.entity.RoomStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RoomResponseDTO {

    private Long id;
    private Long roomType;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer standardCapacity;
    private Integer maxCapacity;
    private BigDecimal extraPersonPrice;
    private RoomStatus status;

    private String updatedBy;
    private LocalDateTime updatedAt;

    //  EntityToDTO
    public static RoomResponseDTO from(Room room) {
        return RoomResponseDTO.builder()
                .id(room.getId())
                .name(room.getName())
                .description(room.getDescription())
                .price(room.getPrice())
                .standardCapacity(room.getStandardCapacity())
                .maxCapacity(room.getMaxCapacity())
                .extraPersonPrice(room.getExtraPersonPrice())
                .status(room.getStatus())
                .updatedBy(room.getUpdatedBy())
                .updatedAt(room.getUpdatedAt())
                .build();
    } // from()

} // end class
