package com.dont.modelo.bukkit;

import com.dont.modelo.Terminal;
import com.dont.modelo.models.bukkit.DoListener;
import com.dont.modelo.models.database.User;
import com.dont.modelo.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldLoadEvent;

public class PlayerJoinQuit extends DoListener {
    public PlayerJoinQuit(Terminal main) {
        super(main);
    }

    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent e){
        if (e.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) return;
        if (manager.isCached(e.getName())){
            ((User)manager.get(e.getName())).setLastActivity(System.currentTimeMillis());
        } else {
            if (manager.getDataSource().exists(e.getName())){
                User user = manager.getDataSource().find(e.getName(), User.class);
                user.setLastActivity(System.currentTimeMillis());
                manager.cache(user);
                Utils.debug(Utils.LogType.DEBUG, "puxando player "+e.getName()+" da tabela");
            } else {
                User user = new User(e.getName());
                manager.cache(user);
                Utils.debug(Utils.LogType.DEBUG, "criando player "+e.getName()+" na tabela");
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        if (!manager.isCached(e.getPlayer().getName())) return;
        User user = manager.get(e.getPlayer().getName());
        user.setLastActivity(System.currentTimeMillis());
        manager.getDataSource().insert(user, true);
        manager.uncache(e.getPlayer().getName());
        Utils.debug(Utils.LogType.DEBUG, "salvando player "+e.getPlayer()+" na tabela");
    }

}
