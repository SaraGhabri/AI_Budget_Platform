package com.example.authservice.dto;

import lombok.Builder;

@Builder
public record LoginResponse(
        String token,
        UserDto user
) {}
