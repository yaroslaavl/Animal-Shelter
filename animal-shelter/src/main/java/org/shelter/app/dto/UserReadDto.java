package org.shelter.app.dto;

import lombok.Value;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Value
public class UserReadDto {

    Long id;

    String email;

    String password;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate birthDate;

    String firstName;

    String lastName;

    String address;

    String phone;

    String profilePicture;

    String role;

    Boolean emailVerified;

    LocalDateTime createdAt;
}
