package it.vamxneedev.strivelib.database.mysql;

import it.vamxneedev.strivelib.database.Database;
import it.vamxneedev.strivelib.database.DatabaseScheduler;
import it.vamxneedev.strivelib.utilities.database.Query;
import it.vamxneedev.strivelib.enums.DatabaseType;
import it.vamxneedev.strivelib.utilities.database.QueryUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySqlDatabase extends DatabaseScheduler implements Database {

    private final Plugin plugin;
    private MySqlConnection mySqlConnection;

    public MySqlDatabase(Plugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public DatabaseType getDatabaseType() {
        return DatabaseType.MYSQL;
    }

    @Override
    public Connection createConnection(String... data) {
        try {
            if(mySqlConnection != null && mySqlConnection.getConnection() != null && !this.mySqlConnection.getConnection().isClosed())
                return this.mySqlConnection.getConnection();
        } catch (SQLException ex) {
            return null;
        }
        MySqlConnection mySql = new MySqlConnection(data[0], data[1], data[2], data[3], data[4]);
        mySqlConnection = mySql;

        try {
            mySqlConnection.setConnection(mySql.openConnection());
            if(mySqlConnection.getConnection().isClosed())
                return null;
        } catch (SQLException ex) {
            return null;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public Connection createConnection(MySqlConfig mySqlConfig){
        return createConnection(mySqlConfig.getHost(), mySqlConfig.getPort(), mySqlConfig.getDatabaseName(), mySqlConfig.getUser(), mySqlConfig.getPassword());
    }

    @Override
    public Connection getConnection() {
        if(mySqlConnection == null) {
            Bukkit.getLogger().warning("[" + plugin.getName() + "/StellarXLib] MySQLConnection object is null. It must be initialized by method createConnection(data...).");
        }
        try {
            if(mySqlConnection.getConnection() != null && !this.mySqlConnection.getConnection().isClosed())
                return mySqlConnection.getConnection();
            else
                return createConnection(mySqlConnection);
        } catch (SQLException e) {
            return createConnection(mySqlConnection);
        }
    }

    @Override
    public Boolean isConnected() {
        if (mySqlConnection == null){
            Bukkit.getLogger().warning("[" + plugin.getName() + "/StellarXLib] MySQLConnection object is null. It must be initialized by method createConnection(data...).");
            return null;
        }
        if (mySqlConnection.getConnection() != null){
            try {
                return !mySqlConnection.getConnection().isClosed();
            } catch (SQLException e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public void closeConnection() {
        if (mySqlConnection.getConnection() != null){
            try {
                mySqlConnection.getConnection().close();
            } catch (SQLException e) {
                mySqlConnection.setConnection(null);
            }
        }
    }

    @Override
    public Boolean updateSync(Query query) {
        if (mySqlConnection == null){
            Bukkit.getLogger().warning("[" + plugin.getName() + "/StellarXLib] MySQLConnection object is null. It must be initialized by method createConnection(data...).");
            return null;
        }

        try {
            Statement statement = mySqlConnection.getConnection().createStatement();
            statement.execute(query.getQuery());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage("[StellarXLib] SQL query error: " +
                    e.getSQLState()
                    + " ---- " +
                    e.getCause()
                    + " ---- " +
                    e.getMessage()
                    + " ---- " +
                    e.getErrorCode() +
                    " ---- USED QUERY:"
                    + query);
        }
        return null;
    }

    @Override
    public void updateAsync(Query query) {
        if (mySqlConnection == null){
            Bukkit.getLogger().warning("[" + plugin.getName() + "/StellarXLib] MySQLConnection object is null. It must be initialized by method createConnection(data...).");
            return;
        }

        super.scheduleRunnable(new Runnable() {
            @Override
            public void run() {
                try {
                    Statement statement = mySqlConnection.getConnection().createStatement();
                    statement.execute(query.getQuery());
                } catch (SQLException e) {
                    e.printStackTrace();
                    Bukkit.getConsoleSender().sendMessage("[StellarXLib] SQL query error: " +
                            e.getSQLState()
                            + " ---- " +
                            e.getCause()
                            + " ---- " +
                            e.getMessage()
                            + " ---- " +
                            e.getErrorCode() +
                            " ---- USED QUERY:"
                            + query);
                }
            }
        });
    }

    @Override
    public ResultSet getResult(Query query) {
        if (mySqlConnection == null){
            Bukkit.getLogger().warning("[" + plugin.getName() + "/StellarXLib] MySQLConnection object is null. It must be initialized by method createConnection(data...).");
            return null;
        }

        ResultSet set = null;
        try {
            Statement statement = mySqlConnection.getConnection().createStatement();
            set = statement.executeQuery(query.getQuery());
        } catch (SQLException e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage("[StellarXLib] MySQL get data from query error: " + e.getErrorCode());
        }
        try {
            if(set != null)
                set.beforeFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return set;
    }

    @Override
    public void performMultipleQueriesSync(List<Query> listOfQueries) {
        for(Query q : listOfQueries) {
            updateSync(q);
        }
    }

    @Override
    public void performMultipleQueriesAsync(List<Query> listOfQueries) {
        for(Query q : listOfQueries) {
            updateAsync(q);
        }
    }

    @Override
    public void setObjectSync(String table, String keyColumn, Object keyValue, String objectColumn, Object objectValue, Boolean hasTablePrimaryKey) {
        List<String> queries = QueryUtils.constructQuerySingleValueSet(table, keyColumn, keyValue, objectColumn, objectValue, hasTablePrimaryKey, DatabaseType.MYSQL);
        for(String query : queries) {
            Query q = new Query(query);
            updateSync(q);
        }
    }

    @Override
    public void setObjectAsync(String table, String keyColumn, Object keyValue, String objectColumn, Object objectValue, Boolean hasTablePrimaryKey) {
        List<String> queries = QueryUtils.constructQuerySingleValueSet(table, keyColumn, keyValue, objectColumn, objectValue, hasTablePrimaryKey, DatabaseType.MYSQL);
        for(String query : queries) {
            Query q = new Query(query);
            updateAsync(q);
        }
    }

    @Override
    public void removeObjectAsync(String table, String keyColumn, Object keyValue, String objectColumn) {
        Query query = new Query(QueryUtils.constructQueryValueRemove(table, keyColumn, keyValue, objectColumn));
        this.updateAsync(query);
    }

    @Override
    public void removeObjectSync(String table, String keyColumn, Object keyValue, String objectColumn) {
        Query query = new Query(QueryUtils.constructQueryValueRemove(table, keyColumn, keyValue, objectColumn));
        this.updateSync(query);
    }

    @Override
    public ResultSet getRow(String table, String keyColumn, String keyValue) {
        Query query = new Query(QueryUtils.constructQueryRowGet(table, keyColumn, keyValue));
        return this.getResult(query);
    }

    @Override
    public ResultSet getRows(String table, String keyColumn, String keyValue, int limit) {
        Query query = new Query(QueryUtils.constructQueryRowsGet(table, keyColumn, keyValue, limit));
        return this.getResult(query);
    }

    @Override
    public void deleteRowSync(String table, String keyColumn, String keyValue) {
        Query query = new Query(QueryUtils.constructQueryRowRemove(table, keyColumn, keyValue, DatabaseType.MYSQL));
        this.updateSync(query);
    }

    @Override
    public void deleteRowAsync(String table, String keyColumn, String keyValue) {
        Query query = new Query(QueryUtils.constructQueryRowRemove(table, keyColumn, keyValue, DatabaseType.MYSQL));
        this.updateAsync(query);
    }

    @Override
    public void deleteRowsSync(String table, String keyColumn, String keyValue, int limit) {
        Query query = new Query(QueryUtils.constructQueryRowsRemove(table, keyColumn, keyValue, limit));
        this.updateSync(query);
    }

    @Override
    public void deleteRowsAsync(String table, String keyColumn, String keyValue, int limit) {
        Query query = new Query(QueryUtils.constructQueryRowsRemove(table, keyColumn, keyValue, limit));
        this.updateAsync(query);
    }
}
