package org.crystalkste.secretsantatg.bot_config;

import org.crystalkste.secretsantatg.tgBot.MyTelegramBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class BotConfig {

    @Value("${telegram.bot.username}")
    private  String botUsername;
    @Value("${telegram.bot.token}")
    private  String botToken;


    @Bean
    public TelegramBotsApi telegramBotsApi() throws TelegramApiException {
        return new TelegramBotsApi(DefaultBotSession.class);
    }

    @Bean
    public MyTelegramBot myTelegramBot() {
        DefaultBotOptions options = new DefaultBotOptions();
        MyTelegramBot myTelegramBot = new MyTelegramBot(options);
        myTelegramBot.setBotUsername(botUsername);
        myTelegramBot.setBotToken(botToken);
        return myTelegramBot;
    }

    public String getBotUsername() {
        return botUsername;
    }

    public String getBotToken() {
        return botToken;
    }
}
