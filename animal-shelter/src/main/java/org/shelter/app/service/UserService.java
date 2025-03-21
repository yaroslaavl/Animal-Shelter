package org.shelter.app.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.shelter.app.dto.UserCreateDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.shelter.app.database.entity.enums.Role;
import org.shelter.app.database.entity.User;
import org.shelter.app.database.repository.UserRepository;
import org.shelter.app.dto.UserReadDto;
import org.shelter.app.mapper.UserMapper;

import java.io.*;
import java.net.InetAddress;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final MailService mailService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @SneakyThrows
    @Transactional
    public UserReadDto create(UserCreateDto userDto) {
        InetAddress localhost;
        localhost = InetAddress.getLocalHost();
        String ip = localhost.getHostAddress();

        String activationToken = UUID.randomUUID().toString();
        UserReadDto userReadDto = Optional.of(userDto)
                .map(dto -> {
                    User user = userMapper.toEntity(dto,passwordEncoder);
                    user.setRole(Role.USER);
                    user.setEmailVerificationToken(activationToken);
                    user.setEmailVerified(false);
                    usersOfApp(userDto);
                    return userRepository.saveAndFlush(user);
                })
                .map(userMapper::toDto)
                .orElseThrow();

        if (!StringUtils.isEmpty(userDto.getEmail())) {
            String message = String.format(
                    "Hello, %s! \n" +
                            "Welcome to our family. Please, visit the following link to activate your account:  http://" + ip + ":8080/activate?token=%s",
                    userDto.getEmail(),
                    activationToken
            );
            mailService.send(userDto.getEmail(), "Activation code", message);
        }
        return userReadDto;
    }

    @SneakyThrows
    public void usersOfApp(UserCreateDto userCreateEditDto) {
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("Europe/Warsaw"));
        String formattedTime = zonedDateTime.format(
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                        .withLocale(new Locale("pl", "PL")));

        try (BufferedWriter bufferedWriter = new BufferedWriter(
                new FileWriter("rental-pets-service/src/main/resources/newUsersOfApp.txt", true))) {
            String record = String.format("%s   |   %s%n", userCreateEditDto.getEmail(), formattedTime);
            bufferedWriter.write(record);
        } catch (IOException e) {
            log.error("Exception: " + e);
        }
    }

    @SneakyThrows
    @Transactional
    public boolean resendActivationCode(String username){
        InetAddress localhost;
        localhost = InetAddress.getLocalHost();
        String ip = localhost.getHostAddress();

        Optional<User> optionalUser = userRepository.findByEmail(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            String activationToken = UUID.randomUUID().toString();
            user.setEmailVerificationToken(activationToken);
            user.setEmailVerified(false);
            userRepository.save(user);

            if (!StringUtils.isEmpty(user.getEmail())) {
                String message = String.format(
                        "Hello, %s! \n" +
                                "Welcome to our family. Please, visit the following link to activate your account: http://" + ip + ":8080/activate?token=%s",
                        user.getEmail(),
                        activationToken
                );
                mailService.send(user.getEmail(), "Activation code", message);
                return true;
            }
        }
        return false;
    }

    @Transactional
    public boolean activation(String activationToken){
        User user = userRepository.findByEmailVerificationToken(activationToken);

        if(user != null){
            user.setEmailVerified(true);
            userRepository.save(user);
            return true;
        } else {
            throw new IllegalArgumentException("Not found");
        }
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByEmail(username);
    }

    public User findUserById(Long id){
        Optional<User> optionalUser = userRepository.findById(id);
        return optionalUser.orElse(null);
    }

   /* @Transactional
    public Optional<User> update(Long id, UserCreateDto userCreateEditDto) {
        return userRepository.findById(id)
                .map(user -> {
                    uploadImage(userCreateEditDto.getProfilePicture());
                    userMapper.updateEntityFromDto(userCreateEditDto, user);
                    return userRepository.saveAndFlush(user);
                });
    }*/

    @Transactional
    public boolean delete(Long id){
        return userRepository.findById(id)
                .map(entity -> {
                    userRepository.delete(entity);
                    userRepository.flush();
                    return true;
                })
                .orElse(false);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null || username.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        return userRepository.findByEmail(username)
                .map(user -> {
                    Set<GrantedAuthority> authorities = new HashSet<>();
                    authorities.add(new SimpleGrantedAuthority(user.getRole().toString()));
                    return new org.springframework.security.core.userdetails.User(
                            user.getEmail(),
                            user.getPassword(),
                            authorities
                    );
                })
                .orElseThrow(() -> new UsernameNotFoundException("Failed to retrieve user:" + username));
    }

    public List<UserReadDto> findAll(){
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
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
}
