package com.dont.modelo.database.managers;

import com.dont.modelo.Terminal;
import com.dont.modelo.database.dao.GenericDao;
import com.dont.modelo.database.exceptions.DatabaseException;
import com.dont.modelo.utils.Utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLLiteEntityManager extends EntityManager {

    private Connection connection;

    public SQLLiteEntityManager() throws DatabaseException {
        openConnection();
    }

    private void openConnection() throws DatabaseException {
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + Terminal.getInstance().getDataFolder().getPath() + "/database.db");
            Utils.debug(Utils.LogType.INFO, "conexão com sqlite inicializada com sucesso");
        } catch (Exception e) {
            throw new DatabaseException("não foi possivel iniciar conexão com banco de sqlite", e);
        }
    }

    @Override
    public <V> void insert(String key, V value, boolean async, String tableName) {
        Runnable runnable = () -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT OR REPLACE INTO `" + tableName + "`(`key`, `json`) VALUES(?, ?)")) {
                preparedStatement.setString(1, key);
                preparedStatement.setString(2, gson.toJson(value));
                preparedStatement.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        if (async) executor.submit(runnable);
        else runnable.run();
    }

    @Override
    public void delete(String key, boolean async, String tableName) {
        Runnable runnable = () -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM `" + tableName + "` WHERE `key` = ?")) {
                preparedStatement.setString(1, key);
                preparedStatement.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        if (async) executor.submit(runnable);
        else runnable.run();
    }

    @Override
    public <V> V find(String key, String tableName, Class<V> vClass) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `" + tableName + "` WHERE `key` = ?")) {
            preparedStatement.setString(1, key);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return gson.fromJson(resultSet.getString("json"), vClass);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <V> List<V> findAll(String tableName, Class<V> vClass) {
        List<V> values = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `" + tableName + "`")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                values.add(gson.fromJson(resultSet.getString("json"), vClass));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    @Override
    public boolean exists(String key, String tableName) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `" + tableName + "` WHERE `key` = ?")) {
            preparedStatement.setString(1, key);
            return preparedStatement.executeQuery().next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void createTable(GenericDao dao) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + dao.getTableName() + "`(`key` VARCHAR(64) NOT NULL, `json` TEXT NOT NULL, PRIMARY KEY (`key`))")) {
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws DatabaseException {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new DatabaseException("não foi possivel fechar conexão com mysql", e);
        }
    }
}
