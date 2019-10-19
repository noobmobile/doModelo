package com.dont.modelo.database;

import com.dont.modelo.models.database.Storable;

import java.util.List;

public interface IDataSource {

    /**
     *
     * @param key
     * @return storable salvo no database
     */
    public <T extends Storable> T find(String key, Class<T> clazz);

    public void insert(Storable storable, boolean async);
    public void delete(String key, boolean async);
    /**
     * @return tudo que não está no cache
     */
    public <T extends Storable> List<T> getAll(Class<T> clazz);
    /**
     *
     * @param key
     * @return se existe na database
     */
    public boolean exists(String key);
    public void close();
    public boolean isClosed();
}
