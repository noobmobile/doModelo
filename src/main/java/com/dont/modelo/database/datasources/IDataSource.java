package com.dont.modelo.database.datasources;

import com.dont.modelo.database.adapters.ItemStackAdapter;
import com.dont.modelo.database.adapters.LocationAdapter;
import com.dont.modelo.models.database.Storable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public interface IDataSource {

    public default ExecutorService getExecutorService() {
        return Executors.newFixedThreadPool(3);
    }

    public default Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.enableComplexMapKeySerialization();
        gsonBuilder.registerTypeAdapter(Location.class, new LocationAdapter());
        gsonBuilder.registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter());
        return gsonBuilder.create();
    }

    public default String getTableName() {
        return "dont.modelo";
    }

    /**
     * @param key   key que esteja a procurar
     * @param clazz classe que queira que retorne
     * @return storable desejado
     */
    public <T extends Storable> T find(String key, Class<T> clazz);

    public void insert(Storable storable, boolean async);

    public void delete(String key, boolean async);

    /**
     * @param clazz classe que queira que retorne
     * @return lista de storables desejado
     */
    public <T extends Storable> List<T> getAll(Class<T> clazz);

    /**
     * @param key
     * @return se existe na database
     */
    public boolean exists(String key);

    public void close();

    public boolean isClosed();

}
