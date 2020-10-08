package com.dont.modelo.models.bukkit;

import com.dont.modelo.Terminal;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public abstract class DoCommandListener extends DoCommand implements Listener {

    public DoCommandListener(Terminal main, String command) {
        super(main, command);
        Bukkit.getPluginManager().registerEvents(this, main);
    }
}
