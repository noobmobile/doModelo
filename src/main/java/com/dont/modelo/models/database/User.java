package com.dont.modelo.models.database;

public class User implements Storable{

    private final static long DELETE_TIME = 1000l * 60 * 60 * 24 * 14;

    private String name;
    private long lastActivity;

    public User(String name, long lastActivity) {
        this.name = name;
        this.lastActivity = lastActivity;
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

    @Override
    public String getName() {
        return name;
    }

}
