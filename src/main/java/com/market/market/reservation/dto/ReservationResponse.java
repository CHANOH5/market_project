package com.market.market.reservation.dto;

import com.market.market.reservation.entity.ReservationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReservationResponse(
        Long reservationId,
        Long roomId,
        String roomName,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        Integer guestCount,
        String contactName,
        String contactPhone,
        String channel,
        ReservationStatus status,
        BigDecimal totalAmount
) {
}
