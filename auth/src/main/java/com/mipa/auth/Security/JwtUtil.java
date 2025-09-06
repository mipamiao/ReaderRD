package com.mipa.auth.Security;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {
    private static final Key SECRET = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long EXPIRATION_TIME = 86400000; // 1 day in milliseconds

    public static String generateToken(TokenInfo tokenInfo){
        return Jwts.builder()
                .setSubject(tokenInfo.getUserName())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .claim("role", tokenInfo.getUserRole())
                .claim("userId", tokenInfo.getUserId())
                .signWith(SECRET)
                .compact();
    }

    public static Claims parseToken(String Token){
        return Jwts.parserBuilder()
                .setSigningKey(SECRET)
                .build()
                .parseClaimsJws(Token)
                .getBody();
    }
}
