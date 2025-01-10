package com.GoGym.repository;

import com.GoGym.Module.user;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface userRepository extends JpaRepository<user, Long> {
    Optional<user> findByEmail(String email);
}
