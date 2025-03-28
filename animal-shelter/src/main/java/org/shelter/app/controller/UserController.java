package org.shelter.app.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shelter.app.database.repository.UserRepository;
import org.shelter.app.dto.*;
import org.shelter.app.service.MinioService;
import org.shelter.app.service.UserService;
import org.shelter.app.validation.CreateAction;
import org.shelter.app.validation.EditAction;
import org.shelter.app.validation.ImageAction;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final RedisTemplate<String , String> redisTemplate;
    private final UserService userService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final MinioService minioService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        String token = UUID.randomUUID().toString();

        String oldToken = redisTemplate.opsForValue().get("user:" + loginDto.getEmail() + ":api_key");
        if (oldToken != null) {
            redisTemplate.delete("x-api-key:" + oldToken);
        }

        redisTemplate.opsForValue().set("x-api-key:" + token, loginDto.getEmail() + ":" + token,1, TimeUnit.DAYS);
        redisTemplate.opsForValue().set("user:" + loginDto.getEmail() + ":api_key", token, 1, TimeUnit.DAYS);

        log.info("API-KEY: '{}' for user '{}'", token, loginDto.getEmail());
        return ResponseEntity.ok(Map.of("X-API-KEY", token));
    }

    @PostMapping("/create")
    public ResponseEntity<UserReadDto> createNewUser(@RequestBody @Validated(CreateAction.class) UserCreateDto userCreateDto) {
        if (userRepository.existsByEmail(userCreateDto.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        log.info("User created with email: '{}'", userCreateDto.getEmail());
        return ResponseEntity.ok(userService.create(userCreateDto));
    }

    @PostMapping("/activate")
    public ResponseEntity<String> activateAccount(@RequestParam String token) {
        userService.activation(token);
        return ResponseEntity.ok("Activated");
    }

    @DeleteMapping("/delete-account")
    public ResponseEntity<String> deleteUser() {
        try {
            userService.delete();

            return ResponseEntity.ok("Deleted");
        } catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/resend-activation")
    public ResponseEntity<String> resendActivation() {
        boolean isResent = userService.resendActivationCode();

        if (isResent) {
            return ResponseEntity.ok("Activation code resent");
        }
        return ResponseEntity.ok("Activation code did not resend");
    }

    @PutMapping("/update")
    public ResponseEntity<UserReadDto> updateUserData(@RequestBody @Validated(EditAction.class) UserUpdateDto userUpdateDto) {
        return ResponseEntity.ok(userService.updateUserData(userUpdateDto));
    }

    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody @Validated(EditAction.class) UserResetPasswordDto userResetPasswordDto) {
        boolean isChanged = userService.resetPassword(userResetPasswordDto);

        if (isChanged) {
            return ResponseEntity.ok("Password changed successfully");
        }
        return ResponseEntity.ok("Password changed has failed");
    }

    @PutMapping("/set-vetRole")
    public ResponseEntity<String> vetAssigment(@RequestBody VetRoleAssignmentDto vetRoleAssignmentDto) {
        userService.vetAssigment(vetRoleAssignmentDto);

        return ResponseEntity.ok("User with id: " + vetRoleAssignmentDto.getUserId() + " got a vet role");
    }

    @PutMapping("/upload-image")
    public ResponseEntity<String> uploadUserAvatar(@ModelAttribute @Validated(ImageAction.class) ImageUploadDto imageUploadDto) {
        try {
            minioService.uploadImage(imageUploadDto.getFile(), null);
            return ResponseEntity.ok("Image uploaded successfully");
        } catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Upload image failed");
        }
    }

    @GetMapping("/image")
    public ResponseEntity<String> getUserAvatar(@RequestParam("email") String email) {
        return ResponseEntity.ok(minioService.getUserAvatar(email, Boolean.FALSE));
    }

    @GetMapping("find/{userId}")
    public ResponseEntity<UserReadDto> findUserById(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(userService.findById(userId));
    }
}
