package com.dont.modelo.database.datasources;

import com.dont.modelo.database.adapters.ItemStackAdapter;
import com.dont.modelo.database.adapters.LocationAdapter;
import com.dont.modelo.database.datamanagers.GenericDataManager;
import com.dont.modelo.database.exceptions.DatabaseException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractDataSource {

    protected ExecutorService executor;
    protected Gson gson;

    public AbstractDataSource() throws DatabaseException {
        this.executor = Executors.newFixedThreadPool(3);
        this.gson = buildGson();
    }

    private Gson buildGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.enableComplexMapKeySerialization();
        gsonBuilder.registerTypeAdapter(Location.class, new LocationAdapter());
        gsonBuilder.registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter());
        return gsonBuilder.create();
    }

    public abstract <V> void insert(String key, V value, boolean async, String tableName);

    public abstract void delete(String key, boolean async, String tableName);

    public abstract <V> V find(String key, String tableName, Class<V> vClass);

    public abstract <V> List<V> findAll(String tableName, Class<V> vClass);

    public abstract boolean exists(String key, String tableName);

    public abstract void createTable(GenericDataManager dao);

    public abstract void close() throws DatabaseException;

}
