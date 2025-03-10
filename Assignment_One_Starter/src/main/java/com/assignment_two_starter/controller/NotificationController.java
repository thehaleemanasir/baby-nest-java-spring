package com.assignment_two_starter.controller;

import com.assignment_two_starter.model.Notification;
import com.assignment_two_starter.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getNotifications(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Invalid request. User must be authenticated."
            ));
        }

        List<Notification> notifications = notificationService.getUnreadNotifications(userDetails.getUsername());

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", notifications.isEmpty() ? "No new notifications." : "Notifications retrieved.",
                "data", notifications.stream().map(notification -> Map.of(
                        "id", notification.getId(),
                        "message", notification.getMessage(),
                        "isRead", notification.isRead(),
                        "createdAt", notification.getCreatedAt()
                ))
        ));
    }

//    @PostMapping("/mark-read")
//    public ResponseEntity<Map<String, Object>> markNotificationsAsRead(@AuthenticationPrincipal UserDetails userDetails) {
//        if (userDetails == null) {
//            return ResponseEntity.badRequest().body(Map.of(
//                    "status", "error",
//                    "message", "Invalid request. User must be authenticated."
//            ));
//        }
//
//        try {
//            notificationService.markNotificationsAsRead(userDetails.getUsername());
//            return ResponseEntity.ok(Map.of(
//                    "status", "success",
//                    "message", "Notifications marked as read."
//            ));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
//                    "status", "error",
//                    "message", "An error occurred while marking notifications as read."
//            ));
//        }
//    }
}
