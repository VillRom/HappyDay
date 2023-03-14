package ru.romanchev.happyday.mapper;

import lombok.experimental.UtilityClass;
import ru.romanchev.happyday.dto.UserDto;
import ru.romanchev.happyday.model.User;

@UtilityClass
public class UserMapper {

    public static User dtoToUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setNikName(userDto.getNikName());
        return user;
    }

    public static UserDto userToDto(User user) {
        UserDto userdto = new UserDto();
        userdto.setId(user.getId());
        userdto.setFirstName(user.getFirstName());
        userdto.setLastName(user.getLastName());
        userdto.setNikName(user.getNikName());
        return userdto;
    }
}
