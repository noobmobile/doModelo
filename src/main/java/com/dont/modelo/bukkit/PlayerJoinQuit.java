package com.dont.modelo.bukkit;

import com.dont.modelo.Terminal;
import com.dont.modelo.models.bukkit.DoListener;
import com.dont.modelo.models.database.User;
import com.dont.modelo.utils.Utils;
import org.bukkit.entity.Player;
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
            Player player = e.getPlayer();
            User.load(player, main.getDataManager().USERS);
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        if (!manager.USERS.isCached(player.getName())) return;
        User user = manager.USERS.getCached(player.getName());
        manager.USERS.insert(user, true);
        manager.USERS.uncache(player.getName());
        Utils.debug(Utils.LogType.DEBUG, "salvando player " + player.getName() + " na tabela");
    }

}
