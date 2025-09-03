package com.cs.market.user.service;

import com.cs.market.user.dto.UserResponseDTO;
import com.cs.market.user.entity.User;
import com.cs.market.user.repository.UserRepository;
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
        User user = User.of("test", "test", "test@naver.com", 1);

        when(userRepository.findAll()).thenReturn(List.of(user));

        // when
        List<UserResponseDTO> foundUsers = userService.findAll();

        // then
        assertThat(foundUsers).hasSize(1);
        assertThat(foundUsers.get(0).getUserName()).isEqualTo("test");
        assertThat(foundUsers.get(0).getEmail()).isEqualTo("test@naver.com");
    }

} // end class