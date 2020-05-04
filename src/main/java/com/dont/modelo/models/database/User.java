package com.dont.modelo.models.database;

import com.dont.modelo.database.DataManager;

public class User implements Storable {
    private String name;

    public User(String name) {
        this.name = name;
    }

    /**
     * atualiza o player na database caso não esteja no cache
     */
    public void offlineSave(DataManager manager) {
        if (!manager.isCached(name)) {
            manager.getDataSource().insert(this, true);
        }
    }

    @Override
    public String getKey() {
        return name;
    }

    public String getName() {
        return name;
    }
}
