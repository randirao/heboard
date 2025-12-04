package com.example.heboard.domain.auth.dto;

import com.example.heboard.domain.user.dto.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private TokenInfo token;
    private UserInfo user;
}
