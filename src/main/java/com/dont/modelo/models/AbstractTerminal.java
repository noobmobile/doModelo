package com.dont.modelo.models;

import com.dont.modelo.config.ConfigManager;
import com.dont.modelo.database.AutoSave;
import com.dont.modelo.database.MainDataManager;
import com.dont.modelo.database.datasources.AbstractDataSource;
import com.dont.modelo.database.datasources.HikariDataSource;
import com.dont.modelo.database.datasources.MySQLDataSource;
import com.dont.modelo.database.datasources.SQLLiteDataSource;
import com.dont.modelo.database.exceptions.DatabaseException;
import com.dont.modelo.utils.Utils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractTerminal extends JavaPlugin {

    private Economy economy;
    private ConfigManager configManager;
    private Map<Class<? extends Manager>, Manager> managers;
    protected AbstractDataSource abstractDataSource;
    private MainDataManager mainDataManager;


    @Override
    public void onEnable() {
        saveDefaultConfig();
        Utils.debug(Utils.LogType.INFO, "Plugin iniciado, by don't");
        Utils.DEBUGGING = getConfig().getBoolean("Database.Debug");
        this.managers = new HashMap<>();
        if (!prepareDatabase()) return;
        setup();
    }

    @Override
    public void onDisable() {
        Utils.debug(Utils.LogType.INFO, "Plugin desligado");
        disable();
        saveAll();
    }

    protected abstract void disable();

    private void setup() {
        this.configManager = new ConfigManager(this);
        preSetup();
        if (!setupEconomy()) {
            Bukkit.getConsoleSender().sendMessage("§eVault não encontrado");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        new AutoSave(this);
        posSetup();
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
        return (economy != null);
    }

    private void saveAll() {
        try {
            if (abstractDataSource == null) return;
            mainDataManager.saveCached(false);
            abstractDataSource.close();
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    private boolean prepareDatabase() {
        try {
            String databaseType = getConfig().getString("Database.Tipo");
            if (databaseType.equalsIgnoreCase("MYSQL_POOLING")) {
                abstractDataSource = new HikariDataSource(getConfig().getString("Database.IP"), getConfig().getString("Database.DB"), getConfig().getString("Database.User"), getConfig().getString("Database.Pass"));
            } else if (databaseType.equalsIgnoreCase("MYSQL_PURO")) {
                abstractDataSource = new MySQLDataSource(getConfig().getString("Database.IP"), getConfig().getString("Database.DB"), getConfig().getString("Database.User"), getConfig().getString("Database.Pass"));
            } else {
                abstractDataSource = new SQLLiteDataSource();
            }
            this.mainDataManager = new MainDataManager(abstractDataSource);
            return true;
        } catch (DatabaseException e) {
            Utils.debug(Utils.LogType.INFO, "erro ao inicializar conexão com banco de dados");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }
        return false;
    }

    public MainDataManager getDataManager() {
        return mainDataManager;
    }

    public void reloadConfigManager() {
        this.configManager = new ConfigManager(this);
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public Economy getEconomy() {
        return economy;
    }

    public <T extends Manager> T getManager(Class<T> clazz) {
        return (T) managers.get(clazz);
    }

    protected void registerManager(Manager manager) {
        managers.put(manager.getClass(), manager);
    }

    protected abstract void preSetup();

    protected abstract void posSetup();

    public static <T extends AbstractTerminal> T getInstance() {
        return (T) AbstractTerminal.getPlugin(AbstractTerminal.class);
    }

}
