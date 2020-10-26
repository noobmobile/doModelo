package com.dont.modelo.models.bukkit;

import com.dont.modelo.Terminal;
import com.dont.modelo.database.MainDataManager;
import com.dont.modelo.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public abstract class DoListener implements Listener {

    protected final Terminal main;
    protected final MainDataManager manager;

    public DoListener(Terminal main) {
        this.main = main;
        this.manager = main.getDataManager();
        Bukkit.getPluginManager().registerEvents(this, main);
        Utils.debug(Utils.LogType.INFO, this.getClass().getSimpleName() + " carregado");
    }

}
