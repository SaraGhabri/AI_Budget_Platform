package com.example.authservice.services;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private long expiration;

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // add roles to JWT claims (custom claim)
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toList()));
        // add username to claims
        claims.put("sub", userDetails.getUsername());  // Standard JWT claim

        //build jwt
        return Jwts.builder()
                .setClaims(claims) //custom claim
                .setSubject(userDetails.getUsername()) //jwt subject claim
                .setIssuedAt(new Date()) //tken creation time
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey()) //HMAC-SHA256 signature
                .compact(); //serialize to string
    }

    // returns payload


    //extract claims from token
    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey()) //verify  sig with same key
                .build()
                .parseClaimsJws(token)//parse and validate it
                .getBody(); //returns payload
    }
    //extraction of username and expiration

    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimsFromToken(token).getExpiration();
    }

    //verification
    public Boolean validateToken(String token, UserDetails userDetails) {
        //check si username in token matches expected user + si il nest pas expire
        return getUsernameFromToken(token).equals(userDetails.getUsername())
                && getExpirationDateFromToken(token).after(new Date());
    }

    private Key getKey() {
        byte[] x = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(x);
    }
}
