package com.dont.modelo.models.bukkit;

import com.dont.modelo.Terminal;
import com.dont.modelo.database.DataManager;
import com.dont.modelo.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;

public abstract class DoCommand implements CommandExecutor {

    protected final Terminal main;
    protected final DataManager manager;

    public DoCommand(Terminal main, String command) {
        this.main = main;
        this.manager = main.getDataManager();
        main.getCommand(command).setExecutor(this);
        Utils.debug(Utils.LogType.INFO, this.getClass().getSimpleName() + " carregado");
    }

}
