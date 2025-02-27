package com.GoGym.repository;

import com.GoGym.module.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findAllByUserType(User.UserType userType);

    Optional<User> findByUserType(User.UserType userType);
    boolean existsById(Long id);
}
