package com.GoGym.repository;

import com.GoGym.Module.user;
import org.springframework.data.jpa.repository.JpaRepository;

public interface userRepository extends JpaRepository<user, Long> {
    user findByEmail(String email);
}
