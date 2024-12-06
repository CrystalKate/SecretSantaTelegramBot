package org.crystalkste.secretsantatg;

import org.crystalkste.secretsantatg.bot_config.BotConfig;
import org.crystalkste.secretsantatg.model.UserService;
import org.crystalkste.secretsantatg.repositories.UsersItemRepositories;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SecretSantaTgApplication {
    public static void main(String[] args) {
        SpringApplication.run(SecretSantaTgApplication.class, args);
    }

    @Bean
    CommandLineRunner printProperties(BotConfig botConfig) {
        return args -> {
            System.out.println("botUsername: " + botConfig.getBotUsername());
            System.out.println("botToken: " + botConfig.getBotToken());
        };
    }
}
