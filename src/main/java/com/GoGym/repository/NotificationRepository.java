package com.GoGym.repository;

import com.GoGym.module.Notification;
import com.GoGym.module.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserAndStatus(User user, Notification.NotificationStatus status);
    Optional<Notification> findByMessage(String message);
    List<Notification> findByUserOrderByCreatedAtDesc(User user);


}
