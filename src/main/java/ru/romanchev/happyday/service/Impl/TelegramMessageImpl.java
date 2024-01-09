package ru.romanchev.happyday.service.Impl;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.romanchev.happyday.dto.MessageDto;
import ru.romanchev.happyday.model.CallBackDates;
import ru.romanchev.happyday.service.JokeService;
import ru.romanchev.happyday.service.MessageService;
import ru.romanchev.happyday.service.PhraseService;
import ru.romanchev.happyday.service.TelegramMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Data
@Slf4j
public class TelegramMessageImpl implements TelegramMessage {

    private Message message;
    private final PhraseService phraseService;

    private final MessageService messageService;

    private final JokeService jokeService;

    @Override
    public SendMessage getHappy() {
        log.info("Пришло сообщение /happy от {}", message.getChat().getFirstName());
        String response = "Дарю тебе эту фразу:\n" + "\uD83D\uDC4C" + getHappyPhrases() +
                "👌 \nНе стесняйся, нажимай ещё - /happy\nИли кликай кнопку ниже \uD83D\uDC47";
        saveMessage(message.getText(), response, message.getChatId(), message.getDate());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        sendMessage.setText(response);
        sendMessage.setReplyMarkup(oneButtonOnKeyboard(String.valueOf(CallBackDates.GET_HAPPY)));
        return sendMessage;
    }

    @Override
    public SendMessage getJoke() {
        log.info("Пришло сообщение /joke от {}", message.getChat().getFirstName());
        String response;
        String textJoke = getRandomJoke();
        if (textJoke != null)
            response = "Получай анекдот:\n" + textJoke + "\n --------\nНе стесняйся, нажимай ещё" +
                    " - /joke\nИли кликай кнопку ниже \uD83D\uDC47";
        else response = "Извините, в данный момент база данных с анекдотами не заполнена, идет работа по наполнению.\n" +
                "Спасибо за понимание.\nМожете воспользоваться командой - /happy";
        saveMessage(message.getText(), response, message.getChatId(), message.getDate());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        sendMessage.setText(response);
        if (response.startsWith("Получай")) sendMessage.setReplyMarkup(oneButtonOnKeyboard(String
                .valueOf(CallBackDates.GET_JOKE)));
        return sendMessage;
    }

    private String getRandomJoke() {
        return jokeService.getRandomJoke().getTextJoke();
    }

    private String getHappyPhrases() {
        return phraseService.getRandomPhrase().getTextPhrase();
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
