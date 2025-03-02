package com.GoGym.controller;

import com.GoGym.module.Notification;
import com.GoGym.module.User;
import com.GoGym.security.CustomUserDetails;
import com.GoGym.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Map<String, String>>> getAllNotifications(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User loggedInUser = userDetails.getUser();

        List<Map<String, String>> notifications = notificationService.getAllNotifications(loggedInUser)
                .stream()
                .map(notification -> Map.of(
                        "id", String.valueOf(notification.getId()),
                        "message", notification.getMessage(),
                        "status", notification.getStatus().name(),
                        "createdAt", String.valueOf(notification.getCreatedAt().getTime()),
                        "userId", String.valueOf(loggedInUser.getIdUser())
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(notifications);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/mark-read/{id}")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }
}
