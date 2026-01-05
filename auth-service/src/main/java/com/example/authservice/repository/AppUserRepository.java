package com.example.authservice.repository;

import com.example.authservice.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Integer> {  // Change from String to Integer
    AppUser findByUsername(String username);

    boolean existsByUsername(String user);
}
