package org.shelter.app.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shelter.app.dto.NotificationReadDto;
import org.shelter.app.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/all-by-user")
    public ResponseEntity<List<NotificationReadDto>> getAllByUser() {
        return ResponseEntity.ok(notificationService.notificationsForUser());
    }
}
