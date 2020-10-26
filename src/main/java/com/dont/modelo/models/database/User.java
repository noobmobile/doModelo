package com.dont.modelo.models.database;

import com.dont.modelo.database.dao.CachedDao;
import com.dont.modelo.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;

public class User {
    private String name;

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static void loadAll(CachedDao<String, User> dao) {
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

    public static void load(Player player, CachedDao<String, User> dao) {
        if (dao.exists(player.getName())) {
            User user = dao.find(player.getName());
            dao.cache(player.getName(), user);
            Utils.debug(Utils.LogType.DEBUG, "puxando player " + player.getName() + " da tabela");
        } else {
            User user = new User(player.getName());
            dao.cache(player.getName(), user);
            Utils.debug(Utils.LogType.DEBUG, "criando player " + player.getName() + " na tabela");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(name, user.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
