package com.dont.modelo.models;

import com.dont.modelo.bukkit.PlayerJoinQuit;
import com.dont.modelo.config.ConfigManager;
import com.dont.modelo.database.AutoSave;
import com.dont.modelo.database.DataManager;
import com.dont.modelo.database.datasources.*;
import com.dont.modelo.utils.Utils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class AbstractTerminal extends JavaPlugin {

    private final Consumer<DataManager> loaderTyper;
    private Economy economy;
    private IDataSource dataSource;
    private ConfigManager configManager;
    private Map<Class<? extends Manager>, Manager> managers;

    public AbstractTerminal(Consumer<DataManager> loaderType) {
        this.loaderTyper = loaderType;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Utils.debug(Utils.LogType.INFO, "Plugin iniciado, by don't");
        Utils.DEBUGGING = getConfig().getBoolean("Database.Debug");
        this.managers = new HashMap<>();
        prepareDatabase();
        setup();
    }

    @Override
    public void onDisable() {
        Utils.debug(Utils.LogType.INFO, "Plugin desligado");
        saveAll();
    }

    private void setup() {
        if (dataSource == null || dataSource.isClosed()) return;
        this.configManager = new ConfigManager(this);
        preSetup();
        if (!setupEconomy()) {
            Bukkit.getConsoleSender().sendMessage("§eVault não encontrado");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        loadOnlinePlayers();
        new AutoSave(this);
        new PlayerJoinQuit(this);
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
        if (dataSource == null) return;
        getManager(DataManager.class).saveCached();
        dataSource.close();
    }

    private void prepareDatabase() {
        String databaseType = getConfig().getString("Database.Tipo");
        if (databaseType.equalsIgnoreCase("MONGODB")) {
            dataSource = new MongoSource(getConfig().getString("Database.IP"), getConfig().getString("Database.DB"), getConfig().getString("Database.User"), getConfig().getString("Database.Pass"));
        } else if (databaseType.equalsIgnoreCase("MYSQL_POOLING")) {
            dataSource = new MySQLPoolingSource(getConfig().getString("Database.IP"), getConfig().getString("Database.DB"), getConfig().getString("Database.User"), getConfig().getString("Database.Pass"));
        } else if (databaseType.equalsIgnoreCase("MYSQL_PURO")) {
            dataSource = new MySQLNoPoolingSource(getConfig().getString("Database.IP"), getConfig().getString("Database.DB"), getConfig().getString("Database.User"), getConfig().getString("Database.Pass"));
        } else {
            dataSource = new SQLiteSource();
        }
        registerManager(new DataManager(dataSource));
    }

    private void loadOnlinePlayers() {
        loaderTyper.accept(getManager(DataManager.class));
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
