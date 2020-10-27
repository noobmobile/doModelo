package com.dont.modelo.models.database;

import com.dont.modelo.database.Keyable;
import com.dont.modelo.database.datamanagers.CachedDataManager;
import com.dont.modelo.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class User implements Keyable<String> {
    private String name;

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static void loadAll(CachedDataManager<String, User> dao) {
        Utils.measureTime(() -> {
            int i = 0;
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (dao.isCached(player.getName())) continue;
                load(player, dao);
                i++;
            }
            return "Carregado " + i + " objetos em {time}";
        });
    }

    public static void load(Player player, CachedDataManager<String, User> dao) {
        if (dao.exists(player.getName())) {
            User user = dao.find(player.getName());
            dao.cache(user);
            Utils.debug(Utils.LogType.DEBUG, "puxando player " + player.getName() + " da tabela");
        } else {
            User user = new User(player.getName());
            dao.cache(user);
            Utils.debug(Utils.LogType.DEBUG, "criando player " + player.getName() + " na tabela");
        }
    }

    @Override
    public String getKey() {
        return this.name;
    }
}
