package com.example.authservice.dto;

import lombok.Builder;

@Builder
public record UserDto(
        String id,
        String username,
        String email,
        String role
) {}