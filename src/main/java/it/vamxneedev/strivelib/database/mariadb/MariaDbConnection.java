package it.vamxneedev.strivelib.database.mariadb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MariaDbConnection extends MariaDbConfig{

    public MariaDbConnection(String host, String port, String database, String username, String password) {
        super(host, port, database, username, password);

        try {
            super.connection = openConnection();
        } catch(SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public Connection openConnection() throws SQLException, ClassNotFoundException {
        if(checkConnection())
            return connection;

        String connectionURL = "jdbc:mariadb://" + super.getHost() + ":" + super.getPort();
        if (getDatabaseName() != null) {
            connectionURL = connectionURL + "/" + this.getDatabaseName();
        }

        Class.forName("org.mariadb.jdbc.Driver");
        connection = DriverManager.getConnection(connectionURL, super.getUser(), super.getPassword());
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

