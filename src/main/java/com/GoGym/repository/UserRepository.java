package com.GoGym.repository;

import com.GoGym.module.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.userType = com.GoGym.module.User.UserType.TRENER")
    List<User> findAllTrainers();

}
