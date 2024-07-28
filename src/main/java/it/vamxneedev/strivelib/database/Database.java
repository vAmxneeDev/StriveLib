package it.vamxneedev.strivelib.database;

import it.vamxneedev.strivelib.enums.DatabaseType;
import it.vamxneedev.strivelib.utilities.database.Query;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

public interface Database {

    /**
     * <p>Returns the type of database being used.</p>
     * <p>This function retrieves the type of database employed within the application or system.
     * It can be useful for determining whether the application is utilizing MySQL, PostgreSQL, SQLite,
     * MongoDB, or any other supported database type.</p>
     *
     * <p>The returned database type can be utilized for various purposes such as configuring
     * database-specific operations or performing compatibility checks with different database systems.</p>
     * <p>It's important to note that the returned database type may dictate specific behaviors or
     * optimizations in the application logic depending on the database technology being used.</p>
     */
    DatabaseType getDatabaseType();

    /**
    * <p>data values are different depend on type of SQL connection.</p>
    */
    Connection createConnection(String... data);
    Connection getConnection();
    Boolean isConnected();
    void closeConnection();

    /**
     * <p>Send query through connection with synchronization with main thread.
     * It will affect TPS depends of connection quality.</p>
     */
    Boolean updateSync(Query query);
    /**
     * <p>Send query through connection asynchronously in background.
     * It will not affect TPS, but it will work asynchronously.</p>
     *
     * <p>Also, database will be updated in delay and data will not be synchronous.
     * It will be unable to get data from database instantly after update in another line of code.</p>
     */
    void updateAsync(Query query);
    /**
     * <p>Data from ResultSet can be get using ResultSetManager from this plugin.</p>
     */
    ResultSet getResult(Query query);

    /**
    *    <p>Necessary to perform multiple queries in sync way</p>
    */
    void performMultipleQueriesSync(List<Query> listOfQueries);

    /**
     *    <p>Necessary to perform multiple queries in async way</p>
     */
    void performMultipleQueriesAsync(List<Query> listOfQueries);

    /**
     * <p>Keys are used to find correct row.</p>
     * <p>{@code setObjectSync("SQLTable", "player_name", "Esingoteraly", "player_kills", 3);}</p>
     */
    void setObjectSync(String table, String keyColumn, Object keyValue, String objectColumn, Object objectValue, Boolean hasTablePrimaryKey);
    /**
     * <p>Keys are used to find correct row.</p>
     * <p>{@code setObjectAsync("SQLTable", "player_name", "Esingoteraly", "player_kills", 3);}</p>
     */
    void setObjectAsync(String table, String keyColumn, Object keyValue, String objectColumn, Object objectValue, Boolean hasTablePrimaryKey);

    /**
     * <p>Keys are used to find correct row.</p>
     * <p>{@code removeObjectAsync("SQLTable", "player_name", "Esingoteraly", "player_kills");}</p>
     */
    void removeObjectAsync(String table, String keyColumn, Object keyValue, String objectColumn);
    /**
     * <p>Keys are used to find correct row.</p>
     * <p>{@code removeObjectSync("SQLTable", "player_name", "Esingoteraly", "player_kills");}</p>
     */
    void removeObjectSync(String table, String keyColumn, Object keyValue, String objectColumn);

    /**
     * <p>Keys are used to find correct row.</p>
     * <p>{@code getRow("Students", "student_name", "Jonny");}</p>
     */
    ResultSet getRow(String table, String keyColumn, String keyValue);
    /**
     * <p>Keys are used to find correct row.</p>
     * <p>{@code getRow("Students", "student_name", "Jonny", 3);}</p>
     */
    ResultSet getRows(String table, String keyColumn, String keyValue, int limit);

    /**
     * <p>Keys are used to find correct row.</p>
     * <p>{@code deleteRow("Students", "student_course", "math");}</p>
     */
    void deleteRowSync(String table, String keyColumn, String keyValue);
    /**
     * <p>Keys are used to find correct row.</p>
     * <p>{@code deleteRow("Students", "student_course", "math");}</p>
     */
    void deleteRowAsync(String table, String keyColumn, String keyValue);

    /**
     * <p>Keys are used to find correct row.</p>
     * <p>{@code deleteRow("Students", "student_course", "math", 3);}</p>
     */
    void deleteRowsSync(String table, String keyColumn, String keyValue, int limit);
    /**
     * <p>Keys are used to find correct row.</p>
     * <p>{@code deleteRow("Students", "student_course", "math", 3);}</p>
     */
    void deleteRowsAsync(String table, String keyColumn, String keyValue, int limit);
}
