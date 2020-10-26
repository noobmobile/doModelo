package com.dont.modelo.database;

import com.dont.modelo.models.AbstractTerminal;
import com.dont.modelo.utils.Utils;
import org.bukkit.scheduler.BukkitRunnable;

public class AutoSave extends BukkitRunnable {

    private final AbstractTerminal main;

    public AutoSave(AbstractTerminal main) {
        this.main = main;
        runTaskTimerAsynchronously(main, 20L * 60 * 30, 20L * 60 * 30);
    }

    @Override
    public void run() {
        Utils.debug(Utils.LogType.DEBUG, "Iniciando auto save");
        long before = System.currentTimeMillis();
        int i = main.getDataManager().saveCached(false);
        long now = System.currentTimeMillis();
        long total = now - before;
        Utils.debug(Utils.LogType.INFO, "Auto completo, salvo " + i + " objetos em " + total + "ms");
    }

}