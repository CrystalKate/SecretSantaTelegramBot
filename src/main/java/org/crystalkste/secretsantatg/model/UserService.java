package org.crystalkste.secretsantatg.model;

import org.crystalkste.secretsantatg.repositories.UsersItemRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class UserService {
    @Autowired
    private UsersItemRepositories usersItemRepositories;


    public Users saveUsers(String telegramId, String name, String wish,String userNameLink) {
        Users user = new Users();
        user.setTelegramId(telegramId);
        user.setName(name);
        user.setWish(wish);
        user.setUsername(userNameLink);
        return usersItemRepositories.save(user);
    }
    public List<Users> findAllUsers() {
        return usersItemRepositories.findAll();
    }
    public List<Users> findTelegramId() {
        return usersItemRepositories.findAll();
    }
    public void deleteUserByTelegramId(String telegramId) {
        usersItemRepositories.deleteById(telegramId);
    }

}
