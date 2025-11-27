package com.market.market.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserRequestDTO {

    private String userName;
    private String password;
    private String email;

    @Builder
    public UserRequestDTO(String userName, String password, String email) {
        this.userName = userName;
        this.password = password;
        this.email = email;
    }

} // end class
