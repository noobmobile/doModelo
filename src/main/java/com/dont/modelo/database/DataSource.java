package com.dont.modelo.database;

import com.dont.modelo.Terminal;
import com.dont.modelo.utils.Utils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DataSource {

    public final String tableName = "dont.modelo";
    private HikariDataSource dataSource;

    public DataSource(Terminal main, String ip, String user, String database, String password) {
        String url = "jdbc:mysql://" + ip + "/" + database + "?autoReconnect=true";
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(user);
        hikariConfig.setPassword(password);
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        try {
            dataSource = new HikariDataSource(hikariConfig);
            createTables();
            Utils.debug(Utils.LogType.INFO, "mysql ligado com sucesso");
        } catch (Exception e) {
            Utils.debug(Utils.LogType.INFO,"nao foi possivel ligar mysql: " + e.getLocalizedMessage());
            Bukkit.getPluginManager().disablePlugin(main);
        }
    }

    public boolean isClosed(){
        return dataSource == null;
    }

    public void close(){
        dataSource.close();
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null) throw new SQLException("mysql nao ligado");
        return dataSource.getConnection();
    }

    public void createTables() throws SQLException {
        try (Connection connection = getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + tableName + "`(`key` VARCHAR(16) NOT NULL, `json` TEXT NOT NULL, PRIMARY KEY (`key`))");
            preparedStatement.executeUpdate();
        } catch (SQLException e){
            throw e;
        }
    }

}
