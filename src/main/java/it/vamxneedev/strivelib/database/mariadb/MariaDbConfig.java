package it.vamxneedev.strivelib.database.mariadb;

import java.sql.Connection;

public class MariaDbConfig {
    private String host;
    private String port;
    private String database;
    private String username;
    private String password;

    public Connection connection;

    public MariaDbConfig(String host, String port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public String getHost() {
        return host;
    }
    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }
    public void setPort(String port) {
        this.port = port;
    }

    public String getDatabaseName() {
        return database;
    }
    public void setDatabaseName(String database) {
        this.database = database;
    }

    public String getUser() {
        return username;
    }
    public void setUser(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }


    public Connection getConnection() {
        return connection;
    }
    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
