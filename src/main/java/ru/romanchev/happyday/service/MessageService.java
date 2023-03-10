package ru.romanchev.happyday.service;

import ru.romanchev.happyday.dto.MessageDto;

public interface MessageService {

    void addMessageFromUser(MessageDto messageDto);

    boolean isContainsPhraseStart(Long idUser, String phrase);
}
