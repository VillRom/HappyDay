package ru.romanchev.happyday.mapper;

import lombok.experimental.UtilityClass;
import ru.romanchev.happyday.dto.UserDto;
import ru.romanchev.happyday.model.User;

@UtilityClass
public class UserMapper {

    public static User dtoToUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setChatId(userDto.getChatId());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setNikName(userDto.getNikName());
        return user;
    }
}
