package it.vamxneedev.strivelib.database.h2;

import it.vamxneedev.strivelib.database.Database;
import it.vamxneedev.strivelib.database.DatabaseScheduler;
import it.vamxneedev.strivelib.enums.DatabaseType;
import it.vamxneedev.strivelib.utilities.database.Query;
import it.vamxneedev.strivelib.utilities.database.QueryUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class H2Database extends DatabaseScheduler implements Database {
    private final Plugin plugin;
    private H2Connection h2Connection;

    public H2Database(Plugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public DatabaseType getDatabaseType() {
        return DatabaseType.H2;
    }

    @Override
    public Connection createConnection(String... data) {
        try {
            if(h2Connection != null && h2Connection.getConnection() != null && !this.h2Connection.getConnection().isClosed())
                return this.h2Connection.getConnection();
        } catch (SQLException ex) {
            return null;
        }
        H2Connection h2 = new H2Connection(data[0], data[1], data[2]);
        h2Connection = h2;

        try {
            h2Connection.setConnection(h2.openConnection());
            if(h2Connection.getConnection().isClosed())
                return null;
        } catch (SQLException ex) {
            return null;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public Connection createConnection(H2Config h2Config){
        return createConnection(h2Config.getFileLocation(), h2Config.getUser(), h2Config.getPassword());
    }

    @Override
    public Connection getConnection() {
        if(h2Connection == null) {
            Bukkit.getLogger().warning("[" + plugin.getName() + "/StellarXLib] H2Connection object is null. It must be initialized by method createConnection(data...).");
        }
        try {
            if(h2Connection.getConnection() != null && !this.h2Connection.getConnection().isClosed())
                return h2Connection.getConnection();
            else
                return createConnection(h2Connection);
        } catch (SQLException e) {
            return createConnection(h2Connection);
        }
    }

    @Override
    public Boolean isConnected() {
        if (h2Connection == null){
            Bukkit.getLogger().warning("[" + plugin.getName() + "/StellarXLib] H2Connection object is null. It must be initialized by method createConnection(data...).");
            return null;
        }
        if (h2Connection.getConnection() != null){
            try {
                return !h2Connection.getConnection().isClosed();
            } catch (SQLException e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public void closeConnection() {
        if (h2Connection.getConnection() != null){
            try {
                h2Connection.getConnection().close();
            } catch (SQLException e) {
                h2Connection.setConnection(null);
            }
        }
    }

    @Override
    public Boolean updateSync(Query query) {
        if (h2Connection == null){
            Bukkit.getLogger().warning("[" + plugin.getName() + "/StellarXLib] H2Connection object is null. It must be initialized by method createConnection(data...).");
            return null;
        }

        try {
            Statement statement = h2Connection.getConnection().createStatement();
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
        if (h2Connection == null){
            Bukkit.getLogger().warning("[" + plugin.getName() + "/StellarXLib] H2Connection object is null. It must be initialized by method createConnection(data...).");
            return;
        }

        super.scheduleRunnable(new Runnable() {
            @Override
            public void run() {
                try {
                    Statement statement = h2Connection.getConnection().createStatement();
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
        if (h2Connection == null){
            Bukkit.getLogger().warning("[" + plugin.getName() + "/StellarXLib] H2Connection object is null. It must be initialized by method createConnection(data...).");
            return null;
        }

        ResultSet set = null;
        try {
            Statement statement = h2Connection.getConnection().createStatement();
            set = statement.executeQuery(query.getQuery());
        } catch (SQLException e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage("[StellarXLib] H2 get data from query error: " + e.getErrorCode());
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
        List<String> queries = QueryUtils.constructQuerySingleValueSet(table, keyColumn, keyValue, objectColumn, objectValue, hasTablePrimaryKey, DatabaseType.H2);
        for(String query : queries) {
            Query q = new Query(query);
            updateSync(q);
        }
    }

    @Override
    public void setObjectAsync(String table, String keyColumn, Object keyValue, String objectColumn, Object objectValue, Boolean hasTablePrimaryKey) {
        List<String> queries = QueryUtils.constructQuerySingleValueSet(table, keyColumn, keyValue, objectColumn, objectValue, hasTablePrimaryKey, DatabaseType.H2);
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
        Query query = new Query(QueryUtils.constructQueryRowRemove(table, keyColumn, keyValue, DatabaseType.H2));
        this.updateSync(query);
    }

    @Override
    public void deleteRowAsync(String table, String keyColumn, String keyValue) {
        Query query = new Query(QueryUtils.constructQueryRowRemove(table, keyColumn, keyValue, DatabaseType.H2));
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
