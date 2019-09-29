package com.dont.modelo.models.database;

public class User implements Storable{

    private String name;

    public User(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

}
