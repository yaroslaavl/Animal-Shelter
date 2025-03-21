package org.shelter.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shelter.app.database.entity.Notification;
import org.shelter.app.database.entity.User;
import org.shelter.app.database.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public List<Notification> notificationsForUser(Optional<User> userOptional) {
        User user = userOptional.orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository.findAllByUser(user);
    }

    @Transactional
    public void saveAll(List<Notification> notifications) {
        notificationRepository.saveAll(notifications);
    }
}
