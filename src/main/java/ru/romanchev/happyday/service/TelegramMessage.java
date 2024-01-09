package ru.romanchev.happyday.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface TelegramMessage {

    SendMessage getHappy();

    SendMessage getJoke();

    void setMessage(Message message);
}
