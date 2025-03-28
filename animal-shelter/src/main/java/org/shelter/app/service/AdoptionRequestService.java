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
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

        if(user.getBirthDate() == null || user.getAddress() == null || user.getPhone() == null || user.getProfilePicture() == null) {
            throw new UserFieldException("User has not filled in all the required fields");
        }

        if (adoptionRequestRepository.findByUserIdWhenStatusInProgress(user.getId()).isPresent()) {
            throw new AdoptionRequestInProgressException("Adoption request already exists. Wait for a respond");
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
    public void respondAdoptionRequest(UUID adoptionRequestId, Boolean isAccepted) {
        AdoptionRequest adoptionRequest = adoptionRequestRepository.findById(adoptionRequestId)
                .orElseThrow(() -> new AdoptionRequestNotFoundException("Adoption request not found"));

        String adoptionToken = redisTemplate.opsForValue().get(adoptionRequest.getUser().getEmail() + ":adoption:" + adoptionRequest.getId());

        if (adoptionToken == null || adoptionToken.isEmpty()) {
            throw new TokenException("Token expired");
        }

        Pet pet = petRepository.findPetById(adoptionRequest.getPet().getId())
                .orElseThrow(() -> new PetNotFoundException("Pet not found"));

        if (!pet.getStatus().equals(PetStatus.AVAILABLE)) {
            throw new PetFailedStatusException("Pet has invalid status " + pet.getStatus());
        }

        if (isAccepted) {
            pet.setStatus(PetStatus.NOT_AVAILABLE);
            adoptionRequest.setAdoptionStatus(AdoptionStatus.APPROVED);

            notificationService.sendNotification(new NotificationCreateDto(
                    adoptionRequest.getUser().getId(),
                    adoptionRequest.getId(),
                    "Your request has been approved! We look forward to seeing you at our shelter. Name of the animal: " + pet.getName()));

            petRepository.save(pet);
            adoptionRequestRepository.save(adoptionRequest);
        } else {
            adoptionRequest.setAdoptionStatus(AdoptionStatus.REJECTED);

            notificationService.sendNotification(new NotificationCreateDto(
                    adoptionRequest.getUser().getId(),
                    adoptionRequest.getId(),
                    "Your request has been rejected. Name of the animal: " + pet.getName()));

            adoptionRequestRepository.save(adoptionRequest);
        }
    }

    public List<AdoptionRequestReadDto> findAllByUserId() {
        User user = userRepository.findByEmail(securityContext())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return adoptionRequestRepository.findAllByUserId(user.getId())
                .stream()
                .map(adoptionRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    public AdoptionRequestReadDto findById(UUID adoptionRequestId) {
        AdoptionRequest adoptionRequest = adoptionRequestRepository.findById(adoptionRequestId)
                .orElseThrow(() -> new AdoptionRequestNotFoundException("Adoption request not found"));

        return adoptionRequestMapper.toDto(adoptionRequest);
    }

    public String securityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
