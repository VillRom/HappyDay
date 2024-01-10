package ru.romanchev.happyday.service.Impl;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.romanchev.happyday.dto.MessageDto;
import ru.romanchev.happyday.model.CallBackDates;
import ru.romanchev.happyday.service.CallBackQueryService;
import ru.romanchev.happyday.service.JokeService;
import ru.romanchev.happyday.service.MessageService;
import ru.romanchev.happyday.service.PhraseService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Data
@RequiredArgsConstructor
@Slf4j
public class CallbackQueryImpl implements CallBackQueryService {

    private CallbackQuery callbackQuery;

    private final PhraseService phraseService;

    private final MessageService messageService;

    private final JokeService jokeService;

    @Override
    public SendMessage getHappy() {
        log.info("Пользователем  - {} нажата кнопка - 'Ещё' под сообщением /happy", callbackQuery
                .getFrom().getFirstName());
        String response = "Дарю тебе эту фразу:\n" + "\uD83D\uDC4C" + getHappyPhrases() +
                "👌 \nНе стесняйся, нажимай ещё - /happy\nИли кликай кнопку ниже \uD83D\uDC47";
        saveMessage("Нажата кнопка - Ещё \uD83D\uDC47", response, callbackQuery.getMessage().getChatId(),
                callbackQuery.getMessage().getDate());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(callbackQuery.getMessage().getChatId()));
        sendMessage.setText(response);
        sendMessage.setReplyMarkup(oneButtonOnKeyboard(String.valueOf(CallBackDates.GET_HAPPY)));
        return sendMessage;
    }

    @Override
    public SendMessage getJoke() {
        log.info("Пользователем  - {} нажата кнопка - 'Ещё' под сообщением /joke", callbackQuery.getFrom()
                .getFirstName());
        String response;
        String textJoke = getRandomJoke();
        if (textJoke != null)
            response = "Получай анекдот:\n" + textJoke + "\n --------\nНе стесняйся, нажимай ещё" +
                    " - /joke\nИли кликай кнопку ниже \uD83D\uDC47";
        else response = "Извините, в данный момент база данных с анекдотами не заполнена, идет работа по наполнению.\n" +
                "Спасибо за понимание.\nМожете воспользоваться командой - /happy";
        saveMessage("Нажата кнопка - Ещё \uD83D\uDC47 под сообщением /joke", response, callbackQuery.getMessage().getChatId(),
                callbackQuery.getMessage().getDate());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(callbackQuery.getMessage().getChatId()));
        sendMessage.setText(response);
        if (response.startsWith("Получай")) sendMessage.setReplyMarkup(oneButtonOnKeyboard(String
                .valueOf(CallBackDates.GET_JOKE)));
        return sendMessage;
    }

    private String getHappyPhrases() {
        return phraseService.getRandomPhrase().getTextPhrase();
    }

    private String getRandomJoke() {
        return jokeService.getRandomJoke().getTextJoke();
    }

    private void saveMessage(String textIn, String textTo, Long userId, Integer date) {
        MessageDto dto = new MessageDto();
        dto.setDate(new Date(date.longValue() * 1000));
        dto.setUserId(userId);
        dto.setTextIn(textIn);
        dto.setTextTo(textTo);
        messageService.addMessageFromUser(dto);
    }

    private InlineKeyboardMarkup oneButtonOnKeyboard(String callbackData) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var button = new InlineKeyboardButton();
        button.setText("Ещё \uD83E\uDD17");
        button.setCallbackData(callbackData);

        rowInLine.add(button);
        rowsInline.add(rowInLine);
        keyboardMarkup.setKeyboard(rowsInline);
        return keyboardMarkup;
    }

}