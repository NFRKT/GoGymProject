package com.GoGym.controller;

import com.GoGym.module.Notification;
import com.GoGym.module.User;
import com.GoGym.repository.NotificationRepository;
import com.GoGym.security.CustomUserDetails;
import com.GoGym.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/unread")
    public ResponseEntity<List<Map<String, String>>> getUnreadNotifications(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User loggedInUser = userDetails.getUser();

        List<Map<String, String>> notifications = notificationService.getUnreadNotifications(loggedInUser)
                .stream()
                .map(notification -> Map.of(
                        "id", String.valueOf(notification.getId()),
                        "message", notification.getMessage()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(notifications);
    }



    @PostMapping("/mark-read/{id}")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }


}
