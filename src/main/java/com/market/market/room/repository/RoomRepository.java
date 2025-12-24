package com.market.market.room.repository;

import com.market.market.reservation.entity.Reservation;
import com.market.market.room.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findAllByStatusAndStandardCapacityLessThanEqualAndMaxCapacityGreaterThanEqual(
            String status,
            Integer guestsForStandard,
            Integer guestsForMax
    );


} // end class