package org.shelter.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shelter.app.database.entity.Notification;
import org.shelter.app.database.entity.User;
import org.shelter.app.database.repository.AdoptionRequestRepository;
import org.shelter.app.database.repository.NotificationRepository;
import org.shelter.app.database.repository.UserRepository;
import org.shelter.app.dto.NotificationCreateDto;
import org.shelter.app.dto.NotificationReadDto;
import org.shelter.app.exception.BadNotificationMappingException;
import org.shelter.app.mapper.NotificationMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final UserRepository userRepository;
    private final AdoptionRequestRepository adoptionRequestRepository;

    @Transactional
    public void sendNotification(NotificationCreateDto notificationCreateDto) {

        Notification notification = Optional.of(notificationCreateDto)
                .map(dto -> {
                    Notification entity = notificationMapper.toEntity(notificationCreateDto);
                    entity.setUser(userRepository.findById(notificationCreateDto.getUserId()).orElse(null));
                    entity.setAdoptionRequest(adoptionRequestRepository.findById(dto.getAdoptionRequestId()).orElse(null));
                    entity.setMessage(notificationCreateDto.getMessage());
                    entity.setIsRead(Boolean.FALSE);
                    entity.setCreatedAt(LocalDateTime.now());
                    return entity;
                }).orElseThrow(() -> new BadNotificationMappingException("Bad notification mapping"));
        notificationRepository.save(notification);
        notificationMapper.toDto(notification);
    }

    public List<NotificationReadDto> notificationsForUser() {
        User user = userRepository.findByEmail(securityContext())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Notification> allByUser = notificationRepository.findAllByUser(user.getId());
        allByUser.stream()
                .filter(notification -> !notification.getIsRead())
                .forEach(notification -> notification.setIsRead(Boolean.TRUE));
        notificationRepository.saveAll(allByUser);

        return allByUser
                .stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList());
    }

    public String securityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
