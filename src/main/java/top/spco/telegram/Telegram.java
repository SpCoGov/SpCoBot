package top.spco.telegram;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import top.spco.SpCoBot;
import top.spco.core.Platform;
import top.spco.core.PlatformAdapter;
import top.spco.config.Configs;

import java.io.File;

public class Telegram extends PlatformAdapter {
    private static Telegram instance;
    static final SpCoBot bot = SpCoBot.getInstance();
    String botToken;
    TelegramBotsLongPollingApplication longPolling;
    BotSession session;
    TelegramClient telegramClient;

    static {
        Configurator.setLevel("org.telegram.telegrambots", Level.ERROR);
    }

    private Telegram() {
        super(Platform.TELEGRAM);
        File directory = new File(System.getProperty("user.dir"));
        SpCoBot.dataFolder = new File(directory, "data");
        SpCoBot.configFolder = new File(directory, "config");
        SpCoBot.cacheFolder = new File(directory, "cache");
        SpCoBot.pluginFile = directory;
        bot.initOthers();
        bot.setMessageService(new TelegramMessageServiceImpl());
        try {
            botToken = Configs.BOT.getTelegramBotToken();
            longPolling = new TelegramBotsLongPollingApplication();
            session = longPolling.registerBot(botToken, new TelegramLongPolling());
            telegramClient = new OkHttpTelegramClient(botToken);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public static Telegram getInstance() {
        if (instance == null) {
            instance = new Telegram();
        }
        return instance;
    }

    static String getUserNick(User user) {
        StringBuilder sb = new StringBuilder(user.getFirstName());
        if (user.getLastName() != null && !user.getLastName().isEmpty()) {
            sb.append(" ").append(user.getLastName());
        }
        return sb.toString();
    }

    static String getUserNick(Chat user) {
        StringBuilder sb = new StringBuilder(user.getFirstName());
        if (user.getLastName() != null && !user.getLastName().isEmpty()) {
            sb.append(" ").append(user.getLastName());
        }
        return sb.toString();
    }

    static User getSelf() {
        GetMe getMe = GetMe.builder()
                .build();
        try {
            return Telegram.getInstance().telegramClient.execute(getMe);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
