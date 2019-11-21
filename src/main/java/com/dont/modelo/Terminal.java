package com.dont.modelo;

import com.dont.modelo.bukkit.PlayerJoinQuit;
import com.dont.modelo.config.ConfigManager;
import com.dont.modelo.database.*;
import com.dont.modelo.models.database.User;
import com.dont.modelo.utils.Utils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Terminal extends JavaPlugin {

    private Economy economy;
    private ConfigManager configManager;
    private IDataSource dataSource;
    private DataManager dataManager;
    public void onEnable(){
        saveDefaultConfig();
        Utils.debug(Utils.LogType.INFO, "Plugin iniciado, by don't");
        Utils.DEBUGGING = getConfig().getBoolean("Database.Debug");
        if (getConfig().getString("Database.Tipo").equalsIgnoreCase("MONGODB")) dataSource = new MongoSource(getConfig().getString("Database.IP"), getConfig().getString("Database.DB"),getConfig().getString("Database.User"), getConfig().getString("Database.Pass"));
        else if (getConfig().getString("Database.Tipo").equalsIgnoreCase("MYSQL_POOLING")) dataSource = new MySQLPoolingSource(getConfig().getString("Database.IP"), getConfig().getString("Database.DB"),getConfig().getString("Database.User"), getConfig().getString("Database.Pass"));
        else if (getConfig().getString("Database.Tipo").equalsIgnoreCase("MYSQL_PURO")) dataSource = new MySQLNoPoolingSource(getConfig().getString("Database.IP"), getConfig().getString("Database.DB"),getConfig().getString("Database.User"), getConfig().getString("Database.Pass"));
        else dataSource = new SQLiteSource();
        if (dataSource == null || dataSource.isClosed()) return;
        dataManager = new DataManager(dataSource);
        setup();
    }

    public void onDisable(){
        Utils.debug(Utils.LogType.INFO, "Plugin desligado");
        if (dataSource == null) return;
        dataManager.getCached().forEach(storable -> dataSource.insert(storable, false));
        dataSource.close();
    }

    private void setup(){
        configManager = new ConfigManager(this);
        if (!setupEconomy()) {
            Bukkit.getConsoleSender().sendMessage("§eVault não encontrado");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        dataManager.deleteOldUsers();
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (dataSource.exists(player.getName())){
                User user = dataSource.find(player.getName(), User.class);
                user.setLastActivity(System.currentTimeMillis());
                dataManager.cache(user);
                Utils.debug(Utils.LogType.DEBUG, "puxando player "+player.getName()+" da tabela");
            } else {
                User user = new User(player.getName(), System.currentTimeMillis());
                dataManager.cache(user);
                Utils.debug(Utils.LogType.DEBUG, "criando player "+player.getName()+" na tabela");
            }
        });
        new AutoSave(this);
        new PlayerJoinQuit(this);
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

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public void reloadConfigManager(){
        this.configManager = new ConfigManager(this);
    }

    public DataManager getDataManager() {
        return dataManager;
    }
}
