package com.market.market.reservation.service;

import com.market.market.reservation.dto.CreateReservationRequest;
import com.market.market.reservation.dto.ReservationResponseDTO;

import java.util.List;

public interface ReservationService {

    // 예약 생성
    void create(Long userId, CreateReservationRequest request);

    // 예약 취소 (관리자)

    // 예약 조회
    List<ReservationResponseDTO> findAll();

    // 예약 상세 조회

    // 예약 수정 (관리자)

} // end class
