package it.vamxneedev.strivelib.database.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySqlConnection extends MySqlConfig{
    public MySqlConnection(String host, String port, String databaseName, String user, String password) {
        super(host, port, databaseName, user, password);
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

        String connectionURL = "jdbc:mysql://" + super.getHost() + ":" + super.getPort();
        if (getDatabaseName() != null) {
            connectionURL = connectionURL + "/" + this.getDatabaseName();
        }

        Class.forName("com.mysql.jdbc.Driver");
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
