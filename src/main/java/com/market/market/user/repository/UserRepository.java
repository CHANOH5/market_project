package com.market.market.user.repository;

import com.market.market.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    // 특정 이메일을 가진 다른 사용자가 존재하는지 여부 확인
    boolean existsByEmailAndIdNot(String email, Long id);

} // end class