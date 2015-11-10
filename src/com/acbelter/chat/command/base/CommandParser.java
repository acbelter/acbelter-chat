package com.acbelter.chat.command.base;

import java.util.Arrays;

public class CommandParser {
    public static boolean isCommand(String line) {
        return line.matches("^/\\S+.*$");
    }

    public static String parseName(String line) {
        if (!isCommand(line)) {
            return null;
        }

        return line.split(" ")[0].substring(1);
    }

    public static String[] parseArgs(String line) {
        if (!isCommand(line)) {
            return null;
        }

        String[] parts = line.split(" ");
        if (parts.length == 1) {
            return new String[0];
        } else {
            return Arrays.copyOfRange(parts, 1, parts.length);
        }
    }
}
