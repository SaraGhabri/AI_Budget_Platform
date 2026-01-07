package com.example.apigateway.service;

import io.netty.handler.codec.http.HttpMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable) //csrf disabled using jwt
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable) //no basic auth
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable) //no form login
                .authorizeExchange(ex -> ex
                        .pathMatchers(String.valueOf(HttpMethod.OPTIONS), "/**").permitAll()
                        .anyExchange().permitAll() //custon filter handeling auth
                ) // permit all because i disabled all default filters
                .build();
    }
}