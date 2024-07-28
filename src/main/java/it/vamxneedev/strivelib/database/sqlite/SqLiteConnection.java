package it.vamxneedev.strivelib.database.sqlite;

import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqLiteConnection extends SqLiteConfig{
    public SqLiteConnection(String fileLocation) {
        super(fileLocation);
        try {
            super.connection = openConnection();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Connection openConnection() throws SQLException, ClassNotFoundException {
        if (checkConnection()) {
            return connection;
        }
        String connectionURL = "jdbc:sqlite:" + super.getFileLocation();
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection(connectionURL);

        if (connection == null) {
            Bukkit.getLogger().info(" Creating SQLite connection to file failed. " +
                    "Check if given location is correct and required access is given: " + super.getFileLocation() + "");
        }
        return connection;
    }

    public boolean closeConnection() throws SQLException {
        if (connection == null) {
            return false;
        }
        connection.close();
        return true;
    }

    public boolean checkConnection() throws SQLException {
        return connection != null && !connection.isClosed();
    }
    public Connection getConnection() {
        return connection;
    }
}
