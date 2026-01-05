package com.example.authservice;

import com.example.authservice.entity.AppUser;
import com.example.authservice.entity.Role;
import com.example.authservice.repository.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableDiscoveryClient
public class AuthServiceApplication {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    //@Bean
    CommandLineRunner runner(AppUserRepository userRepository,
                             PasswordEncoder passwordEncoder) {
        return args -> {

            if (!userRepository.existsByUsername("admin")) {
                userRepository.save(
                        AppUser.builder()
                                .username("admin")
                                .mail("admin@gmail.com")
                                .password(passwordEncoder.encode("1234"))
                                .role(Role.ADMIN)
                                .build()
                );
            }

            if (!userRepository.existsByUsername("user")) {
                userRepository.save(
                        AppUser.builder()
                                .username("user")
                                .mail("user@gmail.com")
                                .password(passwordEncoder.encode("1234"))
                                .role(Role.USER)
                                .build()
                );
            }
           };
       }
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }

}
