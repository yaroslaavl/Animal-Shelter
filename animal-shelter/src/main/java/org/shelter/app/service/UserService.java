package org.shelter.app.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.shelter.app.dto.UserCreateDto;
import org.shelter.app.dto.UserUpdateDto;
import org.shelter.app.exception.TokenException;
import org.shelter.app.exception.UserAccountAlreadyActivated;
import org.shelter.app.exception.UserNotFoundException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.shelter.app.database.entity.enums.Role;
import org.shelter.app.database.entity.User;
import org.shelter.app.database.repository.UserRepository;
import org.shelter.app.dto.UserReadDto;
import org.shelter.app.mapper.UserMapper;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final MailService mailService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;

    @SneakyThrows
    @Transactional
    public UserReadDto create(UserCreateDto userDto) {
        String activationToken = UUID.randomUUID().toString();
        UserReadDto userReadDto = Optional.of(userDto)
                .map(dto -> {
                    User user = userMapper.toEntity(dto,passwordEncoder);
                    user.setRole(Role.USER);
                    user.setEmailVerificationToken(activationToken);
                    user.setEmailVerified(false);
                    user.setCreatedAt(LocalDateTime.now());
                    usersOfApp(userDto);
                    return userRepository.saveAndFlush(user);
                })
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!StringUtils.isEmpty(userDto.getEmail())) {
            String message = String.format(
                    "Hello, %s! \n" +
                            "Welcome to our family. Please, visit the following link to activate your account:  http://localhost:8080/api/user/activate?token=%s",
                    userDto.getEmail(),
                    activationToken
            );
            mailService.send(userDto.getEmail(), "Activation code", message);
            redisTemplate.opsForValue().set(userDto.getEmail() + ":activationToken", activationToken, 1, TimeUnit.DAYS);
        }
        return userReadDto;
    }

    @SneakyThrows
    @Transactional
    public boolean resendActivationCode(){
        Optional<User> optionalUser = userRepository.findByEmail(securityContext());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            String activationToken = UUID.randomUUID().toString();

            if (user.getEmailVerified()) {
                throw new UserAccountAlreadyActivated("User account already activated");
            }

            user.setEmailVerificationToken(activationToken);
            userRepository.saveAndFlush(user);
            String confirmationLink = "http://localhost:8080/api/user/activate?token=" + activationToken;

            if (!StringUtils.isEmpty(user.getEmail())) {
                String message = String.format(
                        "You have got a new email verification link to activate your account." +
                                " Please click the link below to confirm your email address and complete your registration: " + confirmationLink,
                        user.getEmail(),
                        activationToken
                );
                mailService.send(user.getEmail(), "Activation code", message);

                log.info("Email: {}, Body: {}",
                        user.getEmail(),
                        "You have got a new email verification link to activate your account." +
                                " Please click the link below to confirm your email address and complete your registration: " + confirmationLink);
                redisTemplate.opsForValue().set(user.getEmail() + ":activationToken", activationToken, 10, TimeUnit.MINUTES);
                return true;
            }
        }
        return false;
    }

    @Transactional
    public boolean activation(String activationToken){
        User user = userRepository.findByEmailVerificationToken(activationToken)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String storedToken = redisTemplate.opsForValue().get(user.getEmail() + ":activationToken");
        if (storedToken == null) {
            log.error("Token has expired or does not exist in Redis: {}", storedToken);
            throw new TokenException("Email verification has expired. Please resend the activation message to your email address.");
        }

        if (!storedToken.equals(storedToken)) {
            log.error("Token mismatch: expected {} but found {}", storedToken, storedToken);
            throw new TokenException("Email verification has expired. Please resend the activation message to your email address.");
        }
        user.setEmailVerified(true);
        user.setRole(Role.VERIFIED_USER);
        userRepository.saveAndFlush(user);

        redisTemplate.delete(user.getEmail() + ":activationToken");
        log.info("User {} has been verified", user.getEmail());
        return true;
    }

    @Transactional
    public void delete(){
        User user = userRepository.findByEmail(securityContext())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String apiKey = redisTemplate.opsForValue().get("user:" + user.getEmail() + ":api_key");

        if (apiKey == null) {
            throw new TokenException("User login token has expired");
        }
        redisTemplate.delete("user:" + user.getEmail() + ":api_key");
        redisTemplate.delete("x-api-key:" + apiKey);
        userRepository.delete(user);
    }

    @SneakyThrows
    public void usersOfApp(UserCreateDto userCreateEditDto) {
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("Europe/Warsaw"));
        String formattedTime = zonedDateTime.format(
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                        .withLocale(new Locale("pl", "PL")));

        try (BufferedWriter bufferedWriter = new BufferedWriter(
                new FileWriter("animal-shelter/src/main/resources/newUsersOfApp.txt", true))) {
            String record = String.format("%s   |   %s%n", userCreateEditDto.getEmail(), formattedTime);

            bufferedWriter.write(record);
        } catch (IOException e) {
            log.error("Exception: " + e);
        }
    }

    @Transactional
    public UserReadDto updateUserData(UserUpdateDto userUpdateDto) {
        User user = userRepository.findByEmail(securityContext())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Optional.ofNullable(userUpdateDto.getBirthDate()).ifPresent(user::setBirthDate);
        Optional.ofNullable(userUpdateDto.getFirstName()).ifPresent(user::setFirstName);
        Optional.ofNullable(userUpdateDto.getLastName()).ifPresent(user::setLastName);
        Optional.ofNullable(userUpdateDto.getAddress()).ifPresent(user::setAddress);
        Optional.ofNullable(userUpdateDto.getPhone()).ifPresent(user::setPhone);

        User updatedUser = userRepository.saveAndFlush(user);
        return userMapper.toDto(updatedUser);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByEmail(username);
    }

    public User findUserById(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public List<UserReadDto> findAll(){
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null || username.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
        return userRepository.findByEmail(username)
                .map(user -> {
                    Set<GrantedAuthority> authorities = new HashSet<>();
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().toString()));
                    return new org.springframework.security.core.userdetails.User(
                            user.getEmail(),
                            user.getPassword(),
                            authorities
                    );
                })
                .orElseThrow(() -> new UserNotFoundException("Failed to retrieve user:" + username));
    }

    //TODO: method not finished
    @Transactional
    public boolean resetPassword(String username,String password){
        Optional<User> userOptional = userRepository.findByEmail(username);

        if(userOptional.isPresent()){
            User user = userOptional.get();
            user.setPassword(password);
            userRepository.save(user);

            if (!StringUtils.isEmpty(user.getEmail())) {
                String message = String.format(
                        "Hello, %s! \n" +
                                "You can reset your password clicking this link: http://localhost:8080/resetPassword",
                        user.getEmail()
                );
                mailService.send(user.getEmail(), "Activation code", message);
                return true;
            }
        }
        return false;
    }

    private String securityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
