package com.GoGym.repository;

import com.GoGym.module.User;
import com.GoGym.module.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
    Optional<UserBadge> findByUserAndBadge_Name(User user, String badgeName);
    List<UserBadge> findByUser(User user);
}
