package com.market.market.room.service;

import com.market.market.room.dto.AvailableRoomResponseDTO;
import com.market.market.room.dto.RoomRequestDTO;
import com.market.market.room.dto.RoomResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface RoomService {

    /**
     *
     * @param dto
     */
    void create(RoomRequestDTO dto);
    /**
     * 객실 전체 조회
     * TODO: 페이징 처리, 조회 조건 설정
     */
   List<RoomResponseDTO> findAll();

    /**
     *
     * @param roomId
     * @return
     */
   RoomResponseDTO findByRoomId(Long roomId);

    /**
     *
     * @param roomId
     * @param dto
     */
   void update(Long roomId, RoomRequestDTO dto);

    /**
     *
     * @param roomId
     */
   void withdraw(Long roomId);

    /**
     * 날짜+인원수 기준으로 예약 가능한 객실 목록 조회
     * @param chekIn
     * @param checkOut
     * @param guests
     * @return
     */
   List<AvailableRoomResponseDTO> findAvailableRooms(LocalDate chekIn, LocalDate checkOut, Integer guests);

   // ::TODO 월별 객실 예약 현황 조회, 날짜/인원으로 예약 가능한 Room 리스트 조회, Room 이미지 업로드/삭제

} // end class
