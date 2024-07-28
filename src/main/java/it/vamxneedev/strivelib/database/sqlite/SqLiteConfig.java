package it.vamxneedev.strivelib.database.sqlite;

import java.sql.Connection;

public class SqLiteConfig {
    private String fileLocation;
    public Connection connection;
    public SqLiteConfig(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public String getFileLocation() { return fileLocation; }
    public void setFileLocation(String fileLocation) { this.fileLocation = fileLocation; }

    public Connection getConnection() {
        return connection;
    }
    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
