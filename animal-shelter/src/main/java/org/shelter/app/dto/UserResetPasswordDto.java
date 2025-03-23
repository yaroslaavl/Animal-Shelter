package org.shelter.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.shelter.app.validation.EditAction;

@Data
public class UserResetPasswordDto {

    @NotBlank(groups = EditAction.class)
    String oldPassword;

    @Size(min = 8,max = 20, groups = EditAction.class)
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,}$", groups = EditAction.class)
    String newPassword;
}
