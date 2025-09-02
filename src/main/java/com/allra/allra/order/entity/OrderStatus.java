package com.allra.allra.order.entity;

public enum OrderStatus {

    CREATED,            // 주문 생성
    PAYMENT_PENDING,    // 결제 진행 중
    PAID,               // 결제 완료
    PAYMENT_FAILED,    // 주문 실패
    CANCELLED           // 주문 취소

} // end class
