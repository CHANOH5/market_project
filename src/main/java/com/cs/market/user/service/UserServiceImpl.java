package com.cs.market.user.service;

import com.cs.market.user.dto.UserRequestDTO;
import com.cs.market.user.dto.UserResponseDTO;
import com.cs.market.user.entity.User;
import com.cs.market.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    } // constructor

    @Override
    @Transactional
    public void create(UserRequestDTO dto) {
        userRepository.save(User.from(dto));
    } // create

    //
    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> findAll() {
        return userRepository.findAll()
                .stream()
                .map(UserResponseDTO::from)
                .collect(Collectors.toList());
    } // findAll()

    @Override
    @Transactional
    public void update(Long userId, UserRequestDTO dto) {

        // 엔티티 내에 인스턴스 메서드를 만드는 것이 올바른 방법
        // 즉 db에서 가져온 user 객체를 대상으로 상태를 변경하도록 해야함, create는 새로운 객체를 만드는거니까 from을 쓴거

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 사용자입니다."));

        user.update(dto);

        // save할 필요 없이 트랜잭션이 종료되면 변경 감지(Dirty Checking)에 의해 자동으로 UPDATE 쿼리가 실행

    } // update

    @Override
    @Transactional
    public void withdraw(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다"));
        user.withdraw();
    } // delete


} // end class
