package org.shelter.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.shelter.app.validation.CreateAction;

@Data
public class SpeciesCreateEditDto {

    @NotBlank(groups = CreateAction.class)
    private String name;
}
