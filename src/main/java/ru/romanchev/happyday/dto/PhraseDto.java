package ru.romanchev.happyday.dto;

import lombok.Data;

/**
 * A DTO for the {@link ru.romanchev.happyday.model.Phrase} entity
 */
@Data
public class PhraseDto {

    private Long id;

    private String textPhrase;
}
