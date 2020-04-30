package com.dont.modelo.database.datasources;

import com.dont.modelo.models.database.Storable;

import java.util.List;

public interface IDataSource {

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
