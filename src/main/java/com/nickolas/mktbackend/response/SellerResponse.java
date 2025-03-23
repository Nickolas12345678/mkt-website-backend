package com.nickolas.mktbackend.response;

import com.nickolas.mktbackend.domain.Role;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class SellerResponse {
    private String name;
    private Role role;
    private String jwt;

    public SellerResponse(String name,   Role role, String jwt) {
        this.name = name;
        this.role = role;
        this.jwt = jwt;
    }
}
