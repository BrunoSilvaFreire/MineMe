package me.ddevil.mineme;

import java.util.HashMap;

public class MessageManager {

    private static final HashMap<String, String> messages = new HashMap();

    public static void loadMessages() {
    }

    public static String getMessage(String msg) {
        return parseTags(messages.get(msg));
    }

    private static String parseTags(String input) {
        return input;
    }
}
