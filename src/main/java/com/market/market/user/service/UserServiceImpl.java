package com.market.market.user.service;

import com.market.market.user.dto.UserRequestDTO;
import com.market.market.user.dto.UserResponseDTO;
import com.market.market.user.entity.User;
import com.market.market.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    /**
     * 사용자 등록
     */
    @Override
    @Transactional
    public void create(UserRequestDTO dto) {
//        String encodedPw = passwordEncoder.encode(dto.getPassword());
        User user = User.of(dto.getUserName(), dto.getPassword(), dto.getEmail());
        userRepository.save(user);
    } // create()


    /**
     * 사용자 목록 조회
     */
    // TODO: 페이징 처리
    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserResponseDTO::from);
//        return userRepository.findAll(pageable)
//                .stream()
//                .map(UserResponseDTO::from)
//                .toList();
    } // findAll()

    /**
     * 특정 사용자 상세 정보 조회
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO findById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found: " + userId));
        return UserResponseDTO.from(user);
    } // findById()

    /**
     * 사용자 정보 수정
     */
    // TODO: 비밀번호 변경
    @Override
    @Transactional
    public void update(Long userId, UserRequestDTO dto) {

        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // 사용자명 변경
        if(dto.getUserName() != null && !dto.getEmail().isBlank()) {
            user.changeUserName(dto.getUserName());
        } // if

        // 이메일 변경
        if(dto.getEmail() != null && !dto.getEmail().isBlank()) {
            if (userRepository.existsByEmailAndIdNot(dto.getEmail(), userId)) {
                throw new IllegalStateException("Email is already in use by another account.");
            } // inner-if
            user.changeEmail(dto.getEmail());
        } // if

    } // update()

    /**
     * 사용자 비활성화
     */
    @Override
    @Transactional
    public void withdraw(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        user.withdraw();
    } // withdraw()

} // end class
