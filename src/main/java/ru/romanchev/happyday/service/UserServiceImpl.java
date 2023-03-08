package ru.romanchev.happyday.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.romanchev.happyday.dto.UserDto;
import ru.romanchev.happyday.mapper.UserMapper;
import ru.romanchev.happyday.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    @Override
    public void addUser(UserDto user) {
        if (userRepository.existsById(user.getId())) {
            log.info("Пользователь {} уже существует", user);
            return;
        }
        userRepository.save(UserMapper.dtoToUser(user));
    }
}
