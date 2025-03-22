package org.shelter.app.dto;

import lombok.Data;

import java.util.List;

@Data
public class LoginDto {

    private String email;
    private String password;
}