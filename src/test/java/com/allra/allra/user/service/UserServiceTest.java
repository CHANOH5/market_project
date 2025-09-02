package com.allra.allra.user.service;

import com.allra.allra.user.dto.UserResponseDTO;
import com.allra.allra.user.entity.User;
import com.allra.allra.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void 사용자전체조회() {

        // given
        User user = User.builder()
                .userName("test")
                .email("test@naver.com")
                .password("test") // 테스트라면 중요 X
                .build();

        when(userRepository.findAll()).thenReturn(List.of(user));

        // when
        List<UserResponseDTO> foundUsers = userService.findAll();

        // then
        assertThat(foundUsers).hasSize(1);
        assertThat(foundUsers.get(0).getUserName()).isEqualTo("test");
        assertThat(foundUsers.get(0).getEmail()).isEqualTo("test@naver.com");
    }

} // end class