package com.dont.modelo.database.datamanagers;

import com.dont.modelo.database.Keyable;
import com.dont.modelo.database.datasources.AbstractDataSource;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class GenericDataManager<K, V extends Keyable<K>> {

    private Class<V> vClass;
    private String tableName;
    private AbstractDataSource abstractDataSource;
    private Function<K, String> keyAdapter;

    public GenericDataManager(AbstractDataSource abstractDataSource, String tableName, Class<V> vClass) {
        this(abstractDataSource, tableName, vClass, Objects::toString);
    }

    public GenericDataManager(AbstractDataSource abstractDataSource, String tableName, Class<V> vClass, Function<K, String> keyAdapter) {
        this.tableName = tableName;
        this.abstractDataSource = abstractDataSource;
        this.keyAdapter = keyAdapter;
        this.vClass = vClass;
        abstractDataSource.createTable(this);
    }

    public void insert(V value, boolean async) {
        this.abstractDataSource.insert(keyAdapter.apply(value.getKey()), value, async, tableName);
    }

    public void delete(K key, boolean async) {
        this.abstractDataSource.delete(keyAdapter.apply(key), async, tableName);
    }

    public V find(K key) {
        return this.abstractDataSource.find(keyAdapter.apply(key), tableName, vClass);
    }

    public List<V> findAll() {
        return this.abstractDataSource.findAll(tableName, vClass);
    }

    public boolean exists(K key) {
        return this.abstractDataSource.exists(keyAdapter.apply(key), tableName);
    }

    public String getTableName() {
        return tableName;
    }

}
