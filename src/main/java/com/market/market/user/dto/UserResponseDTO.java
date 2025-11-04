package com.market.market.user.dto;

import com.market.market.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class UserResponseDTO {

    private Long id;
    private String userName;
    private String email;

    @Builder
    public UserResponseDTO(Long id, String userName, String email) {
        this.id = id;
        this.userName = userName;
        this.email = email;
    } // constructor

    // Entity to DTO (매핑과 필터링)
    public static UserResponseDTO from(User entity) {
        return new UserResponseDTO(entity.getId(), entity.getUserName(), entity.getEmail());
    }

} // end class
