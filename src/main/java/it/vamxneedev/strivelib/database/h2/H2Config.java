package it.vamxneedev.strivelib.database.h2;

import java.sql.Connection;

public class H2Config {
    private String fileLocation;
    private String username;
    private String password;

    public Connection connection;
    public H2Config(String fileLocation, String username, String password) {
        this.fileLocation = fileLocation;
        this.username = username;
        this.password = password;
    }

    public String getFileLocation() { return this.fileLocation; }
    public void setFileLocation(String fileLocation) { this.fileLocation = fileLocation; }

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
