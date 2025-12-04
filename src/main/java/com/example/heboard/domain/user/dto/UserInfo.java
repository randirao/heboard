package com.example.heboard.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfo {

    private Long userId;
    private String nickname;
    private String email;
}