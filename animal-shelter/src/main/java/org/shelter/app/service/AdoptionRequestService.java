package org.shelter.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shelter.app.database.entity.AdoptionRequest;
import org.shelter.app.database.entity.Pet;
import org.shelter.app.database.entity.User;
import org.shelter.app.database.entity.enums.AdoptionStatus;
import org.shelter.app.database.entity.enums.PetStatus;
import org.shelter.app.database.entity.enums.Role;
import org.shelter.app.database.repository.AdoptionRequestRepository;
import org.shelter.app.database.repository.PetRepository;
import org.shelter.app.database.repository.UserRepository;
import org.shelter.app.dto.AdoptionRequestCreateEditDto;
import org.shelter.app.dto.AdoptionRequestReadDto;
import org.shelter.app.dto.NotificationCreateDto;
import org.shelter.app.exception.*;
import org.shelter.app.mapper.AdoptionRequestMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdoptionRequestService {

    private final AdoptionRequestMapper adoptionRequestMapper;
    private final AdoptionRequestRepository adoptionRequestRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final PetRepository petRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public AdoptionRequestReadDto sendRequest(AdoptionRequestCreateEditDto adoptionRequestCreateEditDto) {
        User user = userRepository.findByEmail(securityContext())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getRole() == Role.USER) {
            throw new UserRoleException("User has USER role");
        }

        if (adoptionRequestRepository.findByUserId(user.getId()).isPresent()) {
            throw new AdoptionRequestInProgressException("Adoption request already exists. Wait until adoption request finished");
        }

        Pet pet = petRepository.findPetById(adoptionRequestCreateEditDto.getPetId())
                .orElseThrow(() -> new PetNotFoundException("Pet not found"));

        if (!pet.getStatus().equals(PetStatus.AVAILABLE)) {
            throw new PetFailedStatusException("Pet has invalid status " + pet.getStatus());
        }

        AdoptionRequest adoptionRequest = AdoptionRequest.builder()
                .user(user)
                .pet(pet)
                .adoptionStatus(AdoptionStatus.IN_PROGRESS)
                .requestDate(LocalDateTime.now())
                .build();
        adoptionRequestRepository.saveAndFlush(adoptionRequest);
        notificationService.sendNotification(new NotificationCreateDto(
                user.getId(),
                adoptionRequest.getId(),
                "You have sent a request to adopt a pet with name: " + pet.getName()));

        redisTemplate.opsForValue().set(user.getEmail() + ":adoption:" + adoptionRequest.getId(), adoptionRequest.getId().toString(), 7, TimeUnit.DAYS);
        log.info("Adoption request: {}", adoptionRequest);
        return adoptionRequestMapper.toDto(adoptionRequest);
    }

    @Transactional
    public boolean respondAdoptionRequest(Long adoptionRequestId, Boolean isAccepted) {
        return true;
    }

    public String securityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
