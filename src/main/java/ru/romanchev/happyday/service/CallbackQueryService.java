package ru.romanchev.happyday.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface CallBackQueryService {

    SendMessage getHappy();

    SendMessage getJoke();

    void setCallbackQuery(CallbackQuery callbackQuery);
}
