package it.vamxneedev.strivelib.database.h2;

import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2Connection extends H2Config {

    public H2Connection(String fileLocation, String username, String password) {
        super(fileLocation, username, password);
        try {
            super.connection = openConnection();
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public Connection openConnection() throws SQLException, ClassNotFoundException {
        if(checkConnection())
            return connection;

        String connectionURL = "jdbc:h2:file:" + super.getFileLocation();
        Class.forName("org.h2.Driver");
        connection = DriverManager.getConnection(connectionURL, super.getUser(), super.getPassword());

        if (connection == null) {
            Bukkit.getLogger().info(" Creating H2 connection to file failed. " +
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
