package ru.romanchev.happyday.dto;

import lombok.Data;

/**
 * A DTO for the {@link ru.romanchev.happyday.model.User} entity
 */
@Data
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String nikName;
}