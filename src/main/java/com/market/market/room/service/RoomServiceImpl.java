package com.market.market.room.service;

import com.market.market.reservation.entity.Reservation;
import com.market.market.reservation.repository.ReservationRepository;
import com.market.market.room.dto.AvailableRoomResponseDTO;
import com.market.market.room.dto.RoomMonthlyCalendarResponse;
import com.market.market.room.dto.RoomRequestDTO;
import com.market.market.room.dto.RoomResponseDTO;
import com.market.market.room.entity.Room;
import com.market.market.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;

    /**
     * 객실 정보 생성
     */
    @Override
    @Transactional
    public void create(RoomRequestDTO dto) {
        Room room = Room.of(dto.getName(),
                dto.getDescription(),
                dto.getPrice(),
                dto.getStandardCapacity(),
                dto.getMaxCapacity(),
                dto.getExtraPersonPrice()
        );
        roomRepository.save(room);
    } // create()

    /**
     * 객실 전체 정보 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<RoomResponseDTO> findAll() {
        return roomRepository.findAll()
                .stream()
                .map(RoomResponseDTO::from)
                .toList();
    }

    /**
     * 특정 객실 정보 조회
     */
    @Override
    public RoomResponseDTO findByRoomId(Long roomId) {

        Room room = roomRepository.findById(roomId).orElseThrow(
                () -> new IllegalArgumentException("room not found" + roomId)
        );

        return RoomResponseDTO.from(room);
    } // findByRoomId

    /**
     * 특정 객실 정보 업데이트
     */
    @Override
    @Transactional
    public void update(Long roomId, RoomRequestDTO dto) {

        Room room = roomRepository.findById(roomId).orElseThrow(
                () -> new IllegalArgumentException("room not found" + roomId)
        );

        room.updateInfo(
                dto.getRoomType(),
                dto.getName(),
                dto.getDescription(),
                dto.getPrice(),
                dto.getStandardCapacity(),
                dto.getMaxCapacity(),
                dto.getExtraPersonPrice(),
                dto.getStatus()
        );

    } // update

    /**
     * 특정 객실 정보 삭제
     */
    @Override
    @Transactional
    public void withdraw(Long roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow(
                () -> new IllegalArgumentException("room not found" + roomId)
        );
        room.withdraw();
    } // withdraw()

    @Override
    public List<AvailableRoomResponseDTO> findAvailableRooms(LocalDate chekIn, LocalDate checkOut, Integer guests) {

        // checkIn이 checkOut보다 나중이면 예외, guests가 <=0 이면 예외, checkIn이 오늘 이전이면 예외
        if(chekIn.isAfter(checkOut)) {
            throw new IllegalArgumentException("체크인은 체크아웃보다 이전 날짜여야 합니다.");
        }

        if(guests == null || guests <= 0) {
            throw new IllegalArgumentException("투숙 인원은 1명 이상이어야 합니다.");
        }

        if(chekIn.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("과거 날짜는 예약할 수 없습니다.");
        }

        // rooms 테이블에서 status가  OPEN인 애들만 가져오기
        // standard_capacity <= guests <= max_capacity인 객실만 가져오기
        roomRepository.findAllByStatusAndStandardCapacityLessThanEqualAndMaxCapacityGreaterThanEqual("OPEN", guests, guests);

        // reservation에서 ccheckIn ~ checkOut 기간과 겹치는 예약이 있는 roomId 목록 조회하여 CONFIRMED인 예약만 예약불가로 보고 제외
        // room_rates 테이블에서 각 객실에 대해 start_date <= 날짜 <= end_date에 해당하는 요금 정책을 가져와서 체크인~체크아웃까지 날짜별로 금액을 합산
        // totalPrice, nigths(숙박일수)를 계산하여 반환

        return List.of();
    }

    @Override
    public RoomMonthlyCalendarResponse getRoomMonthlyCalendar(Long roomId, Integer year, Integer month) {

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate endExclusive = start.plusMonths(1);

        Room room = roomRepository.findById(roomId).orElseThrow(() -> new IllegalStateException("room not found: " + roomId));

        List<Reservation> reservations = reservationRepository.findOverlappingInMonth(roomId, start, endExclusive);

        // 날짜별 예약 카운트 (하루라도 걸치면 그 날짜 카운트++)
        Map<LocalDate, Integer> countByDate = new HashMap<>();

        for (Reservation r : reservations) {
            LocalDate s = r.getCheckInDate().isBefore(start) ? start : r.getCheckInDate();
            LocalDate e = r.getCheckOutDate().isAfter(endExclusive) ? endExclusive : r.getCheckOutDate();

            for (LocalDate d = s; d.isBefore(e); d = d.plusDays(1)) {
                countByDate.merge(d, 1, Integer::sum);
            }
        }

        int length = start.lengthOfMonth();
        List<RoomMonthlyCalendarResponse.DayStatus> days = new ArrayList<>(length);

        for (int i = 0; i < length; i++) {
            LocalDate date = start.plusDays(i);
            int cnt = countByDate.getOrDefault(date, 0);
            days.add(new RoomMonthlyCalendarResponse.DayStatus(date, cnt > 0, cnt));
        }

        return new RoomMonthlyCalendarResponse(room.getId(), room.getName(), year, month, days);
    }

} // end class
