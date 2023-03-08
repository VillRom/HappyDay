package ru.romanchev.happyday.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.romanchev.happyday.config.BotConfig;
import ru.romanchev.happyday.dto.MessageDto;
import ru.romanchev.happyday.dto.UserDto;
import ru.romanchev.happyday.repository.HappyRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final HappyRepository repository;

    private final BotConfig config;

    private final UserService userService;

    private final MessageService messageService;

    /*public TelegramBot(BotConfig config, HappyRepository repository, UserService userService) {
        this.config = config;
        this.repository = repository;
        this.userService = userService;
    }*/

    @Override
    public String getBotToken() {
        return config.getBotKey();
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            String requestText = update.getMessage().getText();
            String nameUser = update.getMessage().getChat().getFirstName();
            String lastName = update.getMessage().getChat().getLastName();
            String nikName = update.getMessage().getChat().getUserName();
            Long userId = update.getMessage().getFrom().getId();
            saveUser(nameUser, lastName, nikName, chatId, userId);
            switch (requestText) {
                case "/start":
                    log.info("Пришло сообщение /start от {}", nameUser);
                    saveMessage(requestText, startCommand(chatId, nameUser), userId, update.getMessage().getDate());
                    break;
                case "/happy":
                    log.info("Пришло сообщение /happy от {}", nameUser);
                    saveMessage(requestText, happyCommand(chatId), userId, update.getMessage().getDate());
                    break;
                default:
                    log.info("От {} пришло неопознанное сообщение с текстом:\n{}", nameUser, requestText);
                    sendMessage(chatId, "На данное сообщение мне нечем ответить, " +
                            "пожалуйста используйте эти команды:\n/happy");
            }
        } else {
            Long chatId = update.getMessage().getChatId();
            String nameUser = update.getMessage().getChat().getFirstName();
            String lastName = update.getMessage().getChat().getLastName();
            String nikName = update.getMessage().getChat().getUserName();
            Long userId = update.getMessage().getFrom().getId();
            saveUser(nameUser, lastName, nikName, chatId, userId);
            sendMessage(chatId, "На данное сообщение мне нечем ответить, " +
                    "пожалуйста используйте эти команды:\n/happy");
        }
    }

    private String startCommand(Long chatId, String name) {
        String response = "Привет " + name + ", рад видеть тебя!\nПока что у меня есть одна команда - /happy\n" +
                "Напиши её мне - узнаешь что произойдет)";
        sendMessage(chatId, response);
        return response;
    }

    private String happyCommand(Long chatId) {
        String response = "Твоя фраза на сегодня:\n" + "\uD83D\uDC4C" + getHappyPhrases(new File("Phrases.txt")) +
                "👌" + "\nНе стесняйся, попробуй ещё раз - /happy";
        sendMessage(chatId, response);
        return response;
    }

    private void sendMessage(Long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private String getHappyPhrases(File file) {
        int number;
        if (!repository.getHappyPhrases().isEmpty()) {
            number = (int) (Math.random() * repository.getHappyPhrases().size());
            return repository.getHappyPhrases().get(number);
        } else {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                List<String> newPhrases = new ArrayList<>();
                while (br.ready()) {
                    newPhrases.add(br.readLine());
                }
                repository.setHappyPhrases(newPhrases);
                number = (int) (Math.random() * repository.getHappyPhrases().size() + 1);
                return repository.getHappyPhrases().get(number);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void saveUser(String firstName, String lastName, String nikName, Long chatId, Long userId) {
        UserDto dto = new UserDto();
        dto.setId(userId);
        dto.setChatId(chatId);
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setNikName(nikName);
        userService.addUser(dto);
    }

    private void saveMessage(String textIn, String textTo, Long userId, Integer date) {
        MessageDto dto = new MessageDto();
        dto.setDate(new Date(date.longValue() * 1000));
        dto.setUserId(userId);
        dto.setTextIn(textIn);
        dto.setTextTo(textTo);
        messageService.addMessageFromUser(dto);
    }
}
