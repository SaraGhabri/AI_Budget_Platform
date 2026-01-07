package com.example.apigateway.service;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.List;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    //validates token signature and expiration
    public void validateToken(final String token) {
        Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token);
    }


    //extract username from token "subject" claim
    public String extractUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();  // Le username est dans le "subject"
    }


    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    //xtract roles from custom "roles" claim
    public List<String> extractRoles(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        Object rolesObj = claims.get("roles"); //custom claim
        System.out.println("DEBUG - Roles in token: " + rolesObj);

        if (rolesObj instanceof List<?> list) {
            List<String> roles = list.stream().map(String::valueOf).toList();
            System.out.println("DEBUG - Extracted roles: " + roles);
            return roles;
        }
        return List.of();
    }

}