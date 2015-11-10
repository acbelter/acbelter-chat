package com.acbelter.chat;


import com.acbelter.chat.message.base.User;
import com.acbelter.chat.message.base.UserStore;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

public class AuthorizationService {
    private Scanner in;
    private PrintStream out;
    private UserStore userStore;

    public AuthorizationService(Scanner in,
                                PrintStream out,
                                UserStore userStore) throws IOException {
        this.in = in;
        this.out = out;
        this.userStore = userStore;
    }

    public User loginUser(final String login, final String password) {
        if (login == null || login.isEmpty()) {
            return null;
        }

        if (userStore.isUserExists(login)) {
            User user = userStore.getUser(login);
            if (validatePassword(user, password)) {
                return user;
            } else {
                final int attempts = 3;
                for (int i = 0; i < attempts; i++) {
                    out.println("Invalid password. Try again. Attempts: " + (attempts - i));
                    out.println("Password:");
                    String pass = in.nextLine().trim();
                    if (pass.isEmpty()) {
                        return null;
                    }
                    if (validatePassword(user, pass)) {
                        return user;
                    }
                }
                return null;
            }
        } else {
            out.println("User with this login does not exist.");
            out.println("Do you want to create new user? (y/n or yes/no)");
            String answer = in.nextLine();
            if (answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes")) {
                return createUser();
            }
        }
        return null;
    }

    public User createUser() {
        String login;
        while (true) {
            out.println("New login:");
            login = in.nextLine().trim();

            if (login.isEmpty()) {
                return null;
            }

            if (userStore.isUserExists(login)) {
                out.println("This user already exists.");
            } else {
                break;
            }
        }

        String password = readNewPassword();
        User newUser = new User(login);
        String passwordHash = HashUtil.generateHash(password);
        if (passwordHash != null) {
            newUser.setPasswordHash(passwordHash);
            userStore.addUser(newUser);
            out.println("New user is created.");
            return newUser;
        } else {
            out.println("Unable to create new user.\n");
            return null;
        }
    }

    private String readNewPassword() {
        String newPassword;
        String confirmedNewPassword;
        while (true) {
            out.println("New password:");
            newPassword = in.nextLine();
            if (newPassword.trim().isEmpty()) {
                continue;
            }
            out.println("Confirm new password:");
            confirmedNewPassword = in.nextLine();
            if (newPassword.equals(confirmedNewPassword)) {
                return newPassword;
            } else {
                out.println("Passwords are not equal. Try again.");
            }
        }
    }

    private static boolean validatePassword(User user, String password) {
        if (user == null || user.getPasswordHash() == null || password == null) {
            return false;
        }

        return user.getPasswordHash().equalsIgnoreCase(HashUtil.generateHash(password));
    }
}
