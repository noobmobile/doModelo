package com.dont.modelo.database;

import com.dont.modelo.database.datamanagers.CachedDataManager;
import com.dont.modelo.database.datasources.AbstractDataSource;
import com.dont.modelo.models.database.Maquina;
import com.dont.modelo.models.database.User;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class MainDataManager {

    public final CachedDataManager<String, User> USERS;
    public final CachedDataManager<Location, Maquina> MAQUINAS;

    private List<CachedDataManager> daos;

    public MainDataManager(AbstractDataSource abstractDataSource) {
        this.daos = new ArrayList<>();
        daos.add(USERS = new CachedDataManager<>(abstractDataSource, "dont.modelo", User.class));
        daos.add(MAQUINAS = new CachedDataManager<>(abstractDataSource, "dont.maquinas", Maquina.class,
                location -> location.getWorld().getName() + ";" + location.getBlockX() + ";" + location.getBlockY() + ";" + location.getBlockZ()));

    }

    public int saveCached(boolean async) {
        daos.forEach(dao -> dao.saveCached(async));
        return daos.stream().mapToInt(dao -> dao.getCached().size()).sum();
    }
}
