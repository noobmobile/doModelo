package com.dont.modelo.database;

import com.dont.modelo.models.database.Storable;
import com.dont.modelo.models.database.User;
import com.dont.modelo.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public List<User> getOfflineUsers(){
        List<User> users = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `"+dataSource.tableName+"`");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                if (cache.containsKey(resultSet.getString("key"))) continue;
                users.add(gson.fromJson( resultSet.getString("json"), User.class));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public void deleteOldUsers(){
        Utils.measureTime("deletado usuarios em <tempo>ms", () -> {
            int deletado = 0;
            for (User user : getOfflineUsers()){
                if (user.canBeDeleted()){
                    delete(user.getName(), true);
                    deletado++;
                }
            }
            System.out.println("deletado "+deletado+" usuarios");
        });
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

    public void delete(String key, boolean async){
        Runnable runnable = () -> {
            try (Connection connection = dataSource.getConnection()){
                PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM `"+dataSource.tableName+"` WHERE `key` = ?");
                preparedStatement.setString(1, key);
                preparedStatement.executeUpdate();
            } catch (SQLException e){
                e.printStackTrace();
            }
        };

        if (async) executor.submit(runnable);
        else runnable.run();

    }

}
