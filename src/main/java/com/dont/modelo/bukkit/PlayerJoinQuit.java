package com.dont.modelo.bukkit;

import com.dont.modelo.Terminal;
import com.dont.modelo.models.bukkit.DoListener;
import com.dont.modelo.models.database.User;
import com.dont.modelo.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinQuit extends DoListener {
    public PlayerJoinQuit(Terminal main) {
        super(main);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Utils.async(() -> {
            if (manager.getDataSource().exists(e.getPlayer().getName())) {
                User user = manager.getDataSource().find(e.getPlayer().getName(), User.class);
                manager.cache(user);
                Utils.debug(Utils.LogType.DEBUG, "puxando player " + e.getPlayer().getName() + " da tabela");
            } else {
                User user = new User(e.getPlayer().getName());
                manager.cache(user);
                Utils.debug(Utils.LogType.DEBUG, "criando player " + e.getPlayer().getName() + " na tabela");
            }
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (!manager.isCached(e.getPlayer().getName())) return;
        User user = manager.get(e.getPlayer().getName());
        manager.getDataSource().insert(user, true);
        manager.uncache(e.getPlayer().getName());
        Utils.debug(Utils.LogType.DEBUG, "salvando player " + e.getPlayer().getName() + " na tabela");
    }

}
