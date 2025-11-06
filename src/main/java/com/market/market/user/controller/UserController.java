package com.market.market.user.controller;

import com.market.market.user.dto.UserRequestDTO;
import com.market.market.user.dto.UserResponseDTO;
import com.market.market.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    /**
     * 신규 사용자 생성 API
     */
    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody UserRequestDTO requestDTO) {
        userService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    } // createUser

    /**
     * 모든 사용자 조회 API
     */
    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(@PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<UserResponseDTO> users = userService.findAll(pageable);
        return ResponseEntity.ok(users);
    } // getAllUsers

    /**
     * 특정 사용자 정보 수정 API
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable Long id, @RequestBody UserRequestDTO requestDTO) {
        userService.update(id, requestDTO);
        return ResponseEntity.ok().build();
    } // updateUser

    /**
     * 특정 사용자 탈퇴 처리 API
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> withdrawUser(@PathVariable Long id) {
        userService.withdraw(id);
        return ResponseEntity.noContent().build();
    } // withdrawUser

} // end class
