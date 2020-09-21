package com.dont.modelo.models.bukkit;

import com.dont.modelo.models.AbstractTerminal;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public abstract class DoCommandListener extends DoCommand implements Listener {

    public DoCommandListener(AbstractTerminal main, String command) {
        super(main, command);
        Bukkit.getPluginManager().registerEvents(this, main);
    }
}
