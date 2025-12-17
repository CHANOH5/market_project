package com.market.market.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserRequestDTO {

    private String loginId;
    private String userName;
    private String password;
    private String email;

    private String phone;

    @Builder
    public UserRequestDTO(String loginId, String userName, String password, String email, String phone) {
        this.loginId = loginId;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.phone = phone;
    }

} // end class
