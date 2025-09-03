package com.cs.market.user.dto;

import com.cs.market.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserRequestDTO {

    private String userName;
    private String password;
    private String email;

    public UserRequestDTO(String userName, String password, String email) {
        this.userName = userName;
        this.password = password;
        this.email = email;
    }

} // end class
