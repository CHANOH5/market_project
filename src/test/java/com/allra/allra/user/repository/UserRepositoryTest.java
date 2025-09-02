package com.allra.allra.user.repository;

import com.allra.allra.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("사용자 저장 및 조회")
    void 사용자저장및조회() {
        // given
        User user = User.builder()
                .userName("test123")
                .password("1234")
                .email("test@naver.com")
                .build();
        // when
        User savedUser = userRepository.save(user);
        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        // then

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUserName()).isEqualTo("test123");
    }



} // end class