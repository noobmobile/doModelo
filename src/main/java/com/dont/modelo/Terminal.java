package com.dont.modelo;

import com.dont.modelo.bukkit.PlayerJoinQuit;
import com.dont.modelo.models.AbstractTerminal;
import com.dont.modelo.models.database.User;

public class Terminal extends AbstractTerminal {

    @Override
    protected void preSetup() {
        new PlayerJoinQuit(this);
    }

    @Override
    protected void posSetup() {
        User.loadAll(getDataManager().USERS);
    }

    @Override
    protected void disable() {

    }

}
