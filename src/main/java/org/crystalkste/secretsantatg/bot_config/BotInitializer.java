package org.crystalkste.secretsantatg.bot_config;

import org.crystalkste.secretsantatg.tgBot.MyTelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class BotInitializer {
    private static final Logger logger = LoggerFactory.getLogger(BotInitializer.class);
    private final MyTelegramBot myTelegramBot;

    @Autowired
    public BotInitializer(MyTelegramBot myTelegramBot) {
        this.myTelegramBot = myTelegramBot;
    }

    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(myTelegramBot);
            logger.info("Telegram bot registered successfully");
        } catch (TelegramApiException e) {
            logger.error("Failed to register bot: " + e.getMessage(), e);
        }
    }
}

