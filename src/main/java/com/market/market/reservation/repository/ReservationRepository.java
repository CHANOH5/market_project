package com.market.market.reservation.repository;

import com.market.market.reservation.entity.Reservation;
import com.market.market.reservation.entity.ReservationStatus;
import com.market.market.room.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("""
        SELECT CASE WHEN count(r) > 0 THEN true ELSE false END
        FROM Reservation r
        WHERE r.room = :room
          and r.status in :statuses
          and r.checkInDate < :checkOut
          and r.checkOutDate > :checkIn
        """)
    boolean existsOverlappingReservation(
            @Param("room") Room room,
            @Param("statuses") List<ReservationStatus> statuses,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );

    Optional<Reservation> findByIdAndUser_Id(Long reservationId, Long userId);

} // end class