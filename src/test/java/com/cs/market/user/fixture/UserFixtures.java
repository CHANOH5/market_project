package com.cs.market.user.fixture;

import com.cs.market.user.dto.UserRequestDTO;
import com.cs.market.user.entity.User;

import java.util.List;
import java.util.stream.IntStream;

public final class UserFixtures {

    private UserFixtures() {}

    public static final String DEFAULT_NAME = "test01";
    public static final String DEFAULT_PASSWORD = "1234";
    public static final String DEFAULT_EMAIL = "test01@example.com";
    public static final int DEFAULT_STATUS = 1;

    /** DTO 픽스처 */
    public static UserRequestDTO aUserRequest() {
        return UserRequestDTO.builder()
                .userName(DEFAULT_NAME)
                .password(DEFAULT_PASSWORD)
                .email(DEFAULT_EMAIL)
                .build();
    }

    public static UserRequestDTO aUserRequest(String name, String email) {
        return UserRequestDTO.builder()
                .userName(name)
                .password(DEFAULT_PASSWORD)
                .email(email)
                .build();
    }

    /** Entity 픽스처 */
    public static User aUser() {
        return User.of(DEFAULT_NAME, DEFAULT_PASSWORD, DEFAULT_EMAIL, DEFAULT_STATUS);
    }

    public static User aUser(String name, String email) {
        return User.of(name, DEFAULT_PASSWORD, email, DEFAULT_STATUS);
    }

    public static List<User> someUsers(int n) {
        return IntStream.rangeClosed(1, n)
                .mapToObj(i -> aUser("user" + i, "user" + i + "example.com"))
                .toList();
    }


} // end class
