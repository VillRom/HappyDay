package ru.romanchev.happyday.dto;

import lombok.Data;
import ru.romanchev.happyday.model.Message;

import java.util.Date;


/**
 * A DTO for the {@link Message} entity
 */
@Data
public class MessageDto {
    private String textIn;

    private Long userId;

    private String textTo;
    private Date date;
}