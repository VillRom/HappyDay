package ru.romanchev.happyday.service;

import ru.romanchev.happyday.dto.UserDto;

public interface UserService {

    void addUser(UserDto user);

    UserDto getUserById(Long userId);
}
