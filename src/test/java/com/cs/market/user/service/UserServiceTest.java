package com.cs.market.user.service;

import com.cs.market.user.dto.UserRequestDTO;
import com.cs.market.user.dto.UserResponseDTO;
import com.cs.market.user.entity.User;
import com.cs.market.user.fixture.UserFixtures;
import com.cs.market.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository);
    }
    @Test
    void 사용자생성() {
        // given
        UserRequestDTO dto = UserFixtures.aUserRequest();

        // when
        userService.create(dto);

        // then
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(captor.capture());

        User saved = captor.getValue();

        assertThat(saved.getUserName()).isEqualTo("test01");
        assertThat(saved.getEmail()).isEqualTo("test01@example.com");
    }

    @Test
    void 사용자전체조회() {

        // given
        User user = UserFixtures.aUser("test", "test@example.com");

        when(userRepository.findAll()).thenReturn(List.of(user));

        // when
        List<UserResponseDTO> foundUsers = userService.findAll();

        // then
        assertThat(foundUsers).hasSize(1);
        assertThat(foundUsers.get(0).getUserName()).isEqualTo("test");
        assertThat(foundUsers.get(0).getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void 사용자정보_업데이트_실패() {
        // given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        UserRequestDTO dto = UserFixtures.aUserRequest("test", "test@example.com");

        // when / then
        assertThatThrownBy(() -> userService.update(userId, dto))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("존재하지 않는 사용자");

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any());

    }

    @Test
    void 사용장정보_업데이트_성공() {
        //given
        Long userId = 1L;
        UserRequestDTO dto = UserFixtures.aUserRequest("test", "test@example.com");

//        User user = UserFixtures.aUser();
        User user = mock(User.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        //when
        userService.update(userId, dto);

        //then
        verify(userRepository).findById(userId);
        verify(user).update(dto);
        verify(userRepository, never()).save(any());

    }

    @Test
    void 회원탈퇴_성공() {

        // given
        Long userId = 1L;
        User user = mock(User.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        userService.withdraw(userId);

        // then
        verify(userRepository).findById(userId);
        verify(user).withdraw();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void 회원탈퇴_실패_존재하지않음() {
        // given
        Long userId = 1L;
        User user = mock(User.class);


        // when
        assertThatThrownBy(() -> userService.withdraw(userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 회원");

        // then
        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void 회원탈퇴_멱등성_이미탈퇴된사용자() {
        // given
        Long userId = 1L;
        User user = mock(User.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        userService.withdraw(userId);
        userService.withdraw(userId);

        // then
        verify(user, times(2)).withdraw();

    }

} // end class