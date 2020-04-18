package com.dont.modelo.models.database;

import com.dont.modelo.database.DataManager;

public class User implements Storable{

    private final static long DELETE_TIME = 1000l * 60 * 60 * 24 * 14;

    private String name;
    private long lastActivity;

    public User(String name) {
        this.name = name;
        this.lastActivity = System.currentTimeMillis();
    }

    public boolean canBeDeleted(){
        return System.currentTimeMillis() > lastActivity + DELETE_TIME;
    }

    public long getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(long lastActivity) {
        this.lastActivity = lastActivity;
    }

    /**
     * atualiza o player na database caso não esteja no cache
     */
    public void offlineSave(DataManager manager){
        if (!manager.isCached(name)){
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
