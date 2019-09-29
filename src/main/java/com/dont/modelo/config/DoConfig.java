package com.dont.modelo.config;

import com.dont.modelo.Terminal;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public abstract class DoConfig {

    private Terminal main;
    public DoConfig(Terminal main) {
        this.main = main;
    }

    public <T> T get(String path){
        if (main == null) main = Terminal.getPlugin(Terminal.class);
        Object object = main.getConfig().get(path);

        if (object instanceof String) object = ChatColor.translateAlternateColorCodes('&', object.toString());
        else if (object instanceof List){
            List<String> list = new ArrayList<>();
            for (String s : (List<String>) object) list.add(ChatColor.translateAlternateColorCodes('&', s));
            object = list;
        }

        return (T) object;
    }

    public <T> T get(String path, Class<T> clazz){
        if (main == null) main = Terminal.getPlugin(Terminal.class);
        Object object = main.getConfig().get(path);

        if (object instanceof String) object = ChatColor.translateAlternateColorCodes('&', object.toString());
        else if (object instanceof List){
            List<String> list = new ArrayList<>();
            for (String s : (List<String>) object) list.add(ChatColor.translateAlternateColorCodes('&', s));
            object = list;
        }

        return (T) object;
    }

}
