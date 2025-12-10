package com.market.market.reservation.service;

import com.market.market.reservation.dto.CreateReservationRequest;
import com.market.market.reservation.dto.ReservationResponse;
import com.market.market.reservation.dto.ReservationResponseDTO;
import com.market.market.reservation.entity.Reservation;
import com.market.market.reservation.entity.ReservationStatus;
import com.market.market.reservation.repository.ReservationRepository;
import com.market.market.room.entity.Room;
import com.market.market.room.entity.RoomStatus;
import com.market.market.room.repository.RoomRepository;
import com.market.market.user.entity.User;
import com.market.market.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService{

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    @Transactional
    @Override
    public void create(Long userId, CreateReservationRequest request) {

        // 회원 조회
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new IllegalStateException("user not found: " + userId);
        });

        // 객실 조회
        Room room = roomRepository.findById(request.roomId()).orElseThrow(
                () -> new IllegalStateException("room not found: " + request.roomId())
        );

        if(!RoomStatus.OPEN.equals(room.getStatus())) {
            throw new IllegalStateException("예약할 수 없는 객실입니다.");
        } // if

        // 날짜/인원 검증
        if (request.guestCount() < room.getStandardCapacity() || request.guestCount() > room.getMaxCapacity()) {
            throw new IllegalStateException("이 객실은 " + room.getMaxCapacity() + "명을 초과할 수 없습니다.");
        } // if

        // 해당 RoomId에 대해 예약 겹침 여부 확인

        List<ReservationStatus> activeStatuses = Arrays.asList(
                ReservationStatus.CREATED,
                ReservationStatus.PAYMENT_PENDING,
                ReservationStatus.PAID
        );

        boolean overlapped = reservationRepository.existsOverlappingReservation(
                room,
                activeStatuses,
                request.checkInDate(),
                request.checkOutDate()
        );

        if (overlapped) {
            throw new IllegalStateException("이미 해당 날짜에 예약이 존재합니다.");
        }

        // 가격 계산
        BigDecimal totalAmount = calculateTotalAmount(room, request);

        // 예약 생성
        Reservation reservation = Reservation.of(
                user,
                room,
                request.checkInDate(),
                request.checkOutDate(),
                request.guestCount(),
                request.channel(),
                request.contactName(),
                request.contactPhone(),
                request.memo(),
                totalAmount
        );

        reservationRepository.save(reservation);

    } // create

    @Override
    @Transactional
    public List<ReservationResponseDTO> findAll() {

        return reservationRepository.findAll().stream().map(ReservationResponseDTO::from).toList();

    } // findAll

    private BigDecimal calculateTotalAmount(Room room, CreateReservationRequest request) {

        LocalDate checkIn = request.checkInDate();
        LocalDate checkOut = request.checkOutDate();

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (nights <= 0) {
            throw new IllegalArgumentException("숙박일수는 1일 이상이어야 합니다.");
        }

        // 1) 기준 인원 요금: room.price * 숙박일수
        BigDecimal basePerNight = room.getPrice();
        BigDecimal baseTotal = basePerNight.multiply(BigDecimal.valueOf(nights));

        // 2) 추가 인원 요금: max(0, (총 인원 - 기준 인원)) * extraPersonPrice * 숙박일수
        int extraPersons = request.guestCount() - room.getStandardCapacity();
        if (extraPersons < 0) {
            extraPersons = 0;
        }

        BigDecimal extraPerNight =
                room.getExtraPersonPrice().multiply(BigDecimal.valueOf(extraPersons));
        BigDecimal extraTotal = extraPerNight.multiply(BigDecimal.valueOf(nights));

        // 3) 총 금액 = 기준 인원 요금 + 추가 인원 요금
        return baseTotal.add(extraTotal);
    } // calculateTotalAmount

} // end class
