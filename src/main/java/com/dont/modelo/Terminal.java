package com.dont.modelo;

import com.dont.modelo.bukkit.PlayerJoinQuit;
import com.dont.modelo.models.AbstractTerminal;
import com.dont.modelo.models.database.User;
import com.dont.modelo.utils.Configs;

public class Terminal extends AbstractTerminal {

    @Override
    protected void preSetup() {
        Configs.setup();
        User.loadAll(getDataManager().USERS);
    }

    @Override
    protected void posSetup() {
        new PlayerJoinQuit(this);
    }

    @Override
    protected void disable() {

    }

}
