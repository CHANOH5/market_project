package com.market.market.room.controller;

import com.market.market.room.dto.RoomRequestDTO;
import com.market.market.room.dto.RoomResponseDTO;
import com.market.market.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/room")
public class RoomController {

    private final RoomService roomService;

    /**
     * 객실 정보 생성
     */
    @PostMapping
    public ResponseEntity<Void> create(RoomRequestDTO dto) {
        roomService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    } // create

    /**
     * 객실 전체 정보 조회
     */
    @GetMapping
    public ResponseEntity<List<RoomResponseDTO>> findAll() {
        List<RoomResponseDTO> products = roomService.findAll();
        return ResponseEntity.ok(products);
    } // findAll()

    /**
     * 특정 객실 정보 조회
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<RoomResponseDTO> findById(@PathVariable Long roomId) {

        RoomResponseDTO byRoomId = roomService.findByRoomId(roomId);
        return ResponseEntity.ok(byRoomId);
    }

    /**
     * 특정 객실 정보 업데이트
     */
    @PutMapping("/{roomId}")
    public ResponseEntity<Void> updateRoom(@PathVariable Long roomId, @RequestBody RoomRequestDTO dto) {
        roomService.update(roomId, dto);
        return ResponseEntity.ok().build();
    }

    /**
     * 특정 객실 정보 삭제
     */
    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> withdrawRoom(@PathVariable Long roomId) {
        roomService.withdraw(roomId);
        return ResponseEntity.noContent().build();
    }

    // 특정 날짜기준 객실 예약 가능여부 조회, Room 검색(날짜+인원순), 월별 객실 예약 현황 조회 -> 공통적으로 예약정보와 같이 비교해서 봐야함


} // end class
