package com.nickolas.mktbackend.config;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

public class JwtConstant {
    public static final String SECRET_KEY = new String(Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded());
    public static final String JWT_HEADER = "Authorization";
}
