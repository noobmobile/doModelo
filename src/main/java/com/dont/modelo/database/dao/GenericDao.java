package com.dont.modelo.database.dao;

import com.dont.modelo.database.managers.EntityManager;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class GenericDao<K, V> {

    private Class<V> vClass;
    private String tableName;
    private EntityManager entityManager;
    private Function<K, String> keyAdapter;

    public GenericDao(GenericDao<K, V> other) {
        this(other.entityManager, other.tableName, other.vClass, other.keyAdapter);
    }

    public GenericDao(EntityManager entityManager, String tableName, Class<V> vClass) {
        this(entityManager, tableName, vClass, Objects::toString);
    }

    public GenericDao(EntityManager entityManager, String tableName, Class<V> vClass, Function<K, String> keyAdapter) {
        this.tableName = tableName;
        this.entityManager = entityManager;
        this.keyAdapter = keyAdapter;
        this.vClass = vClass;
        entityManager.createTable(this);
    }

    public void insert(K key, V value, boolean async) {
        this.entityManager.insert(keyAdapter.apply(key), value, async, tableName);
    }

    public void delete(K key, boolean async) {
        this.entityManager.delete(keyAdapter.apply(key), async, tableName);
    }

    public V find(K key) {
        return this.entityManager.find(keyAdapter.apply(key), tableName, vClass);
    }

    public List<V> findAll() {
        return this.entityManager.findAll(tableName, vClass);
    }

    public boolean exists(K key) {
        return this.entityManager.exists(keyAdapter.apply(key), tableName);
    }

    public String getTableName() {
        return tableName;
    }

}
