package com.example.heboard.global.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtUserPrincipal {
    private final Long userId;
    private final String nickname;
}
