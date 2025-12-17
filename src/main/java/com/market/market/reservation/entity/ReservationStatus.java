package com.market.market.reservation.entity;

public enum ReservationStatus {

    CREATED, // 결제 전 예약 레코드만 생성된 상태
    PAYMENT_PENDING, // 결제 시도 중 (PG창 열려있거나, 승인 대기 중)
    PAID, // 결제까지 정상적으로 끝나서 예약 확정된 상태
    PAYMENT_FAILED, // 결제 실패 상태(승인 거절, 오류 등)
    CANCELLED // 사용자가 취소했거나, 관리자가 취소 처리한 상태

} // end class
