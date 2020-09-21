package com.dont.modelo.models.bukkit;

import com.dont.modelo.database.DataManager;
import com.dont.modelo.models.AbstractTerminal;
import com.dont.modelo.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public abstract class DoListener implements Listener {

    protected final AbstractTerminal main;
    protected final DataManager manager;

    public DoListener(AbstractTerminal main) {
        this.main = main;
        this.manager = main.getManager(DataManager.class);
        Bukkit.getPluginManager().registerEvents(this, main);
        Utils.debug(Utils.LogType.INFO, this.getClass().getSimpleName() + " carregado");
    }

}
