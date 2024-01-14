package ru.romanchev.happyday.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.romanchev.happyday.config.BotConfig;
import ru.romanchev.happyday.dto.JokeDto;
import ru.romanchev.happyday.dto.UserDto;
import ru.romanchev.happyday.model.CallBackDates;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;

    private final UserService userService;

    private final CallBackQueryService callbackQueryService;

    private final TelegramMessage telegramMessage;

    private final PhraseService phraseService;

    private final JokeService jokeService;

    public TelegramBot(BotConfig config, UserService userService, CallBackQueryService callbackQueryService,
                       TelegramMessage telegramMessage, PhraseService phraseService,
                       JokeService jokeService) {
        this.config = config;
        this.userService = userService;
        this.callbackQueryService = callbackQueryService;
        this.telegramMessage = telegramMessage;
        this.phraseService = phraseService;
        this.jokeService = jokeService;
        List<BotCommand> botCommandList = new ArrayList<>();
        botCommandList.add(new BotCommand("/start", "Команда для запуска бота"));
        botCommandList.add(new BotCommand("/happy", "Получить мотивирующую фразу"));
        botCommandList.add(new BotCommand("/info", "Описание бота"));
        botCommandList.add(new BotCommand("/joke", "Получить анектод"));
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
            telegramMessage.setMessage(update.getMessage());
            Long chatId = update.getMessage().getChatId();
            String requestText = update.getMessage().getText();
            if (requestText.startsWith("/sendAllTheUsers") && chatId.equals(config.getAdminId())) {
                var textToSend = requestText.substring(requestText.indexOf(" "));
                List<UserDto> users = userService.getAllUsersWithoutAdmin(chatId);
                for (UserDto user : users) {
                    sendMessage(twoButtonHappyAndJokeOnKeyboard(user.getId(), textToSend));
                    log.info("Пользователю {} отправлено сообщение - '{}'", user.getFirstName(), textToSend);
                }
                sendMessage(config.getAdminId(), "Рассылка отправлена.");
            } else if (requestText.startsWith("/update") && chatId.equals(config.getAdminId())) {
                sendMessage(chatId, updatePhrasesDbFromFile());
                sendMessage(chatId, updateJokesFromWebsite());
            } else if (requestText.startsWith("/newJoke") && chatId.equals(config.getAdminId())) {
                JokeDto dto = new JokeDto();
                dto.setTextJoke(requestText.substring(requestText.indexOf(" ")));
                sendMessage(chatId, jokeService.addJoke(dto));
            } else {
                sendMessage(telegramMessage.responseOnBotCommand());
            }
        } else if (update.hasMessage() && update.getMessage().hasContact() && update.getMessage().getFrom()
                .getId().equals(config.getAdminId())) {
            saveUser(update.getMessage().getContact().getFirstName(), update.getMessage().getContact().getLastName(),
                    update.getMessage().getContact().getFirstName(), update.getMessage().getContact().getUserId());
            sendMessage(config.getAdminId(), "Контакт сохранен");
        } else if (update.hasCallbackQuery()) {
            callbackQueryService.setCallbackQuery(update.getCallbackQuery());
            String callbackData = update.getCallbackQuery().getData();
            if (callbackData.equals(String.valueOf(CallBackDates.GET_HAPPY))) {
                sendMessage(callbackQueryService.getHappy());
            }
            if (callbackData.equals(String.valueOf(CallBackDates.GET_JOKE))) {
                sendMessage(callbackQueryService.getJoke());
            }
        } else {
            Long chatId = update.getMessage().getChatId();
            sendMessage(chatId, "На данное сообщение мне нечем ответить, " +
                    "пожалуйста используйте эти команды:\n/happy");
        }
    }

    private String updateJokesFromWebsite() {
        return jokeService.addJokesFromWebsite();
    }

    private String updatePhrasesDbFromFile() {
        return phraseService.updatePhrases();
    }

    private SendMessage twoButtonHappyAndJokeOnKeyboard(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(text);

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var buttonHappy = new InlineKeyboardButton();
        buttonHappy.setText("Фраза");
        buttonHappy.setCallbackData(String.valueOf(CallBackDates.GET_HAPPY));
        var buttonJoke = new InlineKeyboardButton();
        buttonJoke.setText("Анекдот");
        buttonJoke.setCallbackData(String.valueOf(CallBackDates.GET_JOKE));

        rowInLine.add(buttonHappy);
        rowInLine.add(buttonJoke);
        rowsInline.add(rowInLine);
        keyboardMarkup.setKeyboard(rowsInline);
        sendMessage.setReplyMarkup(keyboardMarkup);
        return sendMessage;
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

    private void sendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
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
