package com.example.authservice.controller;

import com.example.authservice.dto.LoginResponse;
import com.example.authservice.dto.UserDto;
import com.example.authservice.entity.AppUser;
import com.example.authservice.repository.AppUserRepository;
import com.example.authservice.services.JwtService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;



@RestController
@AllArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final AppUserRepository appUserRepository;

    @PostMapping("/api/auth/login")
    public LoginResponse login(@RequestBody AuthenticationRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getUsername(),
                            authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid username or password");
        }

        final UserDetails user = userDetailsService.loadUserByUsername(
                authenticationRequest.getUsername()
        );

        // Récupérer l'utilisateur complet depuis la base
        AppUser appUser = appUserRepository.findByUsername(authenticationRequest.getUsername());

        // Générer le token avec username comme subject
        String token = jwtService.generateToken(user);

        // Construire la réponse
        UserDto userDto = UserDto.builder()
                .id(String.valueOf(appUser.getId()))  // Convert Integer to String
                .username(appUser.getUsername())
                .email(appUser.getMail())
                .role(appUser.getRole().name())  // Single role, not list
                .build();

        return LoginResponse.builder()
                .token(token)
                .user(userDto)
                .build();
    }


    // Ajouter un endpoint pour valider le token (utile pour Gateway)
    @GetMapping("/api/auth/validate")
    public ResponseEntity<?> validateToken(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        System.out.println("=== VALIDATE TOKEN CALLED ===");

        if (authHeader == null || authHeader.trim().isEmpty()) {
            return ResponseEntity.ok(Map.of("valid", false, "message", "No Authorization header"));
        }

        if (!authHeader.startsWith("Bearer ")) {
            return ResponseEntity.ok(Map.of("valid", false, "message", "Invalid Authorization format"));
        }

        String token = authHeader.substring(7).trim();

        try {
            String username = jwtService.getUsernameFromToken(token);
            System.out.println("Username extracted: " + username);

            if (username != null && jwtService.validateToken(token,
                    userDetailsService.loadUserByUsername(username))) {

                AppUser appUser = appUserRepository.findByUsername(username);
                return ResponseEntity.ok(Map.of(
                        "valid", true,
                        "username", username,
                        "role", appUser.getRole().name()  // Single role now
                ));
            }
        } catch (Exception e) {
            System.out.println("ERROR validating token: " + e.getMessage());
            return ResponseEntity.ok(Map.of("valid", false, "message", e.getMessage()));
        }

        return ResponseEntity.ok(Map.of("valid", false, "message", "Token validation failed"));
    }
}

@Data
class AuthenticationRequest {
    private String username;
    private String password;
}