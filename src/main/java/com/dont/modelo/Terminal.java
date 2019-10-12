package com.dont.modelo;

import com.dont.modelo.bukkit.PlayerJoinQuit;
import com.dont.modelo.config.DoConfig;
import com.dont.modelo.database.AutoSave;
import com.dont.modelo.database.DataManager;
import com.dont.modelo.database.DataSource;
import com.dont.modelo.config.ConfigManager;
import com.dont.modelo.models.database.Storable;
import com.dont.modelo.models.database.User;
import com.dont.modelo.utils.Utils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Terminal extends JavaPlugin {

    private DataSource dataSource;
    private DataManager dataManager;
    private ConfigManager configManager;
    private Economy economy;

    public void onEnable() {
        saveDefaultConfig();
        Utils.debug(Utils.LogType.INFO, "Plugin iniciado, by don't");
        Utils.DEBUGGING = getConfig().getBoolean("MySQL.Debug");
        System.out.println("Modo debug: "+Utils.DEBUGGING);
        this.configManager = new ConfigManager(this);
        this.dataSource = new DataSource(this, getConfig().getString("MySQL.IP"), getConfig().getString("MySQL.User"), getConfig().getString("MySQL.DB"), getConfig().getString("MySQL.Pass"));
        if (dataSource.isClosed()) return;
        this.dataManager = new DataManager(dataSource);
        new AutoSave(this);
        setup();
    }

    private void setup() {
        if (!setupEconomy()) {
            Bukkit.getConsoleSender().sendMessage("§eVault não encontrado");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (dataManager.exists(player.getName())) {
                User user = dataManager.get(player.getName(), User.class);
                dataManager.cache.put(player.getName(), user);
                Utils.debug(Utils.LogType.DEBUG, "Pegando player " + player.getName() + " do mysql");
            } else {
                dataManager.cache.put(player.getName(), new User(player.getName()));
                Utils.debug(Utils.LogType.DEBUG, "Criando novo player no mysql " + player.getName());
            }
        }
        new PlayerJoinQuit(this);
    }


    public void onDisable() {
        if (dataSource == null) return;
        Utils.debug(Utils.LogType.INFO, "Plugin desligado");
        for (Storable storable : dataManager.cache.values()) {
            dataManager.insert(storable, false);
        }
        dataSource.close();
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public void reloadConfigManager(){
        this.configManager = new ConfigManager(this);
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
        return (economy != null);
    }

    public Economy getEconomy() {
        return economy;
    }

}
