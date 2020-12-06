package com.dont.modelo.database.datasources;

import com.dont.modelo.Terminal;
import com.dont.modelo.database.datamanagers.GenericDataManager;
import com.dont.modelo.database.exceptions.DatabaseException;
import org.bukkit.Warning;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Deprecated
@Warning(reason = "feito somente para estudo")
public class YamlDataSource extends AbstractDataSource {

    private YamlConfiguration yamlConfiguration;
    private File yamlConfigurationFile;

    public YamlDataSource() throws DatabaseException {
        openConnection();
    }

    private void openConnection() throws DatabaseException {
        try {
            this.yamlConfigurationFile = new File(Terminal.getInstance().getDataFolder(), "data.yml");
            if (!yamlConfigurationFile.exists()) {
                yamlConfigurationFile.createNewFile();
            }
            yamlConfiguration = YamlConfiguration.loadConfiguration(yamlConfigurationFile);
        } catch (Exception e) {
            throw new DatabaseException("não foi possivel iniciar conexão com banco de sqlite", e);
        }
    }

    private void save() {
        try {
            yamlConfiguration.save(yamlConfigurationFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public <V> void insert(String key, V value, boolean async, String tableName) {
        Runnable runnable = () -> {
            yamlConfiguration.set(tableName + "." + key, gson.toJson(value));
            save();
        };

        if (async) executor.submit(runnable);
        else runnable.run();
    }

    @Override
    public void delete(String key, boolean async, String tableName) {
        Runnable runnable = () -> {
            yamlConfiguration.set(tableName + "." + key, null);
            save();
        };

        if (async) executor.submit(runnable);
        else runnable.run();
    }

    @Override
    public <V> V find(String key, String tableName, Class<V> vClass) {
        String json = yamlConfiguration.getString(tableName + "." + key);
        if (json == null) return null;
        return gson.fromJson(json, vClass);
    }

    @Override
    public <V> List<V> findAll(String tableName, Class<V> vClass) {
        List<V> values = new ArrayList<>();
        ConfigurationSection configurationSection = yamlConfiguration.getConfigurationSection(tableName);
        if (configurationSection != null) {
            for (String key : configurationSection.getKeys(false)) {
                values.add(gson.fromJson(configurationSection.getString(key), vClass));
            }
        }
        return values;
    }

    @Override
    public boolean exists(String key, String tableName) {
        return yamlConfiguration.isSet(tableName + "." + key);
    }

    @Override
    public void createTable(GenericDataManager dao) {
    }

    @Override
    public void close() throws DatabaseException {
    }
}
