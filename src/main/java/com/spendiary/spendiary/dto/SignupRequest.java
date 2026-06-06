package com.spendiary.spendiary.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {
    private String loginId;
    private String password;
    private String passwordConfirm;
    private String name;
}

