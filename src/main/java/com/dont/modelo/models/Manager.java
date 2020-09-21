package com.dont.modelo.models;

public abstract class Manager {

    protected AbstractTerminal main;

    public Manager(AbstractTerminal main) {
        this.main = main;
    }

}
