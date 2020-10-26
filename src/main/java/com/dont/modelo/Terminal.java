package com.dont.modelo;

import com.dont.modelo.bukkit.PlayerJoinQuit;
import com.dont.modelo.database.DataManager;
import com.dont.modelo.models.AbstractTerminal;

public class Terminal extends AbstractTerminal {
    public Terminal() {
        super(DataManager::loadOnline);
    }

    @Override
    protected void preSetup() {
        new PlayerJoinQuit(this);
    }

    @Override
    protected void posSetup() {

    }

    @Override
    protected void disable() {

    }

}
