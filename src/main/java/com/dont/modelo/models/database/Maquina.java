package com.dont.modelo.models.database;

import com.dont.modelo.database.Keyable;
import org.bukkit.Location;

public class Maquina implements Keyable<Location> {

    private Location location;

    public Maquina(Location location) {
        this.location = location;
    }

    @Override
    public Location getKey() {
        return this.location;
    }
}
