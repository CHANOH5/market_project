package com.allra.allra.user.service;

import com.allra.allra.user.dto.UserRequestDTO;
import com.allra.allra.user.dto.UserResponseDTO;

import java.util.List;

public interface UserService {

    /**
     * 새로운 사용자를 생성합니다.
     * @param dto 생성할 사용자의 정보를 담은 DTO (Data Transfer Object)
     */
    void create(UserRequestDTO dto);

    /**
     * 등록된 모든 사용자를 조회합니다.
     * @return 모든 사용자 정보 DTO가 담긴 리스트
     */
    List<UserResponseDTO> findAll();

    /**
     * 특정 사용자의 정보를 수정합니다.
     * @param userId 수정할 사용자의 ID
     * @param dto    수정할 사용자의 새로운 정보를 담은 DTO
     */
    void update(Long userId, UserRequestDTO dto);

    /**
     * 특정 사용자를 탈퇴 처리합니다. (비활성화)
     * @param userId 비활성화할 사용자의 ID
     */
    void withdraw(Long userId);

} // end class
