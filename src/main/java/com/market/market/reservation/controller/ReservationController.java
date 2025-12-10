package com.market.market.reservation.controller;

import com.market.market.global.security.CustomUserDetails;
import com.market.market.reservation.dto.CreateReservationRequest;
import com.market.market.reservation.dto.ReservationResponseDTO;
import com.market.market.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * 예약 정보 생성
     */
    @PostMapping()
    public ResponseEntity<Void> create(@RequestBody CreateReservationRequest request) {
        Long userId = getCurrentUserIdFromSecurity();
        reservationService.create(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    } // create

    /**
     * 예약정보 조회
     */
    @GetMapping
    public ResponseEntity<List<ReservationResponseDTO>> findAll() {
        List<ReservationResponseDTO> reservations = reservationService.findAll();
        return ResponseEntity.ok(reservations);
    } // findAll

    private Long getCurrentUserIdFromSecurity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        return principal.getId();
    }

} // end class
