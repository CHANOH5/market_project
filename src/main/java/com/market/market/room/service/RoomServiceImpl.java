package com.market.market.room.service;

import com.market.market.room.dto.RoomRequestDTO;
import com.market.market.room.dto.RoomResponseDTO;
import com.market.market.room.entity.Room;
import com.market.market.room.repository.RoomRepository;
import com.market.market.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

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

} // end class
