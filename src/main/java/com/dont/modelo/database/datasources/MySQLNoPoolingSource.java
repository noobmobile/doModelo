package com.dont.modelo.database.datasources;

import com.dont.modelo.Terminal;
import com.dont.modelo.database.adapters.ItemStackAdapter;
import com.dont.modelo.database.adapters.LocationAdapter;
import com.dont.modelo.models.database.Storable;
import com.dont.modelo.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MySQLNoPoolingSource implements IDataSource {

    private final String tableName = "dont.modelo";
    private final Gson gson;
    private final ExecutorService executor;
    private Connection connection;

    public MySQLNoPoolingSource(String ip, String database, String user, String password) {
        this.executor = Executors.newFixedThreadPool(3);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.enableComplexMapKeySerialization();
        gsonBuilder.registerTypeAdapter(Location.class, new LocationAdapter());
        gsonBuilder.registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter());
        this.gson = gsonBuilder.create();

        String url = "jdbc:mysql://" + ip + "/" + database + "?autoReconnect=true";
        try {
            connection = DriverManager.getConnection(url, user, password);
            createTables();
            Utils.debug(Utils.LogType.INFO, "conexao com mysql estabelecida");
        } catch (Exception e) {
            System.out.println("nao foi possivel conexao com mysql: " + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(Terminal.getPlugin(Terminal.class));
        }

    }

    private void createTables() throws SQLException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + tableName + "`(`key` VARCHAR(16) NOT NULL, `json` TEXT NOT NULL, PRIMARY KEY (`key`))");
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw e;
        }
    }

    @Override
    public <T extends Storable> T find(String key, Class<T> clazz) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `" + tableName + "` WHERE `key` = ?");
            preparedStatement.setString(1, key);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return gson.fromJson(resultSet.getString("json"), clazz);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void insert(Storable storable, boolean async) {
        Runnable runnable = () -> {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `" + tableName + "`(`key`, `json`) VALUES (?, ?) ON DUPLICATE KEY UPDATE `json` = VALUES(`json`)");
                preparedStatement.setString(1, storable.getKey());
                preparedStatement.setString(2, gson.toJson(storable));
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        };

        if (async) executor.submit(runnable);
        else runnable.run();
    }

    @Override
    public void delete(String key, boolean async) {
        Runnable runnable = () -> {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM `" + tableName + "` WHERE `key` = ?");
                preparedStatement.setString(1, key);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        };

        if (async) executor.submit(runnable);
        else runnable.run();
    }

    @Override
    public <T extends Storable> List<T> getAll(Class<T> clazz) {
        List<T> toReturn = new ArrayList<T>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `" + tableName + "`");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                try {
                    T storable = gson.fromJson(resultSet.getString("json"), clazz);
                    if (storable == null || storable.getKey() == null) continue;
                    toReturn.add(storable);
                } catch (JsonSyntaxException e) {
                    continue;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    @Override
    public boolean exists(String key) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `" + tableName + "` WHERE `key` = ?");
            preparedStatement.setString(1, key);
            return preparedStatement.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isClosed() {
        try {
            return connection == null || connection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

}
