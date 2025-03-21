package org.shelter.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.shelter.app.database.entity.enums.Role;
import org.shelter.app.validation.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.Random;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateDto {

    @NotBlank(groups = CreateAction.class)
    @Email(groups = CreateAction.class)
    private String email;

    @NotBlank(groups = CreateAction.class)
    @Size(min = 8,max = 20, groups = CreateAction.class)
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,}$", groups = CreateAction.class)
    private String password;

    @Size(min = 2, max = 20, groups = CreateAction.class)
    @Pattern(regexp = "[A-Za-z]+", groups = CreateAction.class)
    private String firstName;

    @Size(min = 2, max = 25, groups = CreateAction.class)
    @Pattern(regexp = "[A-Za-z]+", groups = CreateAction.class)
    private String lastName;

    private Role role;

    private Boolean emailVerified;

    private String emailVerificationToken;

    private LocalDateTime createdAt;

    public static UserCreateDto createNewUser(String username, String firstname, String lastname, Role role, Boolean emailVerified, String emailVerificationToken, LocalDateTime createdAt) {
        Random random = new Random();
        int passwordLength = random.nextInt(12) + 5;
        String defaultPassword = RandomStringUtils.randomAlphanumeric(passwordLength);
        return new UserCreateDto(username, defaultPassword, firstname, lastname, role, false, emailVerificationToken, createdAt);
    }
}
