package com.miniinstagram.search.servlet;
import com.miniinstagram.search.dao.UserDao; // You’ll need to add user-service classes here
import com.miniinstagram.search.model.User;
import com.miniinstagram.search.lucene.LuceneManager;

import java.util.List;

public class ReindexUsersMain {
    public static void main(String[] args) throws Exception {
        UserDao userDao = new UserDao(); // Pulls users from DB
        List<User> users = userDao.getAllUsers(-1);

        for (User user : users) {
            LuceneManager.indexUser(
                String.valueOf(user.getId()),
                user.getUsername(),
                user.getDescription() != null ? user.getDescription() : ""
            );
        }

        System.out.println("Reindexing completed!");
    }
}