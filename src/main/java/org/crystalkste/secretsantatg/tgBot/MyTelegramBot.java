package org.crystalkste.secretsantatg.tgBot;

import lombok.Setter;
import org.crystalkste.secretsantatg.model.GiftAssignment;
import org.crystalkste.secretsantatg.model.UserService;
import org.crystalkste.secretsantatg.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MyTelegramBot extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(MyTelegramBot.class);
    @Setter
    private String botUsername;
    @Setter
    private String botToken;

    @Autowired
    private UserService userService;

    @Autowired
    private GiftAssignment giftAssignment;

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public MyTelegramBot(DefaultBotOptions options) {
        super(options);
    }

    @PostConstruct
    private void initializeScheduler() {
        scheduleGiftAssignments();
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        String text = "\uD83C\uDF85 Привет! Я твой Telegram бот для игры \"Секретный Санта\". \uD83C\uDF84\n" +
                "✨ Используйте команду /secretSanta для отправки вашего имени и желания. \uD83C\uDF81\n"
                + "⏳ У вас есть ровно 7 дней, чтобы набрать участников! \uD83C\uDF89\n";
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatIdLong = update.getMessage().getChatId();
            String chatId = String.valueOf(chatIdLong);
            String userName = update.getMessage().getFrom().getFirstName();
            String username = update.getMessage().getFrom().getUserName();  // Получение имени пользователя

            if (messageText.startsWith("/")) {
                switch (messageText) {
                    case "/start":
                        sendMessage(chatId, "Привет! " + userName + "\n" + text, false);
                        break;
                    case "/myid":
                        sendMessage(chatId, "Ваш ID: " + chatId, false);
                        break;
                    case "/secretSanta":
                        sendMessage(chatId, "Введите ваше имя и желание через точку с запятой.", false);
                        break;
                    case "/listUsers":
                        List<Users> users = userService.findAllUsers();
                        StringBuilder participants = new StringBuilder("Участники:\n");
                        for (Users user : users) {
                            String userLink = "<a href=\"tg://user?id=" + user.getTelegramId() + "\">" + user.getName() + "</a>";
                            participants.append(userLink)
                                    .append(": ")
                                    .append(user.getWish())
                                    .append(" (")
                                    .append(user.getUsername())
                                    .append(" - ID: ")
                                    .append(user.getTelegramId())
                                    .append(")\n");
                        }
                        sendMessage(chatId, participants.toString(), true);
                        break;
                    case "/listId":
                        List<Users> users1 = userService.findTelegramId();
                        StringBuilder parts = new StringBuilder();
                        for (Users user : users1) {
                            String userLink = "<a href=\"tg://user?id=" + user.getTelegramId() + "\">" + user.getName() + "</a>";
                            parts.append(userLink).append(": ").append(user.getTelegramId()).append("\n");
                        }
                        sendMessage(chatId, parts.toString(), true);
                        break;
                    case "/deleteUsers":
                        sendMessage(chatId,"Введите ID пользователя для удаления чрез 'ID:'");
                        break;
                    default:
                        sendMessage(chatId, "Неизвестная команда. Попробуйте /start, /myid, /secretSanta или /listUsers.", false);
                        break;
                }
            } else if (messageText.contains(";")) {
                String[] userDetails = messageText.split(";", 2);
                if (userDetails.length == 2) {
                    String name = userDetails[0].trim();
                    String wish = userDetails[1].trim();
                    userService.saveUsers(chatId, name, wish, "@" + username);
                    sendMessage(chatId, "Ваше желание сохранено: " + wish, false);

                } else {
                    sendMessage(chatId, "Пожалуйста, введите ваше имя и желание через точку с запятой.", false);
                }
            }
            else if(messageText.startsWith("ID:")){
                String textId = messageText.substring(3).trim();
                userService.deleteUserByTelegramId(textId);
                sendMessage(chatId,"Пользователь с ID"+ textId+" был удален");
            }else {
                sendMessage(chatId,"Пользователя с таким ID нет ");
            }
        }
    }

    private void scheduleGiftAssignments() {
        // Установка начальной задержки на 7 дней и периодичности на 7 дней
        scheduler.scheduleAtFixedRate(this::assignGiftsToUsers, 7, 7, TimeUnit.DAYS);
        logger.info("Scheduled gift assignment task to run every 7 days with initial delay of 7 days");
    }


    private void assignGiftsToUsers() {
        logger.info("Executing gift assignment task");
        List<Users> users = userService.findAllUsers();
        if (users.size() % 2 == 0) {
            for (Users user : users) {
                if (!giftAssignment.assignments.containsKey(user.getTelegramId())) {
                    Users randomUser = getRandomUser(user.getTelegramId());
                    if (randomUser != null) {
                        giftAssignment.assignGift(user.getTelegramId(), randomUser.getTelegramId());
                        String recipientLink = randomUser.getUsername() != null
                                ? "<a href=\"tg://user?id=" + randomUser.getTelegramId() + "\">" + randomUser.getName() + "</a> (@" + randomUser.getUsername() + ")"
                                : randomUser.getName() + " (нет ссылки на профиль)";
                        sendMessage(user.getTelegramId(), "Вам нужно подарить подарок " + recipientLink + ". Его желание: " + randomUser.getWish(), true);
                        logger.info("Assigned " + user.getTelegramId() + " to give a gift to " + randomUser.getTelegramId());
                    }
                }
            }
        } else {
            logger.info("Number of users is not even, skipping gift assignment");
        }
    }

    private Users getRandomUser(String uniqueTelegramId) {
        List<Users> users = userService.findAllUsers();
        List<Users> filteredUsers = new ArrayList<>();
        for (Users user : users) {
            if (!user.getTelegramId().equals(uniqueTelegramId) && !giftAssignment.isAssigned(user.getTelegramId())) {
                filteredUsers.add(user);
            }
        }
        if (!filteredUsers.isEmpty()) {
            Random random = new Random();
            return filteredUsers.get(random.nextInt(filteredUsers.size()));
        }
        return null;
    }

    private void sendMessage(String chatId, String text, boolean isHtml) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        if (isHtml) {
            message.enableHtml(true);
        }
        try {
            execute(message);
            logger.info("Sent message: " + message.getText());
        } catch (Exception e) {
            logger.error("Failed to send message: " + e.getMessage(), e);
        }
    }

    private void sendMessage(String chatId, String text) {
        sendMessage(chatId, text, false); // Вызов основного метода, передавая false для isHtml по умолчанию
    }
}
