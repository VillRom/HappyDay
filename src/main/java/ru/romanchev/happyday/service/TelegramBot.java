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
import ru.romanchev.happyday.dto.MessageDto;
import ru.romanchev.happyday.dto.UserDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private static final String TEXT_HELP = "Этот бот создан для улучшения вашего настроения \uD83D\uDC4D.\n" +
            "Бот содержит множество мотивирующих фраз, которые вы можете получить, отправив команду  - /happy боту.\n" +
            "Так же вы можете получить рандомный анекдот от бота по команде - /joke. Слева от поля ввода текста вы увидите меню, " +
            "там лежат доступные команды. Со временем их будет становиться больше. \uD83D\uDE09\n\nБот развивается, " +
            "поэтому буду рад вашему фидбэку и пожеланиям. Их вы можете прислать на почту happy_day_bot@bk.ru";

    private static final String GET_HAPPY = "GET_HAPPY";

    private static final String GET_JOKE = "GET_JOKE";

    private final BotConfig config;

    private final UserService userService;

    private final MessageService messageService;

    private final PhraseService phraseService;

    private final JokeService jokeService;

    public TelegramBot(BotConfig config, UserService userService,
                       MessageService messageService, PhraseService phraseService, JokeService jokeService) {
        this.config = config;
        this.userService = userService;
        this.messageService = messageService;
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
            Long chatId = update.getMessage().getChatId();
            String requestText = update.getMessage().getText();
            String nameUser = update.getMessage().getChat().getFirstName();
            String lastName = update.getMessage().getChat().getLastName();
            String nikName = update.getMessage().getChat().getUserName();
            if (requestText.startsWith("/sendAllTheUsers") && chatId.equals(config.getAdminId())) {
                var textToSend = requestText.substring(requestText.indexOf(" "));
                List<UserDto> users = userService.getAllUsersWithoutAdmin(chatId);
                for (UserDto user : users) {
                    sendMessageWithKeyboard(twoButtonHappyAndJokeOnKeyboard(user.getId(), textToSend));
                    log.info("Пользователю {} отправлено сообщение - '{}'", user.getFirstName(), textToSend);
                }
                sendMessage(config.getAdminId(), "Рассылка отправлена.");
            } else if (requestText.startsWith("/update") && chatId.equals(config.getAdminId())) {
                sendMessage(chatId, updatePhrasesDbFromFile());
            } else if (requestText.startsWith("/newJoke") && chatId.equals(config.getAdminId())) {
                JokeDto dto = new JokeDto();
                dto.setTextJoke(requestText.substring(requestText.indexOf(" ")));
                sendMessage(chatId, jokeService.addJoke(dto));
            } else {
                switch (requestText) {
                    case "/start":
                        log.info("Пришло сообщение /start от {}", nameUser);
                        saveUser(nameUser, lastName, nikName, chatId);
                        saveMessage(requestText, startCommand(chatId, nameUser), chatId, update.getMessage().getDate());
                        break;
                    case "/happy":
                        log.info("Пришло сообщение /happy от {}", nameUser);
                        happyCommand(requestText, update.getMessage().getDate(), chatId);
                        break;
                    case "/info":
                        log.info("Пришло сообщение /info от {}", nameUser);
                        sendMessage(chatId, TEXT_HELP);
                        break;
                    case "/joke":
                        log.info("Пришло сообщение /joke от {}", nameUser);
                        jokeCommand(requestText, update.getMessage().getDate(), chatId);
                        break;
                    default:
                        log.info("От {} пришло неопознанное сообщение с текстом:\n{}", nameUser, requestText);
                        sendMessage(chatId, "На данное сообщение мне нечем ответить, " +
                                "пожалуйста используйте эти команды:\n/happy");
                }
            }
        } else if (update.hasMessage() && update.getMessage().hasContact() && update.getMessage().getFrom()
                .getId().equals(config.getAdminId())) {
            saveUser(update.getMessage().getContact().getFirstName(), update.getMessage().getContact().getLastName(),
                    update.getMessage().getContact().getFirstName(), update.getMessage().getContact().getUserId());
            sendMessage(config.getAdminId(), "Контакт сохранен");
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            if (callbackData.equals(GET_HAPPY)) {
                log.info("Пользователем  - {} нажата кнопка - 'Ещё' под сообщением /happy", update.getCallbackQuery()
                        .getFrom().getFirstName());
                happyCommand("Нажата кнопка - Ещё \uD83D\uDC47", update.getCallbackQuery().getMessage().getDate(),
                        chatId);
            }
            if (callbackData.equals(GET_JOKE)) {
                log.info("Пользователем  - {} нажата кнопка - 'Ещё' под сообщением /joke", update.getCallbackQuery()
                        .getFrom().getFirstName());
                jokeCommand("Нажата кнопка - Ещё \uD83D\uDC47", update.getCallbackQuery().getMessage().getDate(),
                        chatId);
            }
        } else {
            Long chatId = update.getMessage().getChatId();
            sendMessage(chatId, "На данное сообщение мне нечем ответить, " +
                    "пожалуйста используйте эти команды:\n/happy");
        }
    }

    private String updatePhrasesDbFromFile() {
        return phraseService.updatePhrases();
    }

    private String startCommand(Long chatId, String name) {
        String response;
        if (messageService.isContainsPhraseStart(chatId, "/start")) {
            response = name + ", рад видеть тебя снова! Скорее выбирай свою фразу дня - /happy\nИли анекдот - /joke";
        } else {
            response = "Привет " + name + ", рад видеть тебя!\nСкорее выбирай свою фразу дня - /happy\n" +
                    "Или анекдот - /joke";
        }
        sendMessage(chatId, response);
        return response;
    }

    private void happyCommand(String textIn, Integer date, Long chatId) {
        String response = "Дарю тебе эту фразу:\n" + "\uD83D\uDC4C" + getHappyPhrases() +
                "👌 \nНе стесняйся, нажимай ещё - /happy\nИли кликай кнопку ниже \uD83D\uDC47";
        try {
            execute(oneButtonOnKeyboard(chatId, response, GET_HAPPY));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        saveMessage(textIn, response, chatId, date);
    }

    private void jokeCommand(String textIn, Integer date, Long chatId) {
        String response;
        String textJoke = getJoke();
        if (textJoke != null) {
            response = "Получай анекдот:\n" /*"\uD83D\uDC4C"*/ + textJoke +
                    "\n --------\nНе стесняйся, нажимай ещё - /joke\nИли кликай кнопку ниже \uD83D\uDC47";
        }
        else response = "Извините, в данный момент база данных с анекдотами не заполнена, идет работа по наполнению.\n" +
                "Спасибо за понимание.\nМожете воспользоваться командой - /happy";
        try {
            execute(oneButtonOnKeyboard(chatId, response, GET_JOKE));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        saveMessage(textIn, response, chatId, date);
    }

    private SendMessage oneButtonOnKeyboard(Long chatId, String text, String callbackData) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(text);

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var button = new InlineKeyboardButton();
        button.setText("Ещё \uD83E\uDD17");
        button.setCallbackData(callbackData);

        rowInLine.add(button);
        rowsInline.add(rowInLine);
        keyboardMarkup.setKeyboard(rowsInline);
        sendMessage.setReplyMarkup(keyboardMarkup);
        return sendMessage;
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
        buttonHappy.setCallbackData(GET_HAPPY);
        var buttonJoke = new InlineKeyboardButton();
        buttonJoke.setText("Анекдот");
        buttonJoke.setCallbackData(GET_JOKE);

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

    private void sendMessageWithKeyboard(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private String getHappyPhrases() {
        return phraseService.getRandomPhrase().getTextPhrase();
    }

    private String getJoke() {
        return jokeService.getRandomJoke().getTextJoke();
    }

    private void saveUser(String firstName, String lastName, String nikName, Long userId) {
        UserDto dto = new UserDto();
        dto.setId(userId);
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
