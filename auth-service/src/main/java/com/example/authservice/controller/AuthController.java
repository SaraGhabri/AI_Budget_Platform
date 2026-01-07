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


    //login endpoint - genere token jwt
    @PostMapping("/api/auth/login")
    public LoginResponse login(@RequestBody AuthenticationRequest authenticationRequest) {
        try {
            //auth credentials via spring sec
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getUsername(),
                            authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid username or password");
        }

        //load user details de la base
        final UserDetails user = userDetailsService.loadUserByUsername(
                authenticationRequest.getUsername()
        );

        // récupérer entite user complete
        AppUser appUser = appUserRepository.findByUsername(authenticationRequest.getUsername());

        // genere le token avec username comme subject
        String token = jwtService.generateToken(user);

        // construire la réponse
        UserDto userDto = UserDto.builder()
                .id(String.valueOf(appUser.getId()))  // convert Integer to String
                .username(appUser.getUsername())
                .email(appUser.getMail())
                .role(appUser.getRole().name())  // single role, not list
                .build();

        return LoginResponse.builder()
                .token(token)
                .user(userDto)
                .build();
    }


    // ajouter un endpoint pour valider le token (utile pour Gateway)
    @GetMapping("/api/auth/validate")
    public ResponseEntity<?> validateToken(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        System.out.println("=== VALIDATE TOKEN CALLED ===");

        //verif si auth header existe
        if (authHeader == null || authHeader.trim().isEmpty()) {
            return ResponseEntity.ok(Map.of("valid", false, "message", "No Authorization header"));
        }

        //verif bearer format
        if (!authHeader.startsWith("Bearer ")) {
            return ResponseEntity.ok(Map.of("valid", false, "message", "Invalid Authorization format"));
        }

        String token = authHeader.substring(7).trim();

        try {
            //extract username du token
            String username = jwtService.getUsernameFromToken(token);
            System.out.println("Username extracted: " + username);


            //valider token signature et expiration
            if (username != null && jwtService.validateToken(token,
                    userDetailsService.loadUserByUsername(username))) {


                //retourne info user si valide
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