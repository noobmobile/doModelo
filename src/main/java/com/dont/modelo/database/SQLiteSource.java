package com.dont.modelo.database;

import com.dont.modelo.Terminal;
import com.dont.modelo.models.database.Storable;
import com.dont.modelo.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SQLiteSource implements IDataSource{

    private final String tableName = "dont.modelo";
    private final Gson gson;
    private final ExecutorService executor;
    private Connection connection;

    public SQLiteSource() {
        this.executor = Executors.newFixedThreadPool(3);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.enableComplexMapKeySerialization();
        this.gson = gsonBuilder.create();
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + Terminal.getPlugin(Terminal.class).getDataFolder().getPath() + "/database.db");
            createTables();
            Utils.debug(Utils.LogType.INFO, "conexao com sqlite estabelecida");
        } catch (Exception e) {
            System.out.println("nao foi possivel conexao com sqlite: "+e.getMessage());
            Bukkit.getPluginManager().disablePlugin(Terminal.getPlugin(Terminal.class));
        }

    }

    private void createTables() throws SQLException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `"+tableName+"`(`key` VARCHAR(16) NOT NULL, `json` TEXT NOT NULL, PRIMARY KEY (`key`))");
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw e;
        }
    }

    @Override
    public <T extends Storable> T find(String key, Class<T> clazz) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `"+tableName+"` WHERE `key` = ?");
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
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT OR REPLACE INTO `"+tableName+"`(`key`, `json`) VALUES (?, ?)");
                preparedStatement.setString(1, storable.getName());
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
                PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM `"+tableName+"` WHERE `key` = ?");
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
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `"+tableName+"`");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                try {
                    toReturn.add(gson.fromJson(resultSet.getString("json"), clazz));
                } catch (JsonSyntaxException e){
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
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `"+tableName+"` WHERE `key` = ?");
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