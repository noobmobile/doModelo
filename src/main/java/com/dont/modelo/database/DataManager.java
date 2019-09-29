package com.dont.modelo.database;

import com.dont.modelo.models.database.Storable;
import com.dont.modelo.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataManager {

    public final HashMap<String, Storable> cache;
    private final DataSource dataSource;
    private final Gson gson;
    private final ExecutorService executor;

    public DataManager(DataSource dataSource) {
        this.dataSource = dataSource;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.enableComplexMapKeySerialization();
        gson = gsonBuilder.create();
        cache = new HashMap<>();
        executor = Executors.newFixedThreadPool(5);
    }

    public boolean exists(String key) {
        try (Connection connection = dataSource.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `" + dataSource.tableName + "` WHERE `key` = ?");
            preparedStatement.setString(1, key);
            return preparedStatement.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public <T extends Storable> T get(String key, Class<? extends T> clazz) {
        try (Connection connection = dataSource.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `" + dataSource.tableName + "` WHERE `key` = ?");
            preparedStatement.setString(1, key);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return gson.fromJson(resultSet.getString("json"), clazz);
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void insert(Storable storable, boolean async) {
        Runnable runnable = () -> {
            try (Connection connection = dataSource.getConnection()){
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `" + dataSource.tableName + "`(`key`, `json`) VALUES (?, ?) ON DUPLICATE KEY UPDATE `json` = VALUES(`json`)");
                preparedStatement.setString(1, storable.getName());
                preparedStatement.setString(2, gson.toJson(storable));
                preparedStatement.executeUpdate();
                Utils.debug(Utils.LogType.DEBUG, "salvando " + storable.getClass().getSimpleName().toLowerCase() + " em " + (async ? "a" : "") + "sync na tabela");
            } catch (SQLException e) {
                e.printStackTrace();
                Utils.debug(Utils.LogType.DEBUG, "erro ao salvar " + storable.getClass().getSimpleName().toLowerCase() + " em " + (async ? "a" : "") + "sync na tabela");
            }
        };

        if (async) executor.submit(runnable);
        else runnable.run();

    }

}
