package ru.romanchev.happyday.mapper;

import lombok.experimental.UtilityClass;
import ru.romanchev.happyday.dto.MessageDto;
import ru.romanchev.happyday.model.Message;
import ru.romanchev.happyday.model.User;

@UtilityClass
public class MessageMapper {

    public static Message dtoToMessage(MessageDto dto, User user) {
        Message message = new Message();
        message.setDate(dto.getDate().getTime());
        message.setUser(user);
        message.setTextIn(dto.getTextIn());
        message.setTextTo(dto.getTextTo());
        return message;
    }
}
