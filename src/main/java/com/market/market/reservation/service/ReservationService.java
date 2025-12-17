package com.market.market.reservation.service;

import com.market.market.reservation.dto.CreateReservationRequest;
import com.market.market.reservation.dto.ReservationResponseDTO;

import java.util.List;

public interface ReservationService {

    /**
     * 예약 생성 (기본 channel은 "DIRECT", 예약 시 status는 "CREATED" 상태로 변경)
     * @param userId 사용자ID
     * @param request 예약 정보
     */
    void create(Long userId, CreateReservationRequest request);

    /**
     * 예약 전체 조회
     */
    List<ReservationResponseDTO> findAll();

    /**
     * 예약 상세 정보를 조회합니다. (사용자 검증)
     * @param userId 사용자ID
     * @param reservationId 예약ID
     */
    // 예약 상세 조회
    ReservationResponseDTO findByUserId(Long userId, Long reservationId);

    // 예약 수정 (관리자)

    // 예약 취소 (관리자)

    /**
     * 예약 취소
     * @param userId 사용자ID
     * @param reservationId 예약ID
     */
    void withdraw(Long userId, Long reservationId);

} // end class
