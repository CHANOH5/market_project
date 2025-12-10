package com.market.market.reservation.dto;

import com.market.market.reservation.entity.Reservation;
import com.market.market.reservation.entity.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ReservationResponseDTO {

    private Long reservationId;
    private Long roomId;
    private String roomName;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer guestCount;
    private String contactName;
    private String contactPhone;
    private String channel;
    private ReservationStatus status;
    private BigDecimal totalAmount;

    public static ReservationResponseDTO from(Reservation reservation) {
        return new ReservationResponseDTO(
                reservation.getId(),
                reservation.getRoom().getId(),
                reservation.getRoom().getName(),
                reservation.getCheckInDate(),
                reservation.getCheckOutDate(),
                reservation.getGuestCount(),
                reservation.getContactName(),
                reservation.getContactPhone(),
                reservation.getChannel(),
                reservation.getStatus(),
                reservation.getTotalAmount()
        );
    }
}
