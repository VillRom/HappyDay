package ru.romanchev.happyday.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.romanchev.happyday.dto.MessageDto;
import ru.romanchev.happyday.mapper.MessageMapper;
import ru.romanchev.happyday.model.User;
import ru.romanchev.happyday.repository.MessageRepository;
import ru.romanchev.happyday.repository.UserRepository;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    private final UserRepository userRepository;

    @Override
    public void addMessageFromUser(MessageDto messageDto) {
        User user = userRepository.findById(messageDto.getUserId()).orElseThrow(() ->
                new EntityNotFoundException("Пользователь с id = " + messageDto.getUserId() + " не найден"));
        messageRepository.save(MessageMapper.dtoToMessage(messageDto, user));
    }
}
