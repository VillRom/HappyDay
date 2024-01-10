package ru.romanchev.happyday.service.Impl;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.romanchev.happyday.dto.MessageDto;
import ru.romanchev.happyday.dto.UserDto;
import ru.romanchev.happyday.model.CallBackDates;
import ru.romanchev.happyday.service.*;

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

    @Value(value = "${admin.bot.id}")
    private Long adminId;

    private static final String TEXT_HELP = "Этот бот создан для улучшения вашего настроения \uD83D\uDC4D.\n" +
            "Бот содержит множество мотивирующих фраз, которые вы можете получить, отправив команду  - /happy боту.\n" +
            "Так же вы можете получить рандомный анекдот от бота по команде - /joke. Слева от поля ввода текста вы увидите меню, " +
            "там лежат доступные команды. Со временем их будет становиться больше. \uD83D\uDE09\n\nБот развивается, " +
            "поэтому буду рад вашему фидбэку и пожеланиям. Их вы можете прислать на почту happy_day_bot@bk.ru";

    private final UserService userService;

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

    @Override
    public SendMessage responseOnBotCommand() {
        SendMessage sendMessage;
        switch (message.getText()) {
            case "/start":
                log.info("Пришло сообщение /start от {}", message.getChat().getFirstName());
                saveUser(message.getChat().getFirstName(), message.getChat().getLastName(),
                        message.getChat().getUserName(), message.getChatId());
                sendMessage = getStart();
                break;
            case "/happy":
                sendMessage = getHappy();
                break;
            case "/info":
                sendMessage = getInfo();
                break;
            case "/joke":
                sendMessage = getJoke();
                break;
            default:
                log.info("От {} пришло неопознанное сообщение с текстом:\n{}",
                        message.getChat().getFirstName(), message.getText());
                sendMessage = defaultMessageTo();
        }
        return sendMessage;
    }

    @Override
    public SendMessage getStart() {
        String response;
        if (messageService.isContainsPhraseStart(message.getChatId(), "/start")) {
            response = message.getChat().getFirstName() + ", рад видеть тебя снова! Скорее выбирай свою фразу дня " +
                    "- /happy\nИли анекдот - /joke";
        } else {
            response = "Привет " + message.getChat().getFirstName() + ", рад видеть тебя!\nСкорее выбирай свою фразу " +
                    "дня - /happy\nИли анекдот - /joke";
        }
        saveMessage(message.getText(), response, message.getChatId(), message.getDate());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        sendMessage.setText(response);
        return sendMessage;
    }

    @Override
    public SendMessage getInfo() {
        log.info("Пришло сообщение /info от {}", message.getChat().getFirstName());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        sendMessage.setText(TEXT_HELP);
        return sendMessage;
    }

    private SendMessage defaultMessageTo() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        sendMessage.setText("На данное сообщение мне нечем ответить, " +
                "пожалуйста используйте эти команды:\n/happy\n/joke");
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

    private void saveUser(String firstName, String lastName, String nikName, Long userId) {
        UserDto dto = new UserDto();
        dto.setId(userId);
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setNikName(nikName);
        userService.addUser(dto);
    }
}
