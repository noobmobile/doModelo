package com.dont.modelo.utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Messages {

    private static Messages instance;

    public static Messages getInstance() {
        if (instance == null) {
            instance = new Messages(Configs.MENSAGENS.getConfig());
        }
        return instance;
    }

    private FileConfiguration config;
    private Map<String, String> messages;
    private Map<String, List<String>> lists;

    private Messages(FileConfiguration config) {
        this.config = config;
        this.messages = new HashMap<>();
        this.lists = new HashMap<>();
        for (String key : config.getKeys(true)) {
            Object value = config.get(key);
            if (value instanceof String) {
                messages.put(key, ChatColor.translateAlternateColorCodes('&', (String) value));
            } else if (value instanceof List) {
                List<String> list = (List<String>) value;
                lists.put(key, list.stream().map(string -> ChatColor.translateAlternateColorCodes('&', string)).collect(Collectors.toList()));
            }
        }
    }

    public String getMessage(String key) {
        return messages.get(key);
    }

    public String getMessage(String key, Object... args) {
        return new MessageFormat(messages.get(key)).format(args);
    }

    public List<String> getMessages(String key) {
        return lists.get(key);
    }

    public List<String> getMessages(String key, Object... args) {
        return lists.get(key).stream().map(s -> new MessageFormat(s).format(args)).collect(Collectors.toList());
    }

}
