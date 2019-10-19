package com.dont.modelo.database;

import com.dont.modelo.Terminal;
import com.dont.modelo.models.database.Storable;
import com.dont.modelo.utils.Utils;
import org.bukkit.scheduler.BukkitRunnable;

public class AutoSave extends BukkitRunnable {

    private final Terminal main;
    public AutoSave(Terminal main) {
        this.main = main;
        runTaskTimerAsynchronously(main, 20l*60*10, 20l*60*10);
    }

    @Override
    public void run() {
        Utils.debug(Utils.LogType.DEBUG, "Iniciando auto save");
        long before = System.currentTimeMillis();
        int i = 0;
        for (Storable storable : main.getDataManager().getCached()) {
            main.getDataManager().getDataSource().insert(storable, true);
            i++;
        }
        long now = System.currentTimeMillis();
        long total = now-before;
        Utils.debug(Utils.LogType.INFO, "Auto completo, salvo "+i+" objetos em "+total+"ms");
    }

}