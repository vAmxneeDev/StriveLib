package it.vamxneedev.strivelib.database.sqlite;

import it.vamxneedev.strivelib.database.Database;
import it.vamxneedev.strivelib.database.DatabaseScheduler;
import it.vamxneedev.strivelib.utilities.database.Query;
import it.vamxneedev.strivelib.enums.DatabaseType;
import it.vamxneedev.strivelib.utilities.database.QueryUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SqLiteDatabase extends DatabaseScheduler implements Database {
    private final Plugin plugin;
    private SqLiteConnection sqLiteConnection;

    public SqLiteDatabase(Plugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public DatabaseType getDatabaseType() {
        return DatabaseType.SQLITE;
    }

    @Override
    public Connection createConnection(String... data) {
        try {
            if(sqLiteConnection != null && sqLiteConnection.getConnection() != null && !this.sqLiteConnection.getConnection().isClosed())
                return this.sqLiteConnection.getConnection();
        } catch (SQLException ex) {
            return null;
        }
        SqLiteConnection sqLite = new SqLiteConnection(data[0]);
        sqLiteConnection = sqLite;

        try {
            sqLiteConnection.setConnection(sqLite.openConnection());
            if(sqLiteConnection.getConnection().isClosed())
                return null;
        } catch (SQLException ex) {
            return null;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public Connection createConnection(SqLiteConfig sqLiteConfig){
        return createConnection(sqLiteConfig.getFileLocation());
    }

    @Override
    public Connection getConnection() {
        if(sqLiteConnection == null) {
            Bukkit.getLogger().warning("[" + plugin.getName() + "/StellarXLib] SqLiteConnection object is null. It must be initialized by method createConnection(data...).");
        }
        try {
            if(sqLiteConnection.getConnection() != null && !this.sqLiteConnection.getConnection().isClosed())
                return sqLiteConnection.getConnection();
            else
                return createConnection(sqLiteConnection);
        } catch (SQLException e) {
            return createConnection(sqLiteConnection);
        }
    }

    @Override
    public Boolean isConnected() {
        if (sqLiteConnection == null){
            Bukkit.getLogger().warning("[" + plugin.getName() + "/StellarXLib] SqLiteConnection object is null. It must be initialized by method createConnection(data...).");
            return null;
        }
        if (sqLiteConnection.getConnection() != null){
            try {
                return !sqLiteConnection.getConnection().isClosed();
            } catch (SQLException e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public void closeConnection() {
        if (sqLiteConnection.getConnection() != null){
            try {
                sqLiteConnection.getConnection().close();
            } catch (SQLException e) {
                sqLiteConnection.setConnection(null);
            }
        }
    }

    @Override
    public Boolean updateSync(Query query) {
        if (sqLiteConnection == null){
            Bukkit.getLogger().warning("[" + plugin.getName() + "/StellarXLib] SqLiteConnection object is null. It must be initialized by method createConnection(data...).");
            return null;
        }

        try {
            Statement statement = sqLiteConnection.getConnection().createStatement();
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
        if (sqLiteConnection == null){
            Bukkit.getLogger().warning("[" + plugin.getName() + "/StellarXLib] SqLiteConnection object is null. It must be initialized by method createConnection(data...).");
            return;
        }

        super.scheduleRunnable(new Runnable() {
            @Override
            public void run() {
                try {
                    Statement statement = sqLiteConnection.getConnection().createStatement();
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
        if (sqLiteConnection == null){
            Bukkit.getLogger().warning("[" + plugin.getName() + "/StellarXLib] SqLiteConnection object is null. It must be initialized by method createConnection(data...).");
            return null;
        }

        ResultSet set = null;
        try {
            Statement statement = this.sqLiteConnection.getConnection().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            set = statement.executeQuery(query.getQuery());

        } catch (SQLException var5) {
            var5.printStackTrace();
            Bukkit.getConsoleSender().sendMessage("[StellarXLib] SqLite get data from query error: " + var5.getErrorCode());
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
        List<String> queries = QueryUtils.constructQuerySingleValueSet(table, keyColumn, keyValue, objectColumn, objectValue, hasTablePrimaryKey, DatabaseType.SQLITE);
        for(String query : queries) {
            Query q = new Query(query);
            updateSync(q);
        }
    }

    @Override
    public void setObjectAsync(String table, String keyColumn, Object keyValue, String objectColumn, Object objectValue, Boolean hasTablePrimaryKey) {
        List<String> queries = QueryUtils.constructQuerySingleValueSet(table, keyColumn, keyValue, objectColumn, objectValue, hasTablePrimaryKey, DatabaseType.SQLITE);
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
        Query query = new Query(QueryUtils.constructQueryRowRemove(table, keyColumn, keyValue, DatabaseType.SQLITE));
        this.updateSync(query);
    }

    @Override
    public void deleteRowAsync(String table, String keyColumn, String keyValue) {
        Query query = new Query(QueryUtils.constructQueryRowRemove(table, keyColumn, keyValue, DatabaseType.SQLITE));
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
