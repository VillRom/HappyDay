package ru.romanchev.happyday.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
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
public class TelegramBot extends TelegramLongPollingBot {

    private static final String TEXT_HELP = "Этот бот создан для улучшения вашего настроения \uD83D\uDC4D.\n" +
            "Бот содержит множество мотивирующих фраз, которые вы можете получить, отправив команду  - /happy боту.\n" +
            "Слева от поля ввода текста вы увидите меню, там лежат доступные команды. Со временем их будет становиться " +
            "больше. \uD83D\uDE09\n\nБот развивается, поэтому буду рад вашему фидбэку и пожеланиям. Их вы можете " +
            "прислать на почту happy_day_bot@bk.ru";

    private final HappyRepository repository;

    private final BotConfig config;

    private final UserService userService;

    private final MessageService messageService;

    public TelegramBot(HappyRepository repository, BotConfig config, UserService userService,
                       MessageService messageService) {
        this.repository = repository;
        this.config = config;
        this.userService = userService;
        this.messageService = messageService;
        List<BotCommand> botCommandList = new ArrayList<>();
        botCommandList.add(new BotCommand("/start", "Is command run bot"));
        botCommandList.add(new BotCommand("/happy", "Get happy phrase"));
        botCommandList.add(new BotCommand("/info", "Help use this bot"));
        //TODO botCommandList.add(new BotCommand("/mydata", "Get your data store"));
        //TODO botCommandList.add(new BotCommand("/deletedata", "Delete my data"));
        //TODO botCommandList.add(new BotCommand("/settings", "Set your preferences"));
        try{
            this.execute(new SetMyCommands(botCommandList, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

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
            if (isZhim(requestText)) {
                log.info("От {} пришло сообщение с текстом:\n{}", nameUser, requestText);
                sendMessage(chatId, "Лох\nНо это тайна \uD83E\uDD2B");
            } else {
                switch (requestText) {
                    case "/start":
                        log.info("Пришло сообщение /start от {}", nameUser);
                        saveMessage(requestText, startCommand(chatId, nameUser, userId), userId, update.getMessage().getDate());
                        break;
                    case "/happy":
                        log.info("Пришло сообщение /happy от {}", nameUser);
                        saveMessage(requestText, happyCommand(chatId), userId, update.getMessage().getDate());
                        break;
                    case "/info":
                        log.info("Пришло сообщение /info от {}", nameUser);
                        sendMessage(chatId, TEXT_HELP);
                        break;
                    default:
                        log.info("От {} пришло неопознанное сообщение с текстом:\n{}", nameUser, requestText);
                        sendMessage(chatId, "На данное сообщение мне нечем ответить, " +
                                "пожалуйста используйте эти команды:\n/happy");
                }
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
    }//ToDo Не забудь сделать новую ветку!!!!!!!!!

    private boolean isZhim(String requestText) {
        return !requestText.matches(".*\\s.*") && requestText.toLowerCase().contains("жимб");
    }

    private String startCommand(Long chatId, String name, Long userId) {
        String response;
        if (messageService.isContainsPhraseStart(userId, "/start")) {
            response = name + ", рад видеть тебя снова! Скорее выбирай свою фразу дня - /happy";
        } else {
            response = "Привет " + name + ", рад видеть тебя!\nПока что у меня есть одна команда - \n/happy\n" +
                    "Напиши её мне - узнаешь что произойдет)";
        }
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
