package com.dont.modelo.bukkit;

import com.dont.modelo.models.AbstractTerminal;
import com.dont.modelo.models.bukkit.DoListener;
import com.dont.modelo.models.database.User;
import com.dont.modelo.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinQuit extends DoListener {
    public PlayerJoinQuit(AbstractTerminal main) {
        super(main);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Utils.async(() -> {
            Player player = e.getPlayer();
            if (manager.getDataSource().exists(player.getName())) {
                User user = manager.getDataSource().find(player.getName(), User.class);
                manager.cache(user);
                Utils.debug(Utils.LogType.DEBUG, "puxando player " + player.getName() + " da tabela");
            } else {
                User user = new User(player.getName());
                manager.cache(user);
                Utils.debug(Utils.LogType.DEBUG, "criando player " + player.getName() + " na tabela");
            }
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        if (!manager.isCached(player.getName())) return;
        User user = manager.get(player.getName());
        manager.getDataSource().insert(user, true);
        manager.uncache(player.getName());
        Utils.debug(Utils.LogType.DEBUG, "salvando player " + player.getName() + " na tabela");
    }

}
