package org.shelter.app.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.shelter.app.validation.AgeLimit;
import org.shelter.app.validation.ContactNumber;
import org.shelter.app.validation.EditAction;

import java.time.LocalDate;

@Data
public class UserUpdateDto {

    @AgeLimit(minAge = 18, groups = EditAction.class)
    private LocalDate birthDate;

    @Size(min = 2, max = 20, groups = EditAction.class)
    @Pattern(regexp = "[A-Za-z]+", groups = EditAction.class)
    private String firstName;

    @Size(min = 2, max = 25, groups = EditAction.class)
    @Pattern(regexp = "[A-Za-z]+", groups = EditAction.class)
    private String lastName;

    @Size(min = 5, max = 60, groups = EditAction.class)
    private String address;

    @ContactNumber(groups = EditAction.class)
    private String phone;
}
