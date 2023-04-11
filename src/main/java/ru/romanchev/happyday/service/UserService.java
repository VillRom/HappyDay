package ru.romanchev.happyday.service;

import ru.romanchev.happyday.dto.UserDto;

import java.util.List;

public interface UserService {

    void addUser(UserDto user);

    List<UserDto> getAllUsersWithoutAdmin(Long idAdmin);

    UserDto getUserById(Long userId);

    List<Long> getAllUsersIdWithoutAdminId(Long idAdmin);
}
