package com.market.market.room.dto;

import java.time.LocalDate;
import java.util.List;

public record RoomMonthlyCalendarResponse(
    Long roomId,
    String roomName,
    int year,
    int month,
    List<DayStatus> days
) {
    public record DayStatus(
            LocalDate date,
            boolean reserved,
            int reservationCount
    ) {}
}
