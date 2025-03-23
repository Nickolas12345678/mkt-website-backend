package com.nickolas.mktbackend.model;

import com.nickolas.mktbackend.domain.Role;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sellers")
@Data
@NoArgsConstructor
public class Seller {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    private String password;

    private boolean isEmailVerified = false;

    @Enumerated(EnumType.STRING)
    private Role role = Role.ROLE_SELLER;
}
