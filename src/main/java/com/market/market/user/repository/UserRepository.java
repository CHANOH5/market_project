package com.market.market.user.repository;

import com.market.market.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 특정 이메일을 가진 다른 사용자가 존재하는지 여부 확인
    boolean existsByEmailAndIdNot(String email, Long id);

    // 이메일로 사용자 조회
    Optional<User> findByEmail(String email);

    // 이메일 중복 체크
    boolean existsByEmail(String email);

} // end class