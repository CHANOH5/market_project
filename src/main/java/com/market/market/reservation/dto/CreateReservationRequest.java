package com.market.market.reservation.dto;

import java.time.LocalDate;

public record CreateReservationRequest(
        Long roomId,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        Integer guestCount,
        String channel,
        String contactName,
        String contactPhone,
        String memo
) {
}
