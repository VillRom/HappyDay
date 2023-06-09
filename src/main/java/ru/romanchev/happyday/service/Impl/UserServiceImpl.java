package ru.romanchev.happyday.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.romanchev.happyday.dto.UserDto;
import ru.romanchev.happyday.mapper.UserMapper;
import ru.romanchev.happyday.repository.UserRepository;
import ru.romanchev.happyday.service.UserService;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public void addUser(UserDto user) {
        if (userRepository.existsById(user.getId())) {
            log.info("Пользователь {} уже существует", user);
            return;
        }
        userRepository.save(UserMapper.dtoToUser(user));
    }

    @Override
    public UserDto getUserById(Long userId) {
        return UserMapper.userToDto(userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("Пользователь на найден")));
    }

    @Override
    public List<UserDto> getAllUsersWithoutAdmin(Long idAdmin) {
        return UserMapper.usersToUsersDto(userRepository.findAllByIdNot(idAdmin));
    }

    @Override
    public List<Long> getAllUsersIdWithoutAdminId(Long idAdmin) {
        return userRepository.findAllUsersId(idAdmin);
    }
}
