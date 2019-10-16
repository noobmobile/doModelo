package com.dont.modelo.bukkit;

import com.dont.modelo.Terminal;
import com.dont.modelo.database.DataManager;
import com.dont.modelo.models.bukkit.DoListener;
import com.dont.modelo.models.database.User;
import com.dont.modelo.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinQuit extends DoListener {

    public PlayerJoinQuit(Terminal main) {
        super(main);
    }

    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent e) {
        if (!e.getLoginResult().equals(Result.ALLOWED))
            return;
        if (manager.cache.containsKey(e.getName()))
            return;
        if (manager.exists(e.getName())) {
            User user = manager.get(e.getName(), User.class);
            user.setLastActivity(System.currentTimeMillis());
            manager.cache.put(e.getName(), user);
            Utils.debug(Utils.LogType.DEBUG, "Pegando player " + e.getName() + " do mysql");
        } else {
            manager.cache.put(e.getName(), new User(e.getName(), System.currentTimeMillis()));
            Utils.debug(Utils.LogType.DEBUG, "Criando novo player no mysql " + e.getName());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (!manager.cache.containsKey(e.getPlayer().getName()))
            return;
        User user = (User) manager.cache.get(e.getPlayer().getName());
        user.setLastActivity(System.currentTimeMillis());
        manager.insert(user, true);
        manager.cache.remove(user.getName());
        user = null;
    }


}
