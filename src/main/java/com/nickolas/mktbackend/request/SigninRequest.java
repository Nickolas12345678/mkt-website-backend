package com.nickolas.mktbackend.request;

import lombok.Data;

@Data
public class SigninRequest {
    private String email;
    private String password;
    private String otp;
}
