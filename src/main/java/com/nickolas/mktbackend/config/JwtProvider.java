package com.nickolas.mktbackend.config;

import com.nickolas.mktbackend.model.User;
import com.nickolas.mktbackend.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtProvider {
    private final SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());


    public String generateToken(Authentication auth) {
        Set<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        System.out.println("Generating token for: " + auth.getName() + " with roles: " + roles);

        return Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .claim("email", auth.getName())
                .claim("roles", String.join(",", roles))
                .signWith(key)
                .compact();
    }


    public String getEmailFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token.replace("Bearer ", ""))
                    .getBody();
            return claims.get("email", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Long getUserIdFromToken(String token, UserRepository userRepository) {
        String email = getEmailFromToken(token);
        if (email != null) {
            User user = userRepository.findByEmail(email).orElse(null);
            return user != null ? user.getId() : null;
        }
        return null;
    }
}
