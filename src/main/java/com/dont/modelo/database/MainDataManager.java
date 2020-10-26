package com.dont.modelo.database;

import com.dont.modelo.database.dao.CachedDao;
import com.dont.modelo.database.dao.GenericDao;
import com.dont.modelo.database.managers.EntityManager;
import com.dont.modelo.models.database.User;

import java.util.ArrayList;
import java.util.List;

public class MainDataManager {

    public final CachedDao<String, User> USERS;

    private final EntityManager entityManager;
    private List<CachedDao> daos;

    public MainDataManager(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.daos = new ArrayList<>();
        daos.add(USERS = new CachedDao<>(new GenericDao<>(entityManager, "dont.modelo", User.class)));

    }

    public int saveCached(boolean async) {
        daos.forEach(dao -> dao.saveCached(async));
        return daos.stream().mapToInt(dao -> dao.getCached().size()).sum();
    }
}
